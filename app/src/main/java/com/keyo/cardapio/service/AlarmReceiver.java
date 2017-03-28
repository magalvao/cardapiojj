package com.keyo.cardapio.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.Html;

import com.keyo.cardapio.R;
import com.keyo.cardapio.dao.AppPreferences;
import com.keyo.cardapio.main.MainActivity;
import com.keyo.cardapio.main.dao.NotificationDAO;
import com.keyo.cardapio.model.Cardapio;

import java.util.ArrayList;
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
        ArrayList<Cardapio> todayMeals = (ArrayList<Cardapio>) findTodayMeals(cardapios);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = createNotificationForCardapido(context, formatNotificationDescription(todayMeals));
        notificationManager.notify(CARDAPIO_NOTIFICATION_ID, notification);
    }

    private String formatNotificationDescription(final ArrayList<Cardapio> todayMeals) {
        if (todayMeals == null || todayMeals.isEmpty()) {
            return "Abra o app para atualizar o cardápio da semana!";
        }

        StringBuilder description = new StringBuilder();
        for (Cardapio meal : todayMeals) {
            description.append("<b>")
                    .append(meal.getOptionName())
                    .append(":</b> ")
                    .append(meal.getDescription())
                    .append("<br />");
        }

        return description.toString();
    }

    private Notification createNotificationForCardapido(final Context context, final String notificationDescription) {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context).setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("Cardápio de Hoje")
                        .setContentText("Deslize para baixo e veja o cardápio de hoje!")
                        .setDefaults(Notification.DEFAULT_ALL) // requires VIBRATE permission
        /*
         * Sets the big view "big text" style and supplies the
         * text (the user's reminder message) that will be displayed
         * in the detail area of the expanded notification.
         * These calls are ignored by the support library for
         * pre-4.1 devices.
         */
                        .setStyle(
                                new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(notificationDescription)));
        PendingIntent resultPendingIntent = openAppIntent(context);
        builder.setContentIntent(resultPendingIntent);
        builder.setAutoCancel(true);
        return builder.build();
    }

    private List<Cardapio> findTodayMeals(@NonNull final List<Cardapio> cardapios) {

        ArrayList<Cardapio> list = new ArrayList<>();

        CalendarDateUtils calendarDateUtils = new CalendarDateUtils();
        Date today = Calendar.getInstance().getTime();
        for (final Cardapio cardapio : cardapios) {
            Date cardapioDate = cardapio.getDate();
            boolean sameDay = calendarDateUtils.isSameDay(today, cardapioDate);
            if (sameDay) {
                list.add(cardapio);
            }
        }
        return list;
    }

    private PendingIntent openAppIntent(final Context context) {
        Intent resultIntent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void showError() {
        throw new IllegalArgumentException("Notification Type can not be null");
    }
}
