package com.example.kyle.joulieapp.Contracts;

import com.example.kyle.joulieapp.Base.BasePresenter;
import com.example.kyle.joulieapp.Base.BaseView;

/**
 * Created by Kyle on 2017-04-16.
 */

public interface ShareContract {

    interface View extends BaseView<Presenter> {
        void showFoundUser();

        void showRequestFailed(String message);

        void showDeviceShared();
    }

    interface Presenter extends BasePresenter {

        void findUserByEmail(String email);

        void shareDeviceWithUser(String deviceID);

    }
}
