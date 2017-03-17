package com.example.kyle.joulieapp.api;

import com.example.kyle.joulieapp.Models.Device;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Kyle on 2017-02-24.
 */

public interface ApiService {

    //Creating a new Robot
    @POST("robot/{robot_name}")
    Call createRobot(@Path("robot_name") String robotName);

    //Delete a Robot
    @DELETE("robot/{robot_name}")
    Call deleteRobot(@Path("robot_name") String robotName);

    //Create a new device for the specified robot
    @POST("robot/{robot_id}/device")
    Call<Device> createDevice(@Path("robot_id") String robotID, @Body Device device);

    //delete a device from the specified robot
    @DELETE("robot/{robot_id}/device/{device_id}")
    Call deleteDevice(@Path("robot_id")String robotID, @Path("device_id") String deviceID);

    //send a command to a specific device
    @POST("robot/{robot_name}/device/{device_name}/{command_name}")
    Call<String> sendCommand(@Path("robot_name") String robotName, @Path("device_name") String deviceName, @Path("command_name") String commandName, @Body HashMap<String, String> body);

    // TODO: 2017-03-17 get usage data endpoint
    // TODO: 2017-03-17 create new rule endpoint

}
