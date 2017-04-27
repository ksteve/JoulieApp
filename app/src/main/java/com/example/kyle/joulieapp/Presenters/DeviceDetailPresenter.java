package com.example.kyle.joulieapp.Presenters;

import android.content.Context;
import android.content.SharedPreferences;
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
import java.util.Calendar;
import java.util.List;

/**
 * Created by Kyle on 2017-04-05.
 */

public class DeviceDetailPresenter implements DeviceDetailContract.Presenter{


    public static final int DAY_FORMAT = 0;
    public static final int WEEK_FORMAT = 1;
    public static final int MONTH_FORMAT = 2;
    public static final int YEAR_FORMAT = 3;
    public static final int MAX_FORMAT = 4;

    private final Context context;
    private final DeviceDetailContract.View mDeviceDetailView;
    private final Device currentDevice;
    private final SharedPreferences mSharedPreferences;
    private boolean mFirstLoad = true;
    private int numHours;
    private int chartFormat;

    public DeviceDetailPresenter(int deviceIndex, DeviceDetailContract.View deviceDetailView, SharedPreferences sharedPreferences, Context context){
        this.context = context;
        this.currentDevice = DummyContent.MY_DEVICES.get(deviceIndex);
        this.mSharedPreferences = sharedPreferences;
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

        LineData data = new LineData();

        if(currentDevice.getDeviceUsage() == null){
            mDeviceDetailView.showUsage(data);
            return;
        }

        if(currentDevice.getDeviceUsage().isEmpty()){
            mDeviceDetailView.showUsage(data);
            // TODO: 2017-04-21 no usage data available for device, request more from server??
            return;
        }

        float totalUsage = 0;
        float totalKwh = 0;
        int dataCount = 0;
        float estimatedCost = 0;
        float cost = 0;
        String mid_peak_cost;

        mid_peak_cost = mSharedPreferences.getString("mid_peak_cost", "");
        if(!mid_peak_cost.isEmpty()){
            cost = Float.valueOf(mid_peak_cost);
        }

        List<ILineDataSet> chartDataSets = new ArrayList<>();
        List<Entry> usageData = new ArrayList<>();

        for (Usage ux : currentDevice.getDeviceUsage()) {
            if(isTimestampNeeded(ux.getTimestamp())) {
                dataCount++;
                totalUsage += ux.getValue();
                usageData.add(new Entry(ux.getTimestamp(), ux.getValue()));
            }
        }

        totalKwh = (totalUsage / dataCount);
        totalKwh = totalKwh * numHours;
        totalKwh = (float)(Math.round(totalKwh * 100d) / 100d);

        estimatedCost = ((totalKwh * cost) / 100);
        estimatedCost = (float)(Math.round(estimatedCost * 100d) / 100d);


        mDeviceDetailView.showTotals(totalKwh, estimatedCost);
        LineDataSet dataSetKilowatt = new LineDataSet(usageData, currentDevice.getDeviceName());
        dataSetKilowatt.setLineWidth(4);
        dataSetKilowatt.setDrawFilled(true);
        dataSetKilowatt.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        //dataSetKilowatt.setCubicIntensity(0.5f);
        dataSetKilowatt.setDrawCircles(false);
        dataSetKilowatt.setDrawValues(false);
        dataSetKilowatt.setColor(currentDevice.getColor());
        chartDataSets.add(dataSetKilowatt);

        data = new LineData(chartDataSets);
        mDeviceDetailView.showUsage(data);

    }

    @Override
    public void setChartTimeSpan(int timeSpan) {
        Calendar c = Calendar.getInstance();
        switch (timeSpan){
            case DAY_FORMAT:
                chartFormat = DAY_FORMAT;
                numHours = 24;
                getDeviceUsage(false, false);
                mDeviceDetailView.setChartFormatter(DAY_FORMAT);
                break;
            case WEEK_FORMAT:
                chartFormat = WEEK_FORMAT;
                numHours = 168;
                getDeviceUsage(false, false);
                mDeviceDetailView.setChartFormatter(WEEK_FORMAT);
                break;
            case MONTH_FORMAT:
                chartFormat = MONTH_FORMAT;
                int monthMaxDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                numHours = (monthMaxDays * 24);
                getDeviceUsage(false, false);
                mDeviceDetailView.setChartFormatter(MONTH_FORMAT);
                break;
            case YEAR_FORMAT:
                chartFormat = YEAR_FORMAT;
                int yearMaxDays = c.getActualMaximum(Calendar.DAY_OF_YEAR);
                numHours = (yearMaxDays * 24);
                getDeviceUsage(false, false);
                mDeviceDetailView.setChartFormatter(YEAR_FORMAT);
                break;
            case MAX_FORMAT:
                chartFormat = MAX_FORMAT;
                break;
        }
    }

    private boolean isTimestampNeeded(float timestamp){

        Calendar c = Calendar.getInstance();

        switch (chartFormat){
            case DAY_FORMAT:

                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);

                long beginningOfDay = c.getTimeInMillis()/1000;

                if(timestamp < beginningOfDay){
                    return false;
                }

                break;
            case WEEK_FORMAT:

                c.set(Calendar.DAY_OF_WEEK, 1);
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                long beginningOfWeek = c.getTimeInMillis()/1000;

                if(timestamp < beginningOfWeek){
                    return false;
                }

                break;
            case MONTH_FORMAT:
                c.set(Calendar.DAY_OF_MONTH, 1);
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                long beginningOfMonth = c.getTimeInMillis()/1000;

                if(timestamp < beginningOfMonth){
                    return false;
                }
                break;
            case YEAR_FORMAT:

                c.set(Calendar.MONTH, Calendar.JANUARY);
                c.set(Calendar.DAY_OF_MONTH, 1);
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                long beginningOfYear = c.getTimeInMillis()/1000;
                if(timestamp < beginningOfYear){
                    return false;
                }
                break;
            case MAX_FORMAT:

//                if(timestamp < beginningOfMonth){
//                    return false;
//                }
                break;
        }


        return true;
    }

    @Override
    public void editDevice(){

    }

    @Override
    public void shareDevice(){

    }




}
