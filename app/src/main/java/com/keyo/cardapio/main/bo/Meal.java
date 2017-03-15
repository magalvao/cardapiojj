package com.keyo.cardapio.main.bo;

/**
 * Created by mgalvao3 on 02/02/17.
 */
public class Meal {
    String mOption;
    String mName;
    int mLikes;
    int mDislikes;

    public Meal(String mOption, String mName, int mLikes, int mDislikes) {
        this.mOption = mOption;
        this.mName = mName;
        this.mLikes = mLikes;
        this.mDislikes = mDislikes;
    }

    public String getOption() {
        return mOption;
    }

    public String getName() {
        return mName;
    }

    public int getLikes() {
        return mLikes;
    }

    public int getDislikes() {
        return mDislikes;
    }
}
