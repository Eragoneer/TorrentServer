package bg.sofia.uni.fmi.mjt.client;

import bg.sofia.uni.fmi.mjt.client.exceptions.ClientLogger;
import bg.sofia.uni.fmi.mjt.client.peertopeer.PeerToPeerClient;
import bg.sofia.uni.fmi.mjt.client.peertopeer.PeerToPeerServer;
import bg.sofia.uni.fmi.mjt.client.update.RepeatUpdate;
import bg.sofia.uni.fmi.mjt.peerinfo.PeerInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {
    private static final int SERVER_PORT = 4122;
    private static final String DECLINE_MATCHER = "\\s*quit\\s*";
    private String username;
    private List<PeerInfo> peers;
    private PeerInfo peer;
    private static final Integer THREAD_SLEEP_SECONDS = 300;
    private static final Integer THREAD_DELAY = 1;
    public static final String DOWNLOAD_MATCHER = "\\bdownload\\s\\S+\\s\\S+\\s\\S+\\b";
    public static final Integer REMOTE_FILE_INDEX = 3 ;
    public static final Integer LOCAL_FILE_INDEX = 2;

    public Client(String username) {
        this.username = username;
        peers = new ArrayList<>();
    }

    public static String enterUsername(PrintWriter writer, BufferedReader reader, Scanner scanner)
            throws IOException {
        String reply = "";
        while (true) {
            System.out.println("Please Enter your username:");
            System.out.print("=> ");
            String message = scanner.nextLine();
            if (message.isEmpty()) {
                continue;
            }
            writer.println(message);
            reply = reader.readLine();
            System.out.println(reply);
            if (!reply.equalsIgnoreCase("username exists")) {
                break;
            }
        }
        return reply;
    }

    public static void sendIPAndPortToServer(PrintWriter writer, BufferedReader reader,
                                             String localIP, Integer localServerPort)
            throws IOException {
        String reply;
        writer.println("peer " + localIP + ", " + localServerPort);
        reply = reader.readLine();
        System.out.println(reply);
    }

    public static boolean downloadFromPeer(String message) {
        Pattern pattern = Pattern.compile(DOWNLOAD_MATCHER);
        Matcher matcher = pattern.matcher(message);
        return matcher.matches();
    }

    public static boolean disconnectClient(String message) {
        Pattern pattern = Pattern.compile(DECLINE_MATCHER);
        Matcher matcher = pattern.matcher(message);
        return matcher.matches();
    }

    private PeerInfo getPeer(String line) {
        String[] tokens = line.split(" ");
        String username = tokens[1];
        for (PeerInfo peerInfo : peers) {
            if (peerInfo.getHost().equalsIgnoreCase(username)) {
                return peerInfo;
            }
        }
        return null;
    }

    private static void connectPeerClient(String line, Client client) {
        PeerInfo peerInfo = client.getPeer(line);
        String[] tokens = line.split("\\s+");
        String localPath = tokens[LOCAL_FILE_INDEX];
        String remotePath = tokens[REMOTE_FILE_INDEX];
        if (peerInfo != null) {
            Thread.ofVirtual().start(new PeerToPeerClient(peerInfo, localPath, remotePath));
        } else {
            System.out.println("Peer either not registered or not updated");
        }
    }

    public static void runningClient(Scanner scanner, PrintWriter writer, BufferedReader reader, Client client)
            throws IOException {
        while (true) {
            System.out.print("=> ");
            String message = scanner.nextLine();
            if (disconnectClient(message)) {
                System.out.println("Disconnected from the server");
                break;
            }
            if (downloadFromPeer(message)) {
                connectPeerClient(message, client);
                continue;
            }
            writer.println(message);
            String reply = reader.readLine();
            System.out.println(reply);
        }
    }

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", SERVER_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in);
             ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1)) {

            System.out.println("Connected to the server.");
            Client client = new Client(enterUsername(writer, reader, scanner));
            PeerToPeerServer p2pServer = new PeerToPeerServer(0);
            Thread.ofVirtual().start(p2pServer);
            while (p2pServer.getPort() == 0) {

            }
            sendIPAndPortToServer(writer, reader, socket.getInetAddress().toString(), p2pServer.getPort());
           // System.out.println("My local internet port is:" + p2pServer.getPort());
            scheduler.scheduleAtFixedRate(new RepeatUpdate(client.peers, socket)
                    , THREAD_DELAY, THREAD_SLEEP_SECONDS, TimeUnit.SECONDS);
            client.peer = new PeerInfo(client.username, socket.getInetAddress().toString(), p2pServer.getPort());
            runningClient(scanner, writer, reader, client);
        } catch (IOException e) {
            ClientLogger.appendLogger(e.getStackTrace());
            System.out.println("There is a problem with the network communication");
            throw new RuntimeException("There is a problem with the network communication", e);
        }
    }
}
