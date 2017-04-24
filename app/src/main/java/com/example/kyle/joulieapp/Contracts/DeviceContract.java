package com.example.kyle.joulieapp.Contracts;

import android.support.annotation.NonNull;

import com.example.kyle.joulieapp.Base.BasePresenter;
import com.example.kyle.joulieapp.Base.BaseView;
import com.example.kyle.joulieapp.Models.Device;

import java.util.Collection;
import java.util.List;

/**
 * Created by Kyle on 2017-04-16.
 */

public interface DeviceContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showDevices(List<Device> devices);

        void showAddDevice();

        void showDeviceRemoved(String deviceID);

        void updateRemoveDevicesButton(boolean show);

        void updateDevicePowerButton(boolean state, String message);

        void showDeviceDetailsUi(int deviceId);

        void showNoDevices();

        void refreshList();

        void showRequestFailed(String message);

        void showRequestSuccess(String message);
    }

    interface Presenter extends BasePresenter {

        void deleteDevice(@NonNull Device device);

        void deleteDevices(@NonNull Collection<Device> devices);

        void loadDevices(boolean forceUpdate);

        void showRemoveDevices(boolean show);

        void openDeviceDetails(@NonNull Device device);

        void addNewDevice();

        void toggleDevicePower(@NonNull Device device, final boolean state);

    }
}
