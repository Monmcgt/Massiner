package me.monmcgt.code.massiner.checkers.checking.data;

import com.google.gson.annotations.SerializedName;
import me.monmcgt.code.massiner.checkers.checking.rawData.ExtraDescription;


public class ExtraResponse
        extends MCResponse {
    @SerializedName("description")
    private ExtraDescription description;

    public FinalResponse toFinalResponse() {
        return new FinalResponse(this.players, this.version, this.favicon, this.description.getText());
    }
}


