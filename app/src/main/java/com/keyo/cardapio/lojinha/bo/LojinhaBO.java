package com.keyo.cardapio.lojinha.bo;

import com.keyo.cardapio.lojinha.dao.LojinhaDAO;
import com.keyo.cardapio.lojinha.model.Order;
import com.keyo.cardapio.lojinha.model.Track;

import java.util.List;

/**
 * Created by mgalvao3 on 17/06/17.
 */

public class LojinhaBO {
    private final LojinhaDAO mLojinhaDAO;

    public LojinhaBO(final LojinhaDAO lojinhaDAO) {
        mLojinhaDAO = lojinhaDAO;
    }

    public Track fetchTracking() {
        Track track = mLojinhaDAO.fetchTracking();
        if (track != null) {
            mLojinhaDAO.saveTrackNumber(track);
        }
        return track;
    }

    public List<Order> fetchPedidos() {
        return mLojinhaDAO.fetchPedidos();
    }

    public void saveOrder(final String value) {
        mLojinhaDAO.saveOrder(value);
    }

    public void deleteOrder(final String value) {
        mLojinhaDAO.deleteOrder(value);
    }
}
