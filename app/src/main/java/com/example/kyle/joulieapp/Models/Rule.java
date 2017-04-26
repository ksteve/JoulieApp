package com.example.kyle.joulieapp.Models;


import com.google.gson.annotations.SerializedName;

/**
 * Created by Kyle on 2016-10-30.
 */

public class Rule {

    public String ruleName;
   // public boolean isActive = false;
    public transient Device device;
//    public int turnOnOff;
//    public int time;
//    public int days;

    @SerializedName("uuid")
    public String id;

    //int 1 or 0
    @SerializedName("state")
    public int state;

    //0000 - 2359
    @SerializedName("run_time")
    public int runTime;

    //int 000001
    @SerializedName("repeat")
    public int repeat;

    @SerializedName("device_guid")
    public int deviceID;



    public Rule(String ruleName, Device device, int turnOnOff, int time, int days) {
        this.ruleName = ruleName;
        this.device = device;
        this.state = turnOnOff;
        this.runTime = time;
        this.repeat = days;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
