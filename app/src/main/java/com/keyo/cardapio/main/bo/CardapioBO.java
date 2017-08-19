package com.keyo.cardapio.main.bo;

import com.keyo.cardapio.dao.CardapioDAO;
import com.keyo.cardapio.main.dao.NotificationDAO;
import com.keyo.cardapio.model.CardapioDate;

import java.util.List;

/**
 * Created by renarosantos on 23/03/17.
 */

public class CardapioBO {

    private final CardapioDAO mCardapioDAO;
    private final NotificationDAO mNotificationDAO;

    public CardapioBO(final CardapioDAO cardapioDAO, final NotificationDAO notificationDao) {
        mCardapioDAO = cardapioDAO;
        mNotificationDAO = notificationDao;
    }

    public List<CardapioDate> fetchCardapio() {
        List<CardapioDate> cardapios = mCardapioDAO.fetchCardapio();
        if (cardapios != null) {
            mCardapioDAO.saveCardapios(cardapios);
            mNotificationDAO.scheduleNotifications();
        }
        return cardapios;
    }
}
