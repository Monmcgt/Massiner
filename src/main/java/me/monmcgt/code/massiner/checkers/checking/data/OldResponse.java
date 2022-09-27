package me.monmcgt.code.massiner.checkers.checking.data;

import com.google.gson.annotations.SerializedName;

public class OldResponse
        extends MCResponse {
    public FinalResponse toFinalResponse() {
        return new FinalResponse(this.players, this.version, this.favicon, this.description);
    }

    @SerializedName("description")
    private String description;
}


