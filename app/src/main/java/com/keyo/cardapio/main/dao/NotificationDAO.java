package com.keyo.cardapio.main.dao;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.keyo.cardapio.dao.AppPreferences;
import com.keyo.cardapio.service.AlarmReceiver;

import java.util.Calendar;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

/**
 * Created by renarosantos on 23/03/17.
 */

public class NotificationDAO {

    public static final int ONE_DAY = 1;
    public static final long DAYS_TO_MILLISECONDS = 86400000;
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
        final int hour = mPreferences.getHourToNotify();
        final int minute = mPreferences.getMinuteToNitify();
        Calendar calendarNow = Calendar.getInstance();
        Calendar calendarNotification = Calendar.getInstance();
        calendarNotification.set(Calendar.HOUR_OF_DAY, hour);
        calendarNotification.set(Calendar.MINUTE, minute);
        if (calendarNow.after(calendarNotification)) {
            calendarNotification.add(Calendar.DAY_OF_YEAR, ONE_DAY);
        }
        Intent notificationIntent = AlarmReceiver.createCardapioIntent(mContext);
        final PendingIntent alarmIntent =
                PendingIntent.getBroadcast(mContext, CARDAPIO_ALARM_ID, notificationIntent, FLAG_UPDATE_CURRENT);

        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendarNotification.getTimeInMillis(),
                DAYS_TO_MILLISECONDS, alarmIntent);
    }
}
