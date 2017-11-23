package de.lmu.ifi.pixelfighter;

import android.app.Application;
import android.content.Context;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by michael on 23.11.17.
 */

public class DefaultApp extends Application {

    private static Context appContext;
    public static Context getAppContext() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this.getApplicationContext();
        JodaTimeAndroid.init(this);
    }

}
