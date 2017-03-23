package com.keyo.cardapio.main.dao;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.keyo.cardapio.dao.AppPreferences;
import com.keyo.cardapio.model.Cardapio;
import com.keyo.cardapio.service.AlarmReceiver;

import java.util.List;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

/**
 * Created by renarosantos on 23/03/17.
 */

public class NotificationDAO {

    private static final int CARDAPIO_ALARM_ID = 552;
    private final AppPreferences mPreferences;
    private final AlarmManager mAlarmManager;
    private final Context mContext;

    public NotificationDAO(@NonNull final Context context, final AppPreferences preferences,
                           final AlarmManager alarmManager) {
        mPreferences = preferences;
        mContext = context;
        mAlarmManager = alarmManager;
    }

    public void scheduleNotifications() {
        List<Cardapio> cardapios = mPreferences.loadCardapio();
        final int hour = mPreferences.getHourToNotify();
        final int minute = mPreferences.getMinuteToNitify();
        Intent notificationIntent = AlarmReceiver.createCardapioIntent(mContext);
        final int broadCastId = CARDAPIO_ALARM_ID;
        final PendingIntent alarmIntent =
                PendingIntent.getBroadcast(mContext, broadCastId, notificationIntent, FLAG_UPDATE_CURRENT);

        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);

    }
}
