package com.example.kyle.joulieapp.api;

import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.Rule;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Kyle on 2017-02-24.
 */

public interface ApiService {

    //test connection to url
    @GET(".")
    Call<Void> ping();

    //Creating a new Robot
    @POST("robot/{robot_name}")
    Call createRobot(@Path("robot_name") String robotName);

    //Delete a Robot
    @DELETE("robot/{robot_name}")
    Call deleteRobot(@Path("robot_name") String robotName);

    //Create a new device for the specified robot
    @POST("device")
    Call<Device> createDevice(@Body Device device);

    //delete a device from the specified robot
    @DELETE("device/{device_id}")
    Call<String> deleteDevice(@Path("device_id") String deviceID);

    //Create a new device for the specified robot
    @POST("rule")
    Call<Rule> createRule(@Body Rule rule);

    //delete a device from the specified robot
    @DELETE("rule/{rule_id}")
    Call<String> deleteRule(@Path("rule_id") String ruleID);

    //send a command to a specific device
    @POST("device/{device_id}/{command_name}")
    Call<String> sendCommand(@Path("device_id") String deviceName, @Path("command_name") String commandName, @Body HashMap<String, String> body);

    @POST("newuser")
    Call<String> newUser(@Body HashMap<String, String> body);

    @POST("updateuser")
    Call<String> updateUser(@Body HashMap<String, String> body);

    @GET("devices")
    Call<List<Device>> getDevices();

    @GET("rules")
    Call<List<Rule>> getRules();



    // TODO: 2017-03-17 get usage data endpoint
    // TODO: 2017-03-17 create new rule endpoint

}
