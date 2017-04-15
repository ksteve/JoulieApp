package com.example.kyle.joulieapp.Models;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Kyle on 2016-10-30.
 */

public class Device {
    public static final int TYPE_WEMO = 1;
    public static final int TYPE_TPLINK = 2;
    public static final String WEMO_DISPLAY = "Wemo Insight Switch";
    public static final String TPLINK_DISPLAY = "TP-Link Smart Plug";

    
    @SerializedName("display_name")
    private String deviceName;

    @SerializedName("uuid")
    private String deviceId;

    @SerializedName("type")
    private int deviceType;

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

    public transient int color;

    public transient String status;

    public transient Drawable image;

    public Device(int deviceType,String deviceName, String deviceIP, String devicePort, Drawable image) {
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

    public String getTypeName(){
        if(this.getType() == TYPE_WEMO){
            return WEMO_DISPLAY;
        } else if (this.getType() == TYPE_TPLINK) {
            return TPLINK_DISPLAY;
        } else {
            return "Unknown Type";
        }
    }

    public int getType() {
        return this.deviceType;
    }

    public void setType(int type) {
        this.deviceType = type;
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

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
