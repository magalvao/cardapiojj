package com.keyo.cardapio.base;

import android.app.Application;

import com.keyo.cardapio.R;

import net.danlew.android.joda.JodaTimeAndroid;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by mgalvao3 on 01/02/17.
 */

public class AppApplication extends Application {

    //private Tracker mTracker;

    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                                              .setDefaultFontPath("fonts/SF_Regular.otf")
                                              .setFontAttrId(R.attr.fontPath)
                                              .build()
        );

        JodaTimeAndroid.init(this);
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    /*synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(BuildConfig.ANALYTICS_TRACKER_ID);
            mTracker.enableAdvertisingIdCollection(true);
        }
        return mTracker;
    }*/
}