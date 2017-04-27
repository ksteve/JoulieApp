package com.example.kyle.joulieapp.Presenters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.example.kyle.joulieapp.Contracts.DeviceContract;
import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Api.ApiClient;
import com.example.kyle.joulieapp.Models.DummyContent;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kyle on 2017-04-05.
 */

public class DevicePresenter  implements DeviceContract.Presenter {

    private final Context context;
    private final DeviceContract.View mDeciceView;
    private boolean mFirstLoad = true;

    public DevicePresenter(@NonNull DeviceContract.View deviceView, Context context){
        this.mDeciceView = deviceView;
        this.mDeciceView.setPresenter(this);
        this.context = context;
    }

    @Override
    public void start() {
        loadDevices(false);
    }

    @Override
    public void deleteDevice(@NonNull final Device device) {
        ApiClient.getInstance(context.getApplicationContext()).getCloudApiService()
                .deleteDevice(device.getId())
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        DummyContent.MY_DEVICES.remove(device);
                        mDeciceView.showDeviceRemoved(device.getId());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        mDeciceView.showRequestFailed(t.getMessage());
                    }
                });
    }

    @Override
    public void deleteDevices(@NonNull Collection<Device> devices) {
        for (Device device: devices) {
            deleteDevice(device);
        }
    }

    @Override
    public void openDeviceDetails(@NonNull Device device) {
        mDeciceView.showDeviceDetailsUi(DummyContent.MY_DEVICES.indexOf(device));
    }

    @Override
    public void addNewDevice() {

    }


    @Override
    public void toggleDevicePower(@NonNull Device device, final boolean state) {
        String sState = (state) ? "1" : "0";
        HashMap<String,String> body = new HashMap<>();
        body.put("state", sState);

        String device_id = device.getId();
        final String command = "set_power_state";

        Call<String> call = ApiClient.getInstance(context.getApplicationContext()).getApiService().sendCommand(device_id, command, body);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mDeciceView.updateDevicePowerButton(state, response.message());
                Log.d("tag", response.message());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                mDeciceView.updateDevicePowerButton(!state, t.getMessage());
                mDeciceView.showRequestFailed(t.getMessage());
                Log.d("tag", t.getMessage());
            }
        });
    }

    @Override
    public void loadDevices(boolean forceUpdate) {
        // Simplification for sample: a network reload will be forced on first load.
        getDevices(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    @Override
    public void showRemoveDevices(boolean show) {
        mDeciceView.updateRemoveDevicesButton(show);
    }

    public void getDevices(boolean forceUpdate, final boolean showLoadingUI){

        if (showLoadingUI) {
       //     mTasksView.setLoadingIndicator(true);
        }
        if (forceUpdate) {
            //  mTasksRepository.refreshTasks();
            ApiClient.getInstance(context.getApplicationContext()).getApiService()
                    .getDevices()
                    .enqueue(new Callback<List<Device>>() {
                        @Override
                        public void onResponse(Call<List<Device>> call, Response<List<Device>> response) {
                            processDevices(response.body());
                        }

                        @Override
                        public void onFailure(Call<List<Device>> call, Throwable t) {
                            mDeciceView.showRequestFailed(t.getMessage());
                        }
                    });
        }
    }


    private void processDevices(List<Device> devices) {
        if(devices != null) {
            if (devices.isEmpty()) {
                mDeciceView.showNoDevices();
            } else {
                DummyContent.setMyDevices(devices);
                mDeciceView.showDevices(devices);
            }
        }
    }

}
