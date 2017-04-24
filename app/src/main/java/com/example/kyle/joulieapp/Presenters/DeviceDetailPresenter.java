package com.example.kyle.joulieapp.Presenters;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.kyle.joulieapp.Contracts.DeviceDetailContract;
import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.Models.Usage;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kyle on 2017-04-05.
 */

public class DeviceDetailPresenter implements DeviceDetailContract.Presenter{

    private final Context context;
    private final DeviceDetailContract.View mDeviceDetailView;
    private final Device currentDevice;
    private boolean mFirstLoad = true;

    public DeviceDetailPresenter(int deviceIndex, DeviceDetailContract.View deviceDetailView, Context context){
        this.context = context;
        this.currentDevice = DummyContent.MY_DEVICES.get(deviceIndex);
        this.mDeviceDetailView = deviceDetailView;
        this.mDeviceDetailView.setPresenter(this);
    }

    @Override
    public void start() {
        loadUsages(false);
    }

    @Override
    public void loadUsages(boolean forceUpdate) {
        // Simplification for sample: a network reload will be forced on first load.
        getDeviceUsage(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    public void getDeviceUsage(boolean forceUpdate, final boolean showLoadingUI){
        if(currentDevice.getDeviceUsage().isEmpty()){
            // TODO: 2017-04-21 no usage data available for device, request more from server??
            return;
        }

        float totalUsage = 0;
        List<ILineDataSet> chartDataSets = new ArrayList<>();
        List<Entry> usageData = new ArrayList<>();

        for (Usage ux : currentDevice.getDeviceUsage()) {

            totalUsage += ux.getValue();

            usageData.add(new Entry(ux.getTimestamp(), ux.getValue()));
        }
        LineDataSet dataSetKilowatt = new LineDataSet(usageData, currentDevice.getDeviceName());
        dataSetKilowatt.setLineWidth(4);
        dataSetKilowatt.setDrawFilled(true);
        dataSetKilowatt.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        //dataSetKilowatt.setCubicIntensity(0.5f);
        dataSetKilowatt.setDrawCircles(false);
        dataSetKilowatt.setDrawValues(false);
        dataSetKilowatt.setColor(currentDevice.getColor());
        chartDataSets.add(dataSetKilowatt);

        LineData data = new LineData(chartDataSets);
        mDeviceDetailView.showUsage(data);

    }

    @Override
    public void editDevice(){

    }

    @Override
    public void shareDevice(){

    }




}
