package com.keyo.cardapio.service;

import com.keyo.cardapio.model.CardapioCategory;
import com.keyo.cardapio.model.CardapioDate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by mgalvao3 on 10/08/17.
 */

public class CardapioConverter {

    public static List<CardapioDate> convert(String json) {
        try {
            ArrayList<CardapioDate> dates = new ArrayList<>();
            JSONObject root = new JSONObject(json);

            JSONObject items = root.getJSONObject(CardapioDate.KEY_ITEMS);
            Iterator<String> keysItems = items.keys();
            while(keysItems.hasNext()) {
                String keyItem = keysItems.next();

                CardapioDate cardapioDate = new CardapioDate(keyItem);
                ArrayList<CardapioCategory> categories = new ArrayList<>();

                Iterator<String> keysCategory = items.getJSONObject(keyItem).keys();
                while(keysCategory.hasNext()) {
                    String keyCategory = keysCategory.next();
                    JSONArray categoryJson = items.getJSONObject(keyItem).getJSONArray(keyCategory);

                    for(int i=0; i<categoryJson.length(); i++) {
                        CardapioCategory category = new CardapioCategory(keyCategory, categoryJson.getJSONObject(i).getString(CardapioCategory.KEY_NAME),
                             categoryJson.getJSONObject(i).getString(CardapioCategory.KEY_DESCRIPTION), categoryJson.getJSONObject(i).getInt(CardapioCategory.KEY_LIKES),
                             categoryJson.getJSONObject(i).getInt(CardapioCategory.KEY_DISLIKES), categoryJson.getJSONObject(i).getInt(CardapioCategory.KEY_ID));

                        if(i==0) {
                            category.setFirst(true);
                        }

                        categories.add(category);
                    }
                }
                cardapioDate.setCategories(categories);
                dates.add(cardapioDate);
            }

            return dates;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }
}
