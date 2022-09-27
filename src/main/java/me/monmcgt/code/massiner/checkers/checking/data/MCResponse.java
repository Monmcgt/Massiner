package me.monmcgt.code.massiner.checkers.checking.data;


import com.google.gson.annotations.SerializedName;
import me.monmcgt.code.massiner.checkers.checking.rawData.Players;
import me.monmcgt.code.massiner.checkers.checking.rawData.Version;

class MCResponse {
    @SerializedName("players")
    Players players;

    @SerializedName("version")
    Version version;

    @SerializedName("favicon")
    String favicon;
}


