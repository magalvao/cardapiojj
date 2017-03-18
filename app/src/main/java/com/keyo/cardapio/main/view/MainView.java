package com.keyo.cardapio.main.view;

import com.keyo.cardapio.model.Cardapio;

import java.util.List;

/**
 * Created by mgalvao3 on 01/02/17.
 */
public interface MainView {

    void updateList(List<Cardapio> list);

    void setRefreshing(boolean state);

    void selectTab(int index);

    void notifyUpdatedList();

    void notifyError();
}
