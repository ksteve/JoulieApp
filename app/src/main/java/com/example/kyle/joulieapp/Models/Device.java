package com.example.kyle.joulieapp.Models;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Kyle on 2016-10-30.
 */

public class Device {
    public static final String TYPE_WEMO = "wemo";
    public static final String TYPE_TPLINK = "tplink";
    public static final String WEMO_DISPLAY = "Wemo Insight Switch";
    public static final String TPLINK_DISPLAY = "TP-Link Smart Plug";

    
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

    public transient int color;

    public transient String status;

    public transient Drawable image;

    private transient List<Usage> deviceUsage;

    public transient float totalKwh;
    public transient float estimatedCost;

    public Device(String deviceType,String deviceName, String deviceIP, String devicePort, Drawable image) {
        setDeviceName(deviceName);
        setType(deviceType);
        setOwned(1);
        setPowerState(false);
        setIpAddress(deviceIP);
        setPort(devicePort);
        setImage(image);
        color = Color.BLUE;
    }

    public String getId() {
        return this.deviceId;
    }

    public void setId(String id) {
        this.deviceId = id;
    }

    public String getTypeName(){
        if(this.getType().equals(TYPE_WEMO)){
            return WEMO_DISPLAY;
        } else if (this.getType().equals(TYPE_TPLINK)) {
            return TPLINK_DISPLAY;
        } else if(this.getType().equals("1")){
            return WEMO_DISPLAY;
        } else if (this.getType().equals("2")) {
            return TPLINK_DISPLAY;
        } else {
            return "Unknown Type";
        }
    }

    public String getType() {
        return this.deviceType;
    }

    public void setType(String type) {

        if(type == "1") {type = Device.TYPE_WEMO;}

        if(type == "2") {type = Device.TYPE_TPLINK;}

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

    public List<Usage> getDeviceUsage() {
        return deviceUsage;
    }

    public void setDeviceUsage(List<Usage> deviceUsage) {
        this.deviceUsage = deviceUsage;
    }
}
