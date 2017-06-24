package com.keyo.cardapio.lojinha.view;

/**
 * Created by mgalvao3 on 17/06/17.
 */

public interface LojinhaView {

    void updateTracking(String lastTrackNumber);

    void showLastData();

    void showErrorMessage();
}
