package com.keyo.cardapio.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mgalvao3 on 09/03/17.
 */

public class CardapioList {
    @SerializedName("items")
    private List<Cardapio> items;

    public List<Cardapio> items() {
        return items != null ? new ArrayList<>(items) : null;
    }
}
