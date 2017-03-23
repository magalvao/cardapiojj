package com.keyo.cardapio.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.jnj.mycalendar.event.dao.AppEventDAO;
import com.jnj.mycalendar.event.model.Event;
import com.jnj.mycalendar.register.model.ReminderSetup;
import com.jnj.mycalendar.register.view.SplashScreenActivity;
import com.jnj.mycalendar.setup.model.NotificationOption;
import com.jnj.mycalendar.setup.model.NotificationsSetupPreferences;
import com.jnj.mycalendar.soundDialog.AlarmSound;
import com.jnj.mycalendar.user.dao.UserPreference;
import com.keyo.cardapio.main.dao.NotificationDAO;

import java.util.Calendar;
import java.util.Date;

import jnj.com.mycalendar.R;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

/**
 * Created by renarosantos on 21/09/16.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private static final long ONE_DAY = 1000L * 60 * 60 * 24;
    private NotificationDAO mNotificationDAO;

    public static Intent createCardapioIntent(final Context context) {
        return new Intent(context, AlarmReceiver.class);
    }

    public static Intent createMenstruationIntent(final Context context) {
        final Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(EXTRA_ALARM_TYPE, EXTRA_MENSTRUATION_TYPE);
        return intent;
    }

    public static Intent createContraceptiveIntent(final Context context) {
        final Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(EXTRA_ALARM_TYPE, EXTRA_CONTRACEPTIVE_TYPE);
        return intent;
    }

    public static Intent createContraceptiveSnoozeIntent(final Context context, final long snoozeTime) {
        final Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(EXTRA_ALARM_TYPE, EXTRA_CONTRACEPTIVE_SCHEDULE_SNOOZE_TYPE);
        intent.putExtra(EXTRA_SNOOZE_TIME, snoozeTime);
        return intent;
    }

    public static Intent createContraceptiveNotificationSnoozeIntent(final Context context, final long snoozeTime) {
        final Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(EXTRA_ALARM_TYPE, EXTRA_CONTRACEPTIVE_SHOW_SNOOZE_TYPE);
        intent.putExtra(EXTRA_SNOOZE_TIME, snoozeTime);
        return intent;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final UserPreference userPreference = new UserPreference(context);
        mSetup = userPreference.fetchReminderSetup();
        mNotificationOptions = userPreference.fetchNotificationsPreferences();
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mNotificationDAO = new NotificationDAO(context, alarmManager, userPreference);
        final String type = intent.getStringExtra(EXTRA_ALARM_TYPE);
        if (type == null) {
            showError();
            return;
        }

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (type.equals(EXTRA_EVENT_TYPE)) {
            final long eventTime = intent.getLongExtra(EXTRA_EVENT_TIME, -1);
            createNotificationForEvent(eventTime, context, notificationManager);
        } else if (type.equals(EXTRA_CONTRACEPTIVE_TYPE)) {
            final boolean isPauseInterval = mNotificationDAO.isPauseInterval(mSetup);
            if (isPauseInterval) {
                mNotificationDAO.rescheduleContraceptives(mSetup);
            } else {
                userPreference.saveReminderSetup(mSetup);
                Notification notification = createNotificationForContraceptive(context);
                if (mNotificationOptions.contraceptivesOption().isNotificationAllowed()) {
                    notificationManager.notify(CONTRACEPTIVE_EVENT_ID, notification);
                }
            }
        } else if (type.equals(EXTRA_MENSTRUATION_TYPE)) {
            if (mNotificationOptions.calendarOption().isNotificationAllowed()) {
                Notification notification = createNotificationFromMenstruation(context);
                notificationManager.notify(MENSTRUATION_EVENT_ID, notification);
            }
        } else if (type.equals(EXTRA_CONTRACEPTIVE_SCHEDULE_SNOOZE_TYPE)) {
            final long snoozeTime = intent.getLongExtra(EXTRA_SNOOZE_TIME, THIRTY_MINUTES);
            notificationManager.cancel(CONTRACEPTIVE_EVENT_ID);
            scheduleSnoozeNotification(context, snoozeTime);
        } else if (type.equals(EXTRA_CONTRACEPTIVE_SHOW_SNOOZE_TYPE)) {
            if (mNotificationOptions.contraceptivesOption().isNotificationAllowed()) {
                Notification notification = createNotificationForSnooze(context);
                notificationManager.notify(CONTRACEPTIVE_EVENT_ID, notification);
            }
        }
    }

    private void scheduleSnoozeNotification(final Context context, final long snoozeTime) {
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final PendingIntent alarmIntent = PendingIntent.getBroadcast(context, CONTRACEPTIVE_REMINDER_SNOOZE_ID,
                AlarmReceiver.createContraceptiveNotificationSnoozeIntent(context, snoozeTime), FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + snoozeTime, alarmIntent);
    }

    private Notification createNotificationForSnooze(final Context context) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(context.getString(R.string.contraceptive_notification_title));
        builder.setContentText(context.getString(R.string.contraceptive_notification_message));
        builder.setSmallIcon(R.mipmap.ic_launcher);

        if (mNotificationOptions.contraceptivesOption() == NotificationOption.NOTIFICATION_PLUS_ALARM) {
            final Uri parse = Uri.parse(context.getString(R.string.resource_location) + mSetup.sound().audioResource());
            builder.setSound(parse);
        }
        final PendingIntent alarmSnoozeIntent = PendingIntent.getBroadcast(context, SHORT_SNOOZE_NOTIFICATION,
                AlarmReceiver.createContraceptiveSnoozeIntent(context, THIRTY_MINUTES), FLAG_UPDATE_CURRENT);
        final PendingIntent alarmSnoozeIntent2 = PendingIntent.getBroadcast(context, LONG_SNOOZE_NOTIFICATION,
                AlarmReceiver.createContraceptiveSnoozeIntent(context, ONE_HOUR), FLAG_UPDATE_CURRENT);

        builder.addAction(R.drawable.alarm_white, context.getString(R.string.alarm_thirty_minutes), alarmSnoozeIntent);
        builder.addAction(R.drawable.alarm_white, context.getString(R.string.alarm_one_hour), alarmSnoozeIntent2);
        return builder.build();
    }

    private void createNotificationForEvent(final long eventTime, final Context context,
                                            final NotificationManager notificationManager) {
        Event event = new AppEventDAO().fetchEventByDate(new Date(eventTime));
        if (event != null) {
            Notification notification = createNotificationFromEvent(event, context);
            if (!mNotificationOptions.eventsOption().equals(NotificationOption.NO_NOTIFICATION)) {
                notificationManager.notify(EVENT_ID, notification);
            }
        }
    }

    private Notification createNotificationForContraceptive(final Context context) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(context.getString(R.string.contraceptive_notification_title));
        builder.setContentText(context.getString(R.string.contraceptive_notification_message));
        builder.setSmallIcon(R.mipmap.ic_launcher);

        if (mNotificationOptions.contraceptivesOption() == NotificationOption.NOTIFICATION_PLUS_ALARM) {
            final Uri parse = Uri.parse(context.getString(R.string.resource_location) + mSetup.sound().audioResource());
            builder.setSound(parse);
        }
        final PendingIntent alarmSnoozeIntent = PendingIntent.getBroadcast(context, SHORT_SNOOZE_NOTIFICATION,
                AlarmReceiver.createContraceptiveSnoozeIntent(context, THIRTY_MINUTES), FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.alarm_white, context.getString(R.string.alarm_thirty_minutes), alarmSnoozeIntent);

        final PendingIntent alarmSnoozeIntent2 = PendingIntent.getBroadcast(context, LONG_SNOOZE_NOTIFICATION,
                AlarmReceiver.createContraceptiveSnoozeIntent(context, ONE_HOUR), FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.alarm_white, context.getString(R.string.alarm_one_hour), alarmSnoozeIntent2);

        PendingIntent resultPendingIntent = openAppIntent(context);
        builder.setContentIntent(resultPendingIntent);
        builder.setAutoCancel(true);

        return builder.build();
    }

    private Notification createNotificationFromMenstruation(final Context context) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(context.getString(R.string.menstruation_coming_notification_title));
        builder.setContentText(context.getString(R.string.menstuation_notification_message));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        if (mNotificationOptions.calendarOption().equals(NotificationOption.NOTIFICATION_PLUS_ALARM)) {
            final Uri parse =
                    Uri.parse(context.getString(R.string.resource_location) + AlarmSound.ALARM8.audioResource());
            builder.setSound(parse);
        }
        PendingIntent resultPendingIntent = openAppIntent(context);
        builder.setContentIntent(resultPendingIntent);
        builder.setAutoCancel(true);

        return builder.build();
    }

    private Notification createNotificationFromEvent(@NonNull final Event event, Context context) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(context.getString(event.getSelectedEvent().stringResource()));
        builder.setContentText(event.getEventDescription());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        if (mNotificationOptions.eventsOption() == NotificationOption.NOTIFICATION_PLUS_ALARM) {
            final Uri parse =
                    Uri.parse(context.getString(R.string.resource_location) + AlarmSound.ALARM8.audioResource());
            builder.setSound(parse);
        }

        PendingIntent resultPendingIntent = openAppIntent(context);
        builder.setContentIntent(resultPendingIntent);
        builder.setAutoCancel(true);
        return builder.build();
    }

    private PendingIntent openAppIntent(final Context context) {
        Intent resultIntent = SplashScreenActivity.createIntent(context, false, false, 0, false);
        return PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void showError() {
        throw new IllegalArgumentException("Notification Type can not be null");
    }
}
