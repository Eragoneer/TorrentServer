package bg.sofia.uni.fmi.mjt.client.update;

import bg.sofia.uni.fmi.mjt.client.exceptions.ClientLogger;
import bg.sofia.uni.fmi.mjt.peerinfo.PeerInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class RepeatUpdate implements Runnable {
    private List<PeerInfo> peerInfos;
    private Socket socket;

    public RepeatUpdate(List<PeerInfo> peerInfos, Socket socket) {
        this.peerInfos = peerInfos;
        this.socket = socket;
    }

    private void updatePeerInfos(String line) {
        List<PeerInfo> newList = new ArrayList<>();
        String[] users = line.split(";");
        for (String user : users) {
            String[] tokens = user.split(" - ");
            String[] socket = tokens[1].split(":");
            newList.add(new PeerInfo(tokens[0], socket[0], Integer.parseInt(socket[1])));
        }
        peerInfos.clear();
        peerInfos.addAll(newList);
       // System.out.println(peerInfos);
    }

    @Override
    public void run() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println("update");
            String reply = in.readLine();
            updatePeerInfos(reply);
        } catch (IOException e) {
            ClientLogger.appendLogger(e.getStackTrace());
        }
    }
}
