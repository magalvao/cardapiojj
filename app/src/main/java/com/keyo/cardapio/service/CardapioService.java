package com.keyo.cardapio.service;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by mgalvao3 on 09/03/17.
 */

public interface CardapioService {

    @GET("/cardapiojj")
    Call<String> getCardapio();
}
