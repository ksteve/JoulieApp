package com.example.kyle.joulieapp.Contracts;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.example.kyle.joulieapp.Base.BasePresenter;
import com.example.kyle.joulieapp.Base.BaseView;
import com.example.kyle.joulieapp.Models.Device;

/**
 * Created by Kyle on 2017-04-16.
 */

public interface NewDeviceContract {

    interface View extends BaseView<Presenter> {

        void deviceReady(@NonNull Device device);

        void showRequestFailed(String message);

        void showRequestSuccess(String message);

    }

    interface Presenter extends BasePresenter {

        void createDevice(String deviceType, String deviceName, String deviceIP, String devicePort, Drawable deviceImage);

    }
}
