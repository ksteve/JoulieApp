package com.example.kyle.joulieapp.api;

import com.example.kyle.joulieapp.Models.Device;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Kyle on 2017-02-24.
 */

public interface ApiService {

    //@POST("device_test/Test")
    //Call createDevice(@Body Device device);

    @POST("api/robots/Test/commands/create_device")
    Call<Device> createDevice(@Body Device device);

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://joulie-cylon.herokuapp.com/")
            //.baseUrl("http://192.168.2.14:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
