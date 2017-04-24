package com.example.kyle.joulieapp.Contracts;

import android.view.View;

import com.example.kyle.joulieapp.Base.BasePresenter;
import com.example.kyle.joulieapp.Base.BaseView;
import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.UsageResponse;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.List;

/**
 * Created by Kyle on 2017-04-16.
 */

public interface UsageContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showUsages(LineData data);

        void showTotals(float totalUsage, float totalCost);

        void showNoUsage();

        void refreshList();

        void showRequestFailed(String message);

        void showRequestSuccess(String message);
    }

    interface Presenter extends BasePresenter {

        void loadUsages(boolean forceUpdate);

        void openFilter(android.view.View view);

    }
}
