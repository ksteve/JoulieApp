package com.example.kyle.joulieapp.Models;

/**
 * Created by Kyle on 2016-12-04.
 */

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

//Names: Amshar Basheer, Grigory Kozyrev, Kyle Stevenson
//File Name: PiElectricity.java
//Project Name: AndroidPubSub
//Description: class used when reading electricity data

@DynamoDBTable(tableName = "PI_ELECTRICITY")
public class PiElectricity {

    private String deviceID;

    @DynamoDBHashKey(attributeName = "DeviceID")
    public String getDeviceID(){
        return deviceID;
    }


    public void setDeviceID(String newDeviceID){
        deviceID = newDeviceID;
    }

}