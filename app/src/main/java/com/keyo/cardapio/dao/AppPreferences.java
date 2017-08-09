package com.keyo.cardapio.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.keyo.cardapio.lojinha.model.Order;
import com.keyo.cardapio.model.Cardapio;

import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mgalvao3 on 10/03/17.
 */

public class AppPreferences {

    public static final String LAST_LIST = "LAST_LIST";
    public static final String HOUR = "HOUR";
    public static final int DEFAULT_HOUR = 12;
    public static final int DEFAULT_MINUTE = 0;
    public static final String MINUTE = "MINUTE";
    public static final String NOTIFICATIONS_ALLOWED = "NOTIFICATIONS_ALLOWED";
    public static final int LAST_FEATURE_CODE = 1;
    public static final String SHOW_FEATURE_POPUP = "SHOW_FEATURE_POPUP";
    public static final String ORDER = "ORDERS";
    private static final String PREF_NAME = "JJ_CARDAPIO";
    private static final String LAST_TRACKING = "LAST_TRACKING";
    private static final String LAST_TRACKING_TIME = "LAST_TRACKING_TIME";
    private final SharedPreferences mSharedPreferences;

    public AppPreferences(@NonNull final Context context) {
        mSharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveCardapio(List<Cardapio> list) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(list, ArrayList.class);
        edit.putString(LAST_LIST, json);
        edit.apply();
    }

    @Nullable
    public List<Cardapio> loadCardapio() {
        String json = mSharedPreferences.getString(LAST_LIST, null);

        if (json == null || json.equals("null")) {
            return new ArrayList<>();
        }

        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Cardapio>>() {
        }.getType();
        return gson.fromJson(json, listType);
    }

    public int getHourToNotify() {
        return mSharedPreferences.getInt(HOUR, DEFAULT_HOUR);
    }

    public int getMinuteToNitify() {
        return mSharedPreferences.getInt(MINUTE, DEFAULT_MINUTE);
    }

    public boolean isNotificationAllowed() {
        return mSharedPreferences.getBoolean(NOTIFICATIONS_ALLOWED, false);
    }

    public void saveHourAndMinute(final int hour, final int minute) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putInt(HOUR, hour);
        edit.putInt(MINUTE, minute);
        edit.apply();
    }

    public void isChecked(final boolean isChecked) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putBoolean(NOTIFICATIONS_ALLOWED, isChecked);
        edit.apply();
    }

    public void saveTrackingNumber(final String lastTrackNumber) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();

        DateTime dt = new DateTime();
        edit.putString(LAST_TRACKING, lastTrackNumber);
        edit.putLong(LAST_TRACKING_TIME, dt.getMillis());
        edit.apply();
    }

    public String getLastTrackingNumber() {
        return mSharedPreferences.getString(LAST_TRACKING, "-");
    }

    public long getLastTrackingTime() {
        return mSharedPreferences.getLong(LAST_TRACKING_TIME, -1);
    }

    public int getPopupFeatureValue() {
        return mSharedPreferences.getInt(SHOW_FEATURE_POPUP, -1);
    }

    public void setPopupFeatureValue(final int featureOrdernumber) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putInt(SHOW_FEATURE_POPUP, featureOrdernumber);
        edit.apply();
    }

    public List<Order> fetchPedidos() {
        String pedidosString = mSharedPreferences.getString(ORDER, null);
        final ArrayList<Order> result = new ArrayList<>();
        if (pedidosString != null && !"".equals(pedidosString)) {
            String[] values = pedidosString.split(";");
            for (final String value : values) {
                result.add(new Order(value));
            }
        }
        return result;
    }

    public void updateOrders(@NonNull  final List<Order> orders) {
        String finalOrders = "";
        for (int i = 0; i < orders.size(); i++) {
            finalOrders = finalOrders.concat(orders.get(i).getNumber());
            finalOrders = finalOrders.concat(";");
        }
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putString(ORDER, finalOrders);
        edit.apply();
    }
}
