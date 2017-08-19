package com.keyo.cardapio.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by mgalvao3 on 10/08/17.
 */

public class CardapioDate implements Serializable {

    public static String KEY_ITEMS = "items";
    private Date                   date;
    private List<CardapioCategory> categories;

    public CardapioDate(final String date) {

        if(!isValid(date)) {
            throw new NumberFormatException("Invalid date format");
        }

        Calendar cal = Calendar.getInstance();
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(5, 7)) - 1;
        int day = Integer.parseInt(date.substring(8, 10));
        cal.set(year, month, day);

        this.date = new Date(cal.getTimeInMillis());
    }

    private boolean isValid(final String date) {
        if(date.length() != 10) {
            return false;
        }

        boolean year = Character.isDigit(date.charAt(0))
                && Character.isDigit(date.charAt(1))
                && Character.isDigit(date.charAt(2))
                && Character.isDigit(date.charAt(3));
        boolean month = Character.isDigit(date.charAt(5)) && Character.isDigit(date.charAt(6));
        boolean day = Character.isDigit(date.charAt(8)) && Character.isDigit(date.charAt(9));
        boolean separators = date.charAt(4) == '-' && date.charAt(7) == '-';

        return year && month && day && separators;
    }

    public void setCategories(final List<CardapioCategory> categories) {
        this.categories = new ArrayList<>(categories);
    }

    public List<CardapioCategory> getCategories() {
        return new ArrayList<>(categories);
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        StringBuilder content = new StringBuilder();
        String currentCategory = "";
        boolean isFirst = true;

        for (CardapioCategory category: categories) {

            if(!currentCategory.equals(category.category)) {

                if(!isFirst) {
                    content.append("<br /><br />");
                    isFirst = false;
                }

                content.append("<b>-").append(category.getCategory())
                        .append("-</b><br />");
            }

            content.append(category.getName()).append(": ");
            content.append(category.getDescription());
            content.append("<br />");
        }

        return content.toString();
    }
}
