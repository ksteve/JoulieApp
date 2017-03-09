package com.example.kyle.joulieapp.Models;

import android.graphics.drawable.Drawable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Kyle on 2016-10-30.
 */

public class Device {

    @SerializedName("name")
    private String deviceName;

    @SerializedName("id")
    private String id;

    @SerializedName("type")
    private String type;

    @SerializedName("state")
    private boolean powerState;

    public transient Drawable image;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    //@Bindable
    //private transient int currentTemp;

    public Device(String id, String deviceType,String deviceName, Drawable image) {
        this.id = id;
        this.deviceName = deviceName;
        this.type = deviceType;
        this.image = image;
        this.powerState = false;
    }

    public Device(){

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public boolean getPowerState() { return powerState;}

    public void setPowerState(boolean state) {
        this.powerState = state;
    }

}
