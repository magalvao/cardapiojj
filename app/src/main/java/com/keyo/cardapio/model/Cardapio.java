package com.keyo.cardapio.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mgalvao3 on 09/03/17.
 */

public class Cardapio implements Serializable {
    @SerializedName("id")
    private int  id;
    @SerializedName("date")
    private Date date;
    @SerializedName("optionName")
    private String optionName;
    @SerializedName("description")
    private String description;
    @SerializedName("likes")
    private int likes;
    @SerializedName("dislikes")
    private int dislikes;


    public Cardapio(int id, Date date, String optionName, String description, int likes, int
            dislikes) {
        this.id = id;
        this.date = date;
        this.optionName = optionName;
        this.description = description;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public String getOptionName() {
        return optionName;
    }

    public String getDescription() {
        return description;
    }

    public int getLikes() {
        return likes;
    }

    public int getDislikes() {
        return dislikes;
    }
}
