package com.example.kyle.joulieapp.Contracts;

import com.example.kyle.joulieapp.Base.BasePresenter;
import com.example.kyle.joulieapp.Base.BaseView;
import com.github.mikephil.charting.data.LineData;

/**
 * Created by Kyle on 2017-04-16.
 */

public interface DeviceDetailContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showShareUI();

        void setChartFormatter(int formatType);

        void showUsage(LineData data);

        void showTotals(float totalUsage, float totalCost);

        void showRequestFailed(String message);

        void showRequestSuccess(String message);
    }

    interface Presenter extends BasePresenter {

        void editDevice();

        void shareDevice();

        void loadUsages(boolean forceUpdate);

        void setChartTimeSpan(int timeSpan);

    }
}
