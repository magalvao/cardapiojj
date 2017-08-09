package com.keyo.cardapio.lojinha.model;

import android.support.annotation.NonNull;

/**
 * Created by renarosantos1 on 26/06/17.
 */

public class Order implements Comparable<Order> {

    private String number;
    private boolean shouldTrack = false;
    private boolean alreadyDelivered = false;

    public Order(final String number) {
        this.number = number;
    }


    public boolean isAvailableForRetrieve(final String ultimoPedido) {
        if (ultimoPedido == null || number == null) {
            return false;
        } else {
            try {
                Long ultimoPedidoValue = Long.parseLong(ultimoPedido);
                Long numberValue = Long.parseLong(number);
                return numberValue <= ultimoPedidoValue;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    public String getNumber() {
        return number;
    }

    public Long getNumberConverted() {
        return Long.parseLong(number);
    }

    @Override
    public int compareTo(@NonNull final Order o) {
        return getNumberConverted() >= o.getNumberConverted() ? 1 : -1;
    }
}
