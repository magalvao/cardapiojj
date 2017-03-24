package com.keyo.cardapio.main.presenter;

import com.keyo.cardapio.base.BasePresenter;
import com.keyo.cardapio.dao.AppPreferences;
import com.keyo.cardapio.main.dao.NotificationDAO;
import com.keyo.cardapio.main.view.ActivityAlarmView;

/**
 * Created by renarosantos on 23/03/17.
 */

public class ActivityAlarmPresenter extends BasePresenter {

    private final AppPreferences mPreferences;
    private final ActivityAlarmView mView;
    private final NotificationDAO mNotificationDAO;
    private int mHourToNotify;
    private int mMinuteToNitify;
    private boolean mNotificationAllowed;

    public ActivityAlarmPresenter(AppPreferences preferences, ActivityAlarmView view,
                                  final NotificationDAO notificationDAO) {
        mPreferences = preferences;
        mView = view;
        mNotificationDAO = notificationDAO;
    }

    @Override
    public void onResume() {
        super.onResume();
        mHourToNotify = mPreferences.getHourToNotify();
        mMinuteToNitify = mPreferences.getMinuteToNitify();
        mNotificationAllowed = mPreferences.isNotificationAllowed();
        mView.updateUI(mHourToNotify, mMinuteToNitify, mNotificationAllowed);
    }

    public void onTimeChanged(final int hour, final int minute) {
        mPreferences.saveHourAndMinute(hour, minute);
    }

    public void onSwitchChanged(final boolean isChecked) {
        mPreferences.isChecked(isChecked);
    }

    public void onLeaveActivity() {
        mNotificationDAO.scheduleNotifications();
    }
}
