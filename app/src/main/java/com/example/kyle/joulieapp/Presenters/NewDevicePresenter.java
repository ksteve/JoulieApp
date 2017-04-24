package com.example.kyle.joulieapp.Presenters;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.example.kyle.joulieapp.Contracts.NewDeviceContract;
import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.Api.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kyle on 2017-04-05.
 */

public class NewDevicePresenter implements NewDeviceContract.Presenter {

    private final Context context;
    private final NewDeviceContract.View mNewDeviceView;

    public NewDevicePresenter(NewDeviceContract.View newDeviceView, Context context){
        this.mNewDeviceView = newDeviceView;
        mNewDeviceView.setPresenter(this);
        this.context = context;
    }

    public void createDevice(String deviceType, String deviceName, String deviceIP,
                             String devicePort, Drawable deviceImage){

        final Device device = new Device(
                deviceType,
                deviceName,
                deviceIP,
                devicePort,
                deviceImage
                );

        ApiClient.getInstance(context.getApplicationContext()).getApiService()
                .createDevice(device)
                .enqueue(new Callback<Device>() {
                    @Override
                    public void onResponse(Call<Device> call, Response<Device> response) {
                        if (response.body() != null){
                            DummyContent.addDevice(response.body());
                        }
                        mNewDeviceView.deviceReady(response.body());
                    }

                    @Override
                    public void onFailure(Call<Device> call, Throwable t) {
                        //// TODO: 2017-04-05 throw error, display message to user
                        mNewDeviceView.showRequestFailed(t.getMessage());
                    }
                });
    }

    @Override
    public void start() {

    }
}
