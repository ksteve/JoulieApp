package com.example.kyle.joulieapp;

import android.app.Application;

import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.api.ApiClient;
import com.example.kyle.joulieapp.utils.Tools;

/**
 * Created by Kyle on 2016-12-03.
 */

public class JoulieApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DummyContent.init(getApplicationContext());
        DummyContent.addDevice(new Device(1, "adsfa", "saddfa", "asdfasd", null ));
        ApiClient.getInstance(getApplicationContext());

    }

}
