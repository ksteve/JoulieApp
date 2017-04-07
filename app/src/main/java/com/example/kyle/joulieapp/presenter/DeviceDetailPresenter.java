package com.example.kyle.joulieapp.presenter;

import android.content.Context;

import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.Usage;
import com.example.kyle.joulieapp.api.ApiClient;
import com.example.kyle.joulieapp.api.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kyle on 2017-04-05.
 */

public class DeviceDetailPresenter {

    private final Context context;
    private final DeviceDetailPresenterListener mListener;
    private final ApiService apiService;

    public interface DeviceDetailPresenterListener{
        void DeviceEdited();
        void UsageReady(List<Usage> usages);

    }

    public DeviceDetailPresenter(DeviceDetailPresenterListener listener, Context context){
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

    public void getDeviceUsage(){

    }

    public void editDevice(){

    }

    public void shareDevice(){

    }
}
