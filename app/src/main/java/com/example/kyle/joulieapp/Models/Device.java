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

    private transient String type;

    public transient Drawable image;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    //@Bindable
    //private transient int currentTemp;

    public Device(String id, String streamName, Drawable image) {
        this.id = id;
        this.deviceName = streamName;
        this.image = image;
        //currentTemp = 0;
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

//    public int getCurrentTemp(){
//        return currentTemp;
//    }

//    public void setCurrentTemp(int temp){
//        this.currentTemp = temp;
//       // notifyPropertyChanged(BR.device);
//    }




}
