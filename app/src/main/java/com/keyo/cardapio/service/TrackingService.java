package com.keyo.cardapio.service;

import com.keyo.cardapio.lojinha.model.Track;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by mgalvao3 on 17/06/17.
 */

public interface TrackingService {
    @GET("/track")
    Call<Track> getTracking();
}
