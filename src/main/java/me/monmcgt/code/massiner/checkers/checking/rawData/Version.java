package me.monmcgt.code.massiner.checkers.checking.rawData;

import com.google.gson.annotations.SerializedName;

public class Version {
    @SerializedName("name")
    private String name;
    @SerializedName("protocol")
    private int protocol;

    public void setName(String a) {
        this.name = a;
    }

    public String getName() {
        return this.name;
    }
}


