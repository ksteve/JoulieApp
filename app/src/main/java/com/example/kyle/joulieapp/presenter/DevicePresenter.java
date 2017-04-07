package com.example.kyle.joulieapp.presenter;

import android.content.Context;
import android.util.Log;

import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.api.ApiClient;
import com.example.kyle.joulieapp.api.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kyle on 2017-04-05.
 */

public class DevicePresenter {

    private final Context context;
    private final DevicePresenterListener mListener;
    private final ApiService apiService;

    public interface DevicePresenterListener{
        void devicesReady(List<Device> devices);
        void deviceRemoved();
    }

    public DevicePresenter(DevicePresenterListener listener, Context context){
        this.mListener = listener;
        this.context = context;
        this.apiService = ApiClient.getInstance(this.context).getApiService();
    }

    public void subscribe(){
       // RxBus.get().register(this);
    }

    public void unsubscribe(){
     //   RxBus.get().unregister(this);
    }


    public void removeDevice(Device device){
        apiService
                .deleteDevice(device.getId())
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        mListener.deviceRemoved();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        //// TODO: 2017-04-05 throw error, display message to user
                    }
                });

    }

    public void getDevices(){
        apiService
                .getDevices()
                .enqueue(new Callback<List<Device>>() {
                    @Override
                    public void onResponse(Call<List<Device>> call, Response<List<Device>> response) {
                        mListener.devicesReady(response.body());
                    }

                    @Override
                    public void onFailure(Call<List<Device>> call, Throwable t) {
                        //// TODO: 2017-04-05 throw error, display message to user
                    }
                });
    }
}