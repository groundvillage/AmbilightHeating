package de.htwg_konstanz.in.uc_lab.ambilightcontrol.server;


public class ServerDAO {

    private final String host;
    private final String port;

    public ServerDAO(String host, String port){
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }
}
