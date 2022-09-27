package me.monmcgt.code.massiner.checkers.checking;


public class PingOptions {
    private String hostname;
    private int port;
    private int timeout;

    public PingOptions(String hostname, int port, int timeout) {
        this.hostname = hostname;
        this.port = port;
        this.timeout = timeout;
    }


    public PingOptions setHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    public PingOptions setPort(int port) {
        this.port = port;
        return this;
    }

    public PingOptions setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }


    String getHostname() {
        return this.hostname;
    }

    int getPort() {
        return this.port;
    }

    public int getTimeout() {
        return this.timeout;
    }
}


