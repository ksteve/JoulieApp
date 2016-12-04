package com.example.kyle.joulieapp.Models;

import android.graphics.drawable.Drawable;

/**
 * Created by Kyle on 2016-10-30.
 */

public class Device {

    public String id;
    public Drawable image;
    public String deviceName;
    public Usage usage;
    public boolean isActive = false;

    public Device(String id, String streamName) {
        this.id = id;
        this.deviceName = streamName;
        this.usage = new Usage(this.id);
    }

    public Device(){

    }



}
