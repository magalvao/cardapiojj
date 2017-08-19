package com.keyo.cardapio.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by mgalvao3 on 10/08/17.
 */

public class CardapioCategory implements Serializable {

    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_LIKES = "likes";
    public static final String KEY_DISLIKES = "dislikes";
    public static final String KEY_ID = "id";

    public static final String CATEGORY_HEALTH = "BEM-ESTAR";
    public static final String CATEGORY_HOMEMADE = "CASEIRA";
    public static final String CATEGORY_CHOICE = "SUA ESCOLHA";

    public String category;
    private boolean isFirst = false;

    @SerializedName(KEY_NAME)
    public String name;

    @SerializedName(KEY_DESCRIPTION)
    public String description;

    @SerializedName(KEY_LIKES)
    public int likes;

    @SerializedName(KEY_DISLIKES)
    public int dislikes;

    @SerializedName(KEY_ID)
    public int id;

    public CardapioCategory(final String category, final String name, final String description,
                            final int likes, final int dislikes, final int id) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.likes = likes;
        this.dislikes = dislikes;
        this.id = id;
    }

    public String getName() {
        return name;
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

    public int getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(final boolean first) {
        isFirst = first;
    }
}
