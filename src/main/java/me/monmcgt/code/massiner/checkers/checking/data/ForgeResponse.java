package me.monmcgt.code.massiner.checkers.checking.data;

import com.google.gson.annotations.SerializedName;
import me.monmcgt.code.massiner.checkers.checking.rawData.Description;
import me.monmcgt.code.massiner.checkers.checking.rawData.ForgeModInfo;
import me.monmcgt.code.massiner.checkers.checking.rawData.Players;
import me.monmcgt.code.massiner.checkers.checking.rawData.Version;


public class ForgeResponse {
    @SerializedName("description")
    private Description description;
    @SerializedName("players")
    private Players players;
    @SerializedName("version")
    private Version version;
    @SerializedName("modinfo")
    private ForgeModInfo modinfo;

    public FinalResponse toFinalResponse() {
        this.version.setName(this.version.getName() + " FML with " + this.modinfo.getNMods() + " mods");
        return new FinalResponse(this.players, this.version, "", this.description.getText());
    }
}


