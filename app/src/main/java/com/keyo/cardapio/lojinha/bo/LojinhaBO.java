package com.keyo.cardapio.lojinha.bo;

import com.keyo.cardapio.lojinha.dao.LojinhaDAO;
import com.keyo.cardapio.lojinha.model.Track;

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
}
