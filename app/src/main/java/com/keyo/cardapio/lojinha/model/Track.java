package com.keyo.cardapio.lojinha.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by mgalvao3 on 17/06/17.
 */

public class Track implements Serializable {
    @SerializedName("lastTrackNumber")
    private String lastTrackNumber;

    public Track(final String lastTrackNumber) {
        this.lastTrackNumber = lastTrackNumber;
    }

    public String getLastTrackNumber() {
        return lastTrackNumber;
    }
}
