
package me.monmcgt.code.massiner.checkers.checking.data;


import me.monmcgt.code.massiner.checkers.checking.rawData.Players;
import me.monmcgt.code.massiner.checkers.checking.rawData.Version;

public class FinalResponse extends MCResponse {
    private final String description;

    public FinalResponse(Players players, Version version, String favicon, String description) {
        this.description = description;
        this.favicon = favicon;
        this.players = players;
        this.version = version;
    }

    public Players getPlayers() {
        return this.players;
    }

    public Version getVersion() {
        return this.version;
    }

    public String getDescription() {
        return this.description;
    }

    public String getFavicon() {
        return this.favicon;
    }
}


