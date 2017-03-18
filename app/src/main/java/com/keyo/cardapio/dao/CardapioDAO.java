package com.keyo.cardapio.dao;

import android.support.annotation.NonNull;
import android.util.Log;

import com.keyo.cardapio.model.Cardapio;
import com.keyo.cardapio.model.CardapioList;
import com.keyo.cardapio.service.CardapioService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mgalvao3 on 09/03/17.
 */

public class CardapioDAO {
    private final CardapioService mService;

    private CardapioErrorListener mErrorListener;

    public CardapioDAO(@NonNull final String baseUrl) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .readTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create()).build();
        mService = retrofit.create(CardapioService.class);
    }

    public List<Cardapio> fetchCardapio() {
        try {
            final CardapioList result = mService.getCardapio().execute().body();
            if (result != null) {
                return result.items();
            }
        } catch (IOException e) {
            Log.e("CardapioList", "IO Error fetching documents.", e);

            if(mErrorListener != null) {
                mErrorListener.onFetchError();
            }
        }
        return new ArrayList<>();
    }

    public void setErrorListener(CardapioErrorListener mErrorListener) {
        this.mErrorListener = mErrorListener;
    }

    public interface CardapioErrorListener {
        void onFetchError();
    }
}
