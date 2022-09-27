package me.monmcgt.code.massiner.checkers.checking.rawData;

import com.google.gson.annotations.SerializedName;

public class Player {
    @SerializedName("name")
    private String name;
    @SerializedName("id")
    private String id;

    public Player() {
    }

    public Player(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }
}


