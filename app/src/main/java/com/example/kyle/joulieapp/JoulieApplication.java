package com.example.kyle.joulieapp;

import android.app.Application;

import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.Api.ApiClient;
import com.example.kyle.joulieapp.Models.Rule;

/**
 * Created by Kyle on 2016-12-03.
 */

public class JoulieApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DummyContent.init(getApplicationContext());
      //  DummyContent.addDevice(new Device(Device.TYPE_TPLINK, "adsfa", "saddfa", "asdfasd", null ));
       // DummyContent.addRule(new Rule("hello", DummyContent.MY_DEVICES.get(0), 1, 1, "4:30", 1));
        ApiClient.getInstance(getApplicationContext());

    }

}
