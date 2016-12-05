package com.example.kyle.joulieapp;

import android.app.Application;

import com.example.kyle.joulieapp.Models.DummyContent;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by Kyle on 2016-12-03.
 */

public class JoulieApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        DummyContent.setContext(getApplicationContext());
    }

}
