package de.lmu.ifi.pixelfighter;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by michael on 23.11.17.
 */

public class DefaultApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
    }

}
