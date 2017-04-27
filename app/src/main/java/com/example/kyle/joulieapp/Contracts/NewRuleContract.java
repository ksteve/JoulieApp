package com.example.kyle.joulieapp.Contracts;

import android.support.annotation.NonNull;

import com.example.kyle.joulieapp.Base.BasePresenter;
import com.example.kyle.joulieapp.Base.BaseView;
import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.Rule;

/**
 * Created by Kyle on 2017-04-16.
 */

public interface NewRuleContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showTitle(String title);

        void ruleReady(Rule rule);

        void showRequestFailed(String message);

        void showRequestSuccess(String message);

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void createRule(String ruleName, Device device, int turnOnOff, int time, String localTime, int days);

    }
}
