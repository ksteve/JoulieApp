package com.example.kyle.joulieapp;

import android.app.Application;

import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.api.ApiClient;

/**
 * Created by Kyle on 2016-12-03.
 */

public class JoulieApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DummyContent.setContext(getApplicationContext());
     //   ApiClient.getInstance(getApplicationContext());

    }

}
