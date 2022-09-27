package me.monmcgt.code.massiner.checkers.checking;


import me.monmcgt.code.massiner.checkers.checking.rawData.Players;
import me.monmcgt.code.massiner.utilities.Time;

public class Results {
    private Time time;

    private String address;
    private String ip;
    private int port;

    private boolean working = false;

    private String version;
    private String favicon;
    private String motd;
    private Players players;

    private Exception exception;

    public Results(String address, String ip, int port, String version, String favicon, String motd, Players players) {
        this.address = address;
        this.ip = ip;
        this.port = port;
        this.version = version;
        this.favicon = favicon;
        this.motd = motd;
        this.players = players;

        this.working = true;

        this.time = Time.now();
    }

    public Results(String address, String ip, int port, Exception exception) {
        this.address = address;
        this.ip = ip;
        this.port = port;
        this.exception = exception;

        this.working = false;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isWorking() {
        return working;
    }

    public void setWorking(boolean working) {
        this.working = working;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFavicon() {
        return favicon;
    }

    public void setFavicon(String favicon) {
        this.favicon = favicon;
    }

    public String getMotd() {
        return motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public Players getPlayers() {
        return players;
    }

    public void setPlayers(Players players) {
        this.players = players;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }
}
