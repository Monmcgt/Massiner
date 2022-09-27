package me.monmcgt.code.massiner.checkers.checking.data;

import com.google.gson.annotations.SerializedName;
import me.monmcgt.code.massiner.checkers.checking.rawData.Description;
import me.monmcgt.code.massiner.checkers.checking.rawData.Players;
import me.monmcgt.code.massiner.checkers.checking.rawData.Version;


public class NewResponse
        extends MCResponse {
    @SerializedName("description")
    private final Description description;

    public void setVersion(String a) {
        this.version.setName(a);
    }

    public NewResponse() {
        this.description = new Description();
        this.players = new Players();
        this.version = new Version();
    }

    public Description getDescription() {
        return this.description;
    }

    public FinalResponse toFinalResponse() {
        return new FinalResponse(this.players, this.version, this.favicon, this.description.getText());
    }
}


