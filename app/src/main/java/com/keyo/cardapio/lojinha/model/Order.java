package com.keyo.cardapio.lojinha.model;

/**
 * Created by renarosantos1 on 26/06/17.
 */

public class Order {

    private final String number;

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
}
