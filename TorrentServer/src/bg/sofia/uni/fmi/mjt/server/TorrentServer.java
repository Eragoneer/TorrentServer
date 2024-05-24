package bg.sofia.uni.fmi.mjt.server;

import bg.sofia.uni.fmi.mjt.server.exceptions.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TorrentServer {
    private static final int SERVER_PORT = 4122;
    private static final int MAX_EXECUTOR_THREADS = 10;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
             ExecutorService executor = Executors.newFixedThreadPool(MAX_EXECUTOR_THREADS)) {
            Logger.deleteLogger();
            Logger.createLogger();
            Socket clientSocket;
            while (true) {
                clientSocket = serverSocket.accept();
                System.out.println("Accepted connection request from client " +
                        clientSocket.getInetAddress() + " Port: " + clientSocket.getPort());
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                executor.execute(clientHandler);
            }
        } catch (IOException e) {
            Logger.appendLogger(e.getStackTrace());
            throw new RuntimeException("Issue with Thread pool", e);
        }
    }

}
