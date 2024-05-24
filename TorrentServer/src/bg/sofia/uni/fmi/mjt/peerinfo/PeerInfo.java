package bg.sofia.uni.fmi.mjt.peerinfo;

import java.util.Objects;

public class PeerInfo {

    private String host;
    private String ip;
    private Integer port;

    public PeerInfo(String host, String ip, Integer port) {
        initialise(host, ip, port);
    }

    public PeerInfo() {
        initialise("", "", 0);
    }

    private void initialise(String host, String ip, Integer port) {
        setHost(host);
        setIp(ip);
        setPort(port);
    }

    public String getHost() {
        return host;
    }

    public String getIP() {
        return ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setHost(String host) {
        if (host == null) {
            throw new IllegalArgumentException("Host can't be null or empty");
        }
        this.host = host;
    }

    public void setIp(String ip) {
        if (host == null) {
            throw new IllegalArgumentException("ip can't be null or empty");
        }
        this.ip = ip;
    }

    public void setPort(Integer port) {
        if (port < 0) {
            throw new IllegalArgumentException("Port can't be negative");
        }
        this.port = port;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof PeerInfo pi) {
            return host.equals(pi.host) && ip.equals(pi.ip) && port.compareTo(pi.port) == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, ip, port);
    }

    @Override
    public String toString() {
        return String.format("[Host = %s, IP = %s, Port = %d]", host, ip, port);
    }
}
