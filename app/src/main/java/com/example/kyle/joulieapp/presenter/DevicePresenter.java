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

    private boolean mFirstLoad = true;

    public interface DevicePresenterListener{
        void showDevices(List<Device> devices);
        void deviceRemoved();
    }

    public DevicePresenter(DevicePresenterListener listener, Context context){
        this.mListener = listener;
        this.context = context;
    }

    public void subscribe(){
       // RxBus.get().register(this);
    }

    public void unsubscribe(){
     //   RxBus.get().unregister(this);
    }

    public void loadDevices(boolean forceUpdate) {
        // Simplification for sample: a network reload will be forced on first load.
        getDevices(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    public void removeDevice(Device device){
        ApiClient.getInstance(context.getApplicationContext()).getApiService()
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

    public void getDevices(boolean forceUpdate, final boolean showLoadingUI){

        if (showLoadingUI) {
       //     mTasksView.setLoadingIndicator(true);
        }
        if (forceUpdate) {
          //  mTasksRepository.refreshTasks();
        }

        ApiClient.getInstance(context.getApplicationContext()).getApiService()
                .getDevices()
                .enqueue(new Callback<List<Device>>() {
                    @Override
                    public void onResponse(Call<List<Device>> call, Response<List<Device>> response) {
                        processDevices(response.body());
                    }

                    @Override
                    public void onFailure(Call<List<Device>> call, Throwable t) {
                        //// TODO: 2017-04-05 throw error, display message to user
                    }
                });
    }


    private void processDevices(List<Device> devices) {
        if (devices.isEmpty()) {
            // Show a message indicating there are no tasks for that filter type.
           // processEmptyTasks();
        } else {
            // Show the list of tasks
            mListener.showDevices(devices);
            // Set the filter label's text.
            //showFilterLabel();
        }
    }

}
