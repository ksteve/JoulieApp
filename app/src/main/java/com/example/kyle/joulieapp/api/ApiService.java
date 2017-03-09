package com.example.kyle.joulieapp.api;

import com.example.kyle.joulieapp.Models.Device;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Kyle on 2017-02-24.
 */

public interface ApiService {

    @POST("device_test/Test")
    Call<Device> createDevice(@Body Device device);

    //@POST("api/robots/Test/commands/create_device")
    //Call<Device> createDevice(@Body Device device);

    @POST("robot/{robot_name}/device/{device_name}/{command_name}")
    Call<String> sendCommand(@Path("robot_name") String robotName, @Path("device_name") String deviceName, @Path("command_name") String commandName, @Body String state);


    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://joulie-cylon.herokuapp.com/")
            //.baseUrl("http://192.168.2.14:3000/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
