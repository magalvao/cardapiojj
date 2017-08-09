package com.keyo.cardapio.lojinha.view;

import com.keyo.cardapio.lojinha.model.Order;

import java.util.List;

/**
 * Created by mgalvao3 on 17/06/17.
 */

public interface LojinhaView {

    void updateTracking(String lastTrackNumber);

    void deleteOrder(String order);

    void showLastData();

    void showErrorMessage();

    void showOrders(List<Order> result);

    void showInputDialog();

    void showOrderDialog(String order, final boolean ready);
}
