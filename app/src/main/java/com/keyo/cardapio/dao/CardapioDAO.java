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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mgalvao3 on 09/03/17.
 */

public class CardapioDAO {

    private final CardapioService mService;
    private final AppPreferences mAppPreferences;
    private CardapioErrorListener mErrorListener;

    public CardapioDAO(@NonNull final String baseUrl, @NonNull final AppPreferences appPreferences) {
        mAppPreferences = appPreferences;

        //HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        //interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
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

            return null;
        }

        return new ArrayList<>();
    }

    public void setErrorListener(CardapioErrorListener mErrorListener) {
        this.mErrorListener = mErrorListener;
    }

    public void saveCardapios(final List<Cardapio> cardapios) {
        mAppPreferences.saveCardapio(cardapios);
    }

    public interface CardapioErrorListener {

        void onFetchError();
    }
}
