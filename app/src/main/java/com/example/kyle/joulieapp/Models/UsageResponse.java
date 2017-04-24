package com.example.kyle.joulieapp.Models;

import com.github.mikephil.charting.data.Entry;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Kyle on 2017-04-17.
 */

public class UsageResponse {

    @SerializedName("device")
    private String deviceID;

    @SerializedName("usage")
    private List<Usage> usages;

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public List<Usage> getUsages() {
        return usages;
    }

    public void setUsages(List<Usage> usages) {
        this.usages = usages;
    }
}
