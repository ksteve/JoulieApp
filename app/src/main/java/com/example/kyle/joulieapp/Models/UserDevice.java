package com.example.kyle.joulieapp.Models;

/**
 * Created by Kyle on 2016-12-04.
 */

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

//Names: Amshar Basheer, Grigory Kozyrev, Kyle Stevenson
//File Name: PiElectricity.java
//Project Name: AndroidPubSub
//Description: class used when reading electricity data

@DynamoDBTable(tableName = "USER_DEVICES")
public class UserDevice {

    private String userID;
    private String deviceID;
    private String name;

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "UserID-index",attributeName = "UserID")
    public String getUserID(){
        return userID;
    }

    public void setUserID(String newUserID){
        userID = newUserID;
    }

    @DynamoDBHashKey(attributeName = "DeviceID")
    public String getDeviceID(){
        return deviceID;
    }

    public void setDeviceID(String newDeviceID){
        deviceID = newDeviceID;
    }

    @DynamoDBAttribute(attributeName = "DeviceName")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}