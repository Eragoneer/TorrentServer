package bg.sofia.uni.fmi.mjt.client.peertopeer;

import bg.sofia.uni.fmi.mjt.client.exceptions.ClientLogger;
import bg.sofia.uni.fmi.mjt.peerinfo.PeerInfo;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Path;

public class PeerToPeerClient implements Runnable {
    private PeerInfo peerInfo;
    private String localPath;
    private String remotePath;

    private static final Integer BUFFER_SIZE = 1024;

    public PeerToPeerClient(PeerInfo peerInfo, String localPath, String remotePath) {
        this.peerInfo = peerInfo;
        this.localPath = localPath;
        this.remotePath = remotePath;

    }

    public void writeToFile(FileOutputStream out, BufferedInputStream in)
            throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        readInputStream(out, in, buffer);
    }

    public void readInputStream(FileOutputStream out, BufferedInputStream in, byte[] buffer) throws IOException {
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        out.flush();
    }

    @Override
    public void run() {
        //InetAddress.getByName(peerInfo.getIP()) exchange to connect via ip
        try (Socket socket = new Socket("localhost", peerInfo.getPort());
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
             FileOutputStream fos = new FileOutputStream(Path.of(localPath).toFile(), false)
        ) {

            out.println(remotePath);
            writeToFile(fos, in);

        } catch (IOException e) {
            ClientLogger.appendLogger(e.getStackTrace());
            System.out.println("There is a problem with the network communication");
          //  throw new RuntimeException("There is a problem with the network communication", e);
        }
    }
}
