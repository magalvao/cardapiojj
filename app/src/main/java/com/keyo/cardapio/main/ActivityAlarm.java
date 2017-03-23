package com.keyo.cardapio.main;

import android.app.AlarmManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.keyo.cardapio.R;
import com.keyo.cardapio.base.BaseActivity;
import com.keyo.cardapio.dao.AppPreferences;
import com.keyo.cardapio.main.dao.NotificationDAO;
import com.keyo.cardapio.main.presenter.ActivityAlarmPresenter;
import com.keyo.cardapio.main.view.ActivityAlarmView;

import java.util.Calendar;

/**
 * Created by renarosantos on 23/03/17.
 */

public class ActivityAlarm extends BaseActivity<ActivityAlarmPresenter> implements ActivityAlarmView {

    private TextView mTime;
    private Switch mSwitch;

    public static Intent createIntent(final Context context) {
        return new Intent(context, ActivityAlarm.class);
    }

    @NonNull
    @Override
    public String getScreenName() {
        return "bla";
    }

    @NonNull
    @Override
    protected ActivityAlarmPresenter createPresenter(@NonNull final Context context) {
        return new ActivityAlarmPresenter(new AppPreferences(context), this,
                new NotificationDAO(context, new AppPreferences(context),
                        (AlarmManager) getSystemService(Context.ALARM_SERVICE)));
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        mTime = (TextView) findViewById(R.id.time_textview);
        mSwitch = (Switch) findViewById(R.id.switch_daily_notification);
        mSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {
                mPresenter.onSwitchChanged(mSwitch.isChecked());
            }
        });
        findViewById(R.id.time_picker_button).setOnClickListener(new OnEditTimeClicked());
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.onLeaveActivity();
    }

    @Override
    public void updateUI(final int hourToNotify, final int minuteToNitify, final boolean notificationAllowed) {
        mTime.setText(getString(R.string.time_format, hourToNotify, minuteToNitify));
        mSwitch.setChecked(notificationAllowed);
    }

    private class OnEditTimeClicked implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {

        @Override
        public void onTimeSet(final TimePicker timePicker, final int hour, final int minute) {
            mTime.setText(getString(R.string.time_format, hour, minute));
            mPresenter.onTimeChanged(hour, minute);
        }

        @Override
        public void onClick(final View view) {
            Calendar newCalendar = Calendar.getInstance();
            final TimePickerDialog timerPickDialog =
                    new TimePickerDialog(ActivityAlarm.this, this, newCalendar.get(Calendar.HOUR_OF_DAY),
                            newCalendar.get(Calendar.MINUTE), true);
            timerPickDialog.show();
        }
    }
}
