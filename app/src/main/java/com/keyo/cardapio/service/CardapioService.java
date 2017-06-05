package com.keyo.cardapio.service;

import com.keyo.cardapio.model.CardapioList;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by mgalvao3 on 09/03/17.
 */

public interface CardapioService {

    //@GET("/api/json/get/4yi-ycGeX")
    @GET("/cardapio.json")
    Call<CardapioList> getCardapio();
}
