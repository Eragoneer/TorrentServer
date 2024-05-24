package bg.sofia.uni.fmi.mjt.client.peertopeer;

import bg.sofia.uni.fmi.mjt.client.exceptions.ClientLogger;
import bg.sofia.uni.fmi.mjt.server.exceptions.Logger;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;

public class PeerToPeerClientHandler implements Runnable {

    private Socket socket;
    private static final Integer BUFFER_SIZE = 1024;

    public PeerToPeerClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void writeToFile(FileInputStream fis, BufferedOutputStream out)
            throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        readInputStream(fis, out, buffer);
    }

    public void readInputStream(FileInputStream fis, BufferedOutputStream out, byte[] buffer) throws IOException {
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
            ClientLogger.appendLogger("Bytes Read:" + bytesRead);
        }
        out.flush();
    }

    @Override
    public void run() {
        try (BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String inputLine;
            if ((inputLine = in.readLine()) != null) {
                File file = Path.of(inputLine).toFile();
                if (file.exists() && file.isFile()) {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        writeToFile(fis, out);
                    } catch (FileNotFoundException e) {
                        Logger.appendLogger(e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            ClientLogger.appendLogger(e.getStackTrace());
        } finally {
            try {
                socket.close();
                System.out.println("File has been transfered");
            } catch (IOException e) {
                System.out.println("There was an issue with closing the connection");
                ClientLogger.appendLogger(e.getStackTrace());
            }
        }
    }
}
