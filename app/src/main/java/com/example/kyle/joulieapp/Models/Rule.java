package com.example.kyle.joulieapp.Models;



/**
 * Created by Kyle on 2016-10-30.
 */

public class Rule {

    public final String id;
    public String ruleName;
    public boolean isActive = false;
    public Device device;
    public int turnOnOff;
    public String time;
    public String days;

    public Rule(String id, String ruleName, Device device, int turnOnOff, String time, String days) {
        this.id = id;
        this.ruleName = ruleName;
        this.device = device;
        this.turnOnOff = turnOnOff;
        this.time = time;
        this.days = days;
    }

    public Rule(String id){
        this.id = id;
    }


}
