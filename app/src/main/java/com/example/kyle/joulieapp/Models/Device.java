package com.example.kyle.joulieapp.Models;

import android.graphics.drawable.Drawable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Kyle on 2016-10-30.
 */

public class Device {

    public static final int WEMO_INSIGHT_SWITCH = 742;
    public static final int TPLINK_HS110 = 752;
    
    @SerializedName("display_name")
    private String deviceName;

    @SerializedName("uuid")
    private String id;

    @SerializedName("type")
    private String type;

    @SerializedName("state")
    private boolean powerState;

    @SerializedName("ip")
    private String ipAddress;

    @SerializedName("port")
    private String port;

    @SerializedName("owner_user_id")
    private String owner;

    //@SerializedName("creation_date")

    //@SerializedName("last_activity_date")

    public transient String status;

    public transient Drawable image;

    //@Bindable
    //private transient int currentTemp;

    public Device(String id, String deviceType,String deviceName, String deviceIP, String devicePort, Drawable image) {
        setId(id);
        setDeviceName(deviceName);
        setType(deviceType);
        setImage(image);
        setPowerState(false);
        setIpAddress(deviceIP);
        setPort(devicePort);
    }

//    public Device(){
//
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        switch (type){
            case "Wemo Insight Switch":
                this.type = "wemo";
                break;

            case "TP-Link Smart Plug":
                this.type = "tplink";
                break;
        }
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

    public String getIpAddress(){
        return ipAddress;
    }

    public void setIpAddress(String ipAddress){
        this.ipAddress = ipAddress;
    }

    public String getPort(){
        return port;
    }

    public void setPort(String port){
        this.port = port;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

}
