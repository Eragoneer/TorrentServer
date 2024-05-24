package bg.sofia.uni.fmi.mjt.server;

import bg.sofia.uni.fmi.mjt.peerinfo.PeerInfo;
import bg.sofia.uni.fmi.mjt.repository.FileRepository;
import bg.sofia.uni.fmi.mjt.server.exceptions.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientHandler implements Runnable {
    public static Set<ClientHandler> clients = new LinkedHashSet<>();
    private Socket socket;
    private FileRepository userFilePaths;
    private PeerInfo peerInfo;

    public static final String REGISTER_MATCHER = "\\bregister\\s\\S+\\s(\\S+,\\s)*\\S+\\b";
    public static final String UNREGISTER_MATCHER = "\\bunregister\\s\\S+\\s(\\S+,\\s)*\\S+\\b";
    public static final String LIST_ALL_MATCHER = "\\blist-files\\b";
    public static final String UPDATE_MATCHER = "\\bupdate\\b";
    public static final String PEER_INFO_MATCHER = "\\bpeer\\s\\S+\\s\\S+\\b";

    public ClientHandler(Socket socket) {
        peerInfo = new PeerInfo();
        this.socket = socket;
        userFilePaths = new FileRepository();
    }

    public PeerInfo getPeerInfo() {
        return peerInfo;
    }

    private boolean usernameExists(String username) {
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.peerInfo.getHost() == null) {
                continue;
            }
            if (username.equals(clientHandler.peerInfo.getHost())) {
                return true;
            }
        }
        return false;
    }

    private void askClientForUsername(BufferedReader in, PrintWriter out) throws IOException {
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            System.out.println("Username received from client: " + inputLine);
            if (usernameExists(inputLine) || inputLine.isBlank()) {
                out.println(userFilePaths.exists(inputLine));
                continue;
            }
            peerInfo.setHost(inputLine);
            out.println("your username is: " + peerInfo.getHost());
            break;
        }
    }

    public boolean matchCommands(String inputLine, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(inputLine);
        return matcher.matches();
    }

    public List<String> formatCommandLine(String inputLine) {
        return Arrays.stream(inputLine.split(",*\\s"))
                .skip(1)
                .toList();
    }

    public String commandListFiles() {
        Iterator<ClientHandler> it = clients.iterator();
        StringBuilder stringBuilder = new StringBuilder();
        if (it.hasNext()) {
            ClientHandler clientHandler = it.next();
            stringBuilder.append(clientHandler.userFilePaths.listFiles(clientHandler.peerInfo.getHost()));
        }
        while (it.hasNext()) {
            ClientHandler clientHandler = it.next();
            stringBuilder.append(";")
                    .append(clientHandler.userFilePaths.listFiles(clientHandler.peerInfo.getHost()));
        }
        return stringBuilder.toString();
    }

    public String commandUpdate() {
        Iterator<ClientHandler> it = clients.iterator();
        StringBuilder stringBuilder = new StringBuilder();
        if (it.hasNext()) {
            ClientHandler clientHandler = it.next();
            stringBuilder.append(clientHandler.peerInfo.getHost())
                    .append(" - ").append(clientHandler.peerInfo.getIP())
                    .append(":")
                    .append(clientHandler.peerInfo.getPort());
        }
        while (it.hasNext()) {
            ClientHandler clientHandler = it.next();
            stringBuilder.append(";").append(clientHandler.peerInfo.getHost())
                    .append(" - ").append(clientHandler.peerInfo.getIP())
                    .append(":")
                    .append(clientHandler.peerInfo.getPort());
        }
        return stringBuilder.toString();
    }

    public String commandInitPeerInfo(String inputLine) {
        List<String> list = formatCommandLine(inputLine);
        peerInfo.setIp(list.get(0));
        peerInfo.setPort(Integer.parseInt(list.get(1)));
        return String.format("HOST = %s, IP = %s, PORT = %s",
                peerInfo.getHost(), peerInfo.getIP(), peerInfo.getPort());
    }

    private boolean authenticateHost(String host) {
        return host.equals(peerInfo.getHost());
    }

    public String commandRegister(String inputLine) {
        List<String> list = formatCommandLine(inputLine);
        if (authenticateHost(list.get(0))) {
            return userFilePaths.register(list.get(0),
                    list.subList(1, list.size())).toString();
        }
        return userFilePaths.unauthorized(list.get(0)).toString();

    }

    public String commandUnregister(String inputLine) {
        List<String> list = formatCommandLine(inputLine);
        if (authenticateHost(list.get(0))) {
            return userFilePaths.unregister(list.get(0),
                    list.subList(1, list.size())).toString();
        }
        return userFilePaths.unauthorized(list.get(0)).toString();

    }

    public String commands(String inputLine) {
        if (matchCommands(inputLine, REGISTER_MATCHER)) {
            return commandRegister(inputLine);
        }
        if (matchCommands(inputLine, UNREGISTER_MATCHER)) {
            return commandUnregister(inputLine);
        }
        if (matchCommands(inputLine, LIST_ALL_MATCHER)) {
            return commandListFiles();
        }
        if (matchCommands(inputLine, UPDATE_MATCHER)) {
            return commandUpdate();
        }
        if (matchCommands(inputLine, PEER_INFO_MATCHER)) {
            return commandInitPeerInfo(inputLine);
        }
        return "unknown command";
    }

    @Override
    public void run() {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String inputLine;
            askClientForUsername(in, out);
            clients.add(this);
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Message received from client: " + inputLine);
                String output = commands(inputLine);
                out.println(output);
            }
        } catch (IOException e) {
            System.out.println("There is an issue with internet connectivity");
            Logger.appendLogger(e.getStackTrace());
        } finally {
            try {
                clients.remove(this);
                socket.close();
            } catch (IOException e) {
                Logger.appendLogger(e.getStackTrace());
                System.out.println(String.format("There was an issue when disconnecting client %s %s %s",
                        peerInfo.getHost(), peerInfo.getIP(), peerInfo.getPort()));
            }
        }
    }
}
