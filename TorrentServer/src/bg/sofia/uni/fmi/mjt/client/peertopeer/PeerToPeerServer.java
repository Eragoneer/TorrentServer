package bg.sofia.uni.fmi.mjt.client.peertopeer;

import bg.sofia.uni.fmi.mjt.client.exceptions.ClientLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PeerToPeerServer implements Runnable {
    private static final int MAX_EXECUTOR_THREADS = 10;

    private int serverPort;

    public PeerToPeerServer(int port) {
        serverPort = port;
    }

    private void setServerPort(int port) {
        this.serverPort = port;
    }

    public Integer getPort() {
        return this.serverPort;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(serverPort);
             ExecutorService executor = Executors.newFixedThreadPool(MAX_EXECUTOR_THREADS)) {
            setServerPort(serverSocket.getLocalPort());
            System.out.println("ClientServer LocalPort: " + serverSocket.getLocalPort());
            Socket clientSocket;
            while (true) {
                clientSocket = serverSocket.accept();
                System.out.println("Accepted connection request from client "
                        + clientSocket.getInetAddress() + " Port: " + clientSocket.getPort());
                PeerToPeerClientHandler clientHandler = new PeerToPeerClientHandler(clientSocket);
                executor.execute(clientHandler);
            }
        } catch (IOException e) {
            ClientLogger.appendLogger(e.getStackTrace());
            System.out.println("Issue with the thread pool");
       //     throw new RuntimeException("Issue with Thread pool", e);
        }
    }
}
