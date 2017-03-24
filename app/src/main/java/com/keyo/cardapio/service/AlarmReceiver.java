package com.keyo.cardapio.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.keyo.cardapio.R;
import com.keyo.cardapio.dao.AppPreferences;
import com.keyo.cardapio.main.dao.NotificationDAO;
import com.keyo.cardapio.model.Cardapio;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by renarosantos on 21/09/16.
 */
public class AlarmReceiver extends BroadcastReceiver {

    public static final int CARDAPIO_NOTIFICATION_ID = 12;
    private NotificationDAO mNotificationDAO;

    public static Intent createCardapioIntent(final Context context) {
        return new Intent(context, AlarmReceiver.class);
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final AppPreferences userPreference = new AppPreferences(context);
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mNotificationDAO = new NotificationDAO(context, userPreference, alarmManager);

        List<Cardapio> cardapios = userPreference.loadCardapio();
        final boolean isNotificationsAllowed = userPreference.isNotificationAllowed();
        if (cardapios == null || !isNotificationsAllowed) {
            return;
        }
        Cardapio currentCardapio = findCurrentCardapio(cardapios);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = createNotificationForCardapido(context, currentCardapio);
        notificationManager.notify(CARDAPIO_NOTIFICATION_ID, notification);
    }

    private Notification createNotificationForCardapido(final Context context, final Cardapio currentCardapio) {
        String description = currentCardapio.getDescription();
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context).setSmallIcon(R.drawable.cardapio_logo)
                        .setContentTitle("Cardápio de hoje")
                        .setContentText(description)
                        .setDefaults(Notification.DEFAULT_ALL) // requires VIBRATE permission
        /*
         * Sets the big view "big text" style and supplies the
         * text (the user's reminder message) that will be displayed
         * in the detail area of the expanded notification.
         * These calls are ignored by the support library for
         * pre-4.1 devices.
         */
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(
                                description + "\n" + description + "\n" + description + "\n" + description));
        return builder.build();
    }

    private Cardapio findCurrentCardapio(@NonNull final List<Cardapio> cardapios) {
        CalendarDateUtils calendarDateUtils = new CalendarDateUtils();
        Date today = Calendar.getInstance().getTime();
        for (final Cardapio cardapio : cardapios) {
            Date cardapioDate = cardapio.getDate();
            boolean sameDay = calendarDateUtils.isSameDay(today, cardapioDate);
            if (sameDay) {
                return cardapio;
            }
        }
        return null;
    }


    private void showError() {
        throw new IllegalArgumentException("Notification Type can not be null");
    }
}
