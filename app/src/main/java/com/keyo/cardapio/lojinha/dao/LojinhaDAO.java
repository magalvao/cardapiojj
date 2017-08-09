package com.keyo.cardapio.lojinha.dao;

import android.support.annotation.NonNull;
import android.util.Log;

import com.keyo.cardapio.dao.AppPreferences;
import com.keyo.cardapio.lojinha.model.Order;
import com.keyo.cardapio.lojinha.model.Track;
import com.keyo.cardapio.service.TrackingService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mgalvao3 on 17/06/17.
 */

public class LojinhaDAO {
    private final TrackingService mService;
    private final AppPreferences mAppPreferences;

    public LojinhaDAO(@NonNull final String baseUrl, @NonNull final AppPreferences appPreferences) {
        mAppPreferences = appPreferences;

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mService = retrofit.create(TrackingService.class);
    }

    public Track fetchTracking() {
        try {
            final Track result = mService.getTracking().execute().body();
            if (result != null) {
                return result;
            }
        } catch (IOException e) {
            Log.e("Tracking", "IO Error fetching tracking number.", e);

            return null;
        }

        return null;
    }

    public void saveTrackNumber(final Track track) {
        mAppPreferences.saveTrackingNumber(track.getLastTrackNumber());
    }

    public List<Order> fetchPedidos() {
        return mAppPreferences.fetchPedidos();
    }

    public void saveOrder(final String value) {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order(value));
        orders.addAll(mAppPreferences.fetchPedidos());

        Collections.sort(orders, Collections.reverseOrder());

        mAppPreferences.updateOrders(orders);
    }

    public void deleteOrder(final String value) {
        List<Order> orders = new ArrayList<>(mAppPreferences.fetchPedidos());

        for (Iterator<Order> iter = orders.listIterator(); iter.hasNext(); ) {
            Order o = iter.next();
            if (o.getNumber().equals(value)) {
                iter.remove();
            }
        }

        mAppPreferences.updateOrders(orders);
    }
}
