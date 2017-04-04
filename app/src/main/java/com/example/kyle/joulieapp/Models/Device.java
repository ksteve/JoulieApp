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
    private String deviceId;

    @SerializedName("type")
    private String deviceType;

    @SerializedName("owned")
    private int isOwned;

    @SerializedName("state")
    private boolean powerState;

    @SerializedName("ip")
    private String ipAddress;

    @SerializedName("port")
    private String port;

    @SerializedName("creation_date")
    private String creationDate;

    @SerializedName("last_activity_date")
    private String lastActivityDate;

//  @SerializedName("owner_user_id")
//  private String owner;

    public transient String status;

    public transient Drawable image;

    public Device(String deviceType,String deviceName, String deviceIP, String devicePort, Drawable image) {
        setDeviceName(deviceName);
        setType(deviceType);
        setOwned(1);
        setPowerState(false);
        setIpAddress(deviceIP);
        setPort(devicePort);
        setImage(image);
    }

    public String getId() {
        return this.deviceId;
    }

    public void setId(String id) {
        this.deviceId = id;
    }

    public String getType() {
        return this.deviceType;
    }

    public void setType(String type) {
        switch (type){
            case "Wemo Insight Switch":
                this.deviceType = "wemo";
                break;

            case "TP-Link Smart Plug":
                this.deviceType = "tplink";
                break;
        }
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getOwned() {
        return isOwned;
    }

    public void setOwned(int owned) {
        isOwned = owned;
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
