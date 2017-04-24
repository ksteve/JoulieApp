package com.example.kyle.joulieapp.Models;


import com.google.gson.annotations.SerializedName;

/**
 * Created by Kyle on 2016-10-30.
 */

public class Rule {

    public String id;
    public String ruleName;
    public boolean isActive = false;
    public Device device;
    public int turnOnOff;
    public int time;
    public int days;

    //@SerializedName("uuid")
    private String ruleId;

    //int 1 or 0
    @SerializedName("state")
    private int state;

    //0000 - 2359
    @SerializedName("run_time")
    private int runTime;

    //int 000001
    @SerializedName("repeat")
    private int repeat;

    @SerializedName("device_id")
    private int deviceID;

    public Rule(String ruleName, Device device, int turnOnOff, int time, int days) {
        this.ruleName = ruleName;
        this.device = device;
        this.turnOnOff = turnOnOff;
        this.time = time;
        this.days = days;
    }

    public String getId() {
        return this.ruleId;
    }

    public void setId(String id) {
        this.ruleId = id;
    }

}
