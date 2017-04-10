package com.example.kyle.joulieapp.presenter;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.api.ApiClient;
import com.example.kyle.joulieapp.api.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kyle on 2017-04-05.
 */

public class NewDevicePresenter {

    private final Context context;
    private final NewDevicePresenterListener mListener;
    private final ApiService apiService;

    public interface NewDevicePresenterListener{
        void deviceReady(Device device);
        void onError(String message);
    }

    public void subscribe(){
        //EventBus.getDefault().register(this);
    }

    public void unsubscribe(){
      //  RxBus.get().unregister(this);
    }

    public NewDevicePresenter(NewDevicePresenterListener listener, Context context){
        this.mListener = listener;
        this.context = context;
        this.apiService = ApiClient.getInstance(this.context).getApiService();
    }

    public void createDevice(int deviceType, String deviceName, String deviceIP,
                             String devicePort, Drawable deviceImage){

        final Device device = new Device(
                deviceType,
                deviceName,
                deviceIP,
                devicePort,
                deviceImage
                );

        apiService
                .createDevice(device)
                .enqueue(new Callback<Device>() {
                    @Override
                    public void onResponse(Call<Device> call, Response<Device> response) {
                        if (response.body() != null){
                            DummyContent.MY_DEVICES.add(response.body());
                        }
                        mListener.deviceReady(response.body());
                    }

                    @Override
                    public void onFailure(Call<Device> call, Throwable t) {
                        //// TODO: 2017-04-05 throw error, display message to user
                        mListener.onError(t.getMessage());
                    }
                });
    }
}
