package com.example.kyle.joulieapp.Presenters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.example.kyle.joulieapp.Contracts.UsageContract;
import com.example.kyle.joulieapp.FilterListAdapter;
import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.Models.Usage;
import com.example.kyle.joulieapp.Models.UsageResponse;
import com.example.kyle.joulieapp.R;
import com.example.kyle.joulieapp.Api.ApiClient;
import com.example.kyle.joulieapp.Utils.Tools;
import com.example.kyle.joulieapp.Views.DeviceDetailActivity;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kyle on 2017-04-05.
 */

public class UsagePresenter implements UsageContract.Presenter {

    private static final int HOURS_IN_DAY = 24;
    private static final int HOURS_IN_WEEK = 24;
    private static final int HOURS_IN_MONTHS = 24;
    private static final int HOURS_IN_YEAR = 24;

    public static final int DAY_FORMAT = 0;
    public static final int WEEK_FORMAT = 1;
    public static final int MONTH_FORMAT = 2;
    public static final int YEAR_FORMAT = 3;
    public static final int MAX_FORMAT = 4;


    private final Context context;
    private final UsageContract.View mUsageView;
    private final SharedPreferences mSharedPreferences;
    private FilterListAdapter adapter;
    private boolean mFirstLoad = true;
    private float estimatedCost;
    private float totalUsage;
    private int numHours;
    private int chartFormat;

    @Override
    public void start() {
        updateUsageCost();
       // loadUsages(false);

    }

    @Override
    public void loadUsages(boolean forceUpdate) {
        // Simplification for sample: a network reload will be forced on first load.
        getUsages(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    public UsagePresenter(UsageContract.View usageView, SharedPreferences sharedPreferences, Context context){
        this.mUsageView = usageView;
        this.mSharedPreferences = sharedPreferences;
        this.context = context;
        this.adapter = new FilterListAdapter(context, R.layout.filter_list_item , DummyContent.MY_DEVICES);
        this.mUsageView.setPresenter(this);
    }

    public void getUsages(boolean forceUpdate, final boolean showLoadingUI){

        if(forceUpdate) {
            ApiClient.getInstance(context.getApplicationContext()).getApiService()
                    .getUsages()
                    .enqueue(new Callback<List<UsageResponse>>() {
                        @Override
                        public void onResponse(Call<List<UsageResponse>> call, Response<List<UsageResponse>> response) {
                            DummyContent.setMyUsages(response.body());
                            processUsages(response.body());
                        }

                        @Override
                        public void onFailure(Call<List<UsageResponse>> call, Throwable t) {
                            //// TODO: 2017-04-05 throw error, display message to user
                            mUsageView.showRequestFailed(t.getMessage());
                        }
                    });
        } else {
            if(DummyContent.MY_USAGES != null){
                if(!DummyContent.MY_USAGES.isEmpty()){
                    processUsages(DummyContent.MY_USAGES);
                }
            }

        }
    }

    private void processUsages(List<UsageResponse> usageDataSet){
        List<ILineDataSet> chartDataSets = new ArrayList<>();
        LineData data = new LineData();
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

        if(usageDataSet != null && !usageDataSet.isEmpty()) {

            for(UsageResponse u: usageDataSet){
                List<Entry> usageData = new ArrayList<>();
                Device device = Tools.findDeviceByID(u.getDeviceID());


                if(device != null) {
                    //set usage data for that device
                    device.setDeviceUsage(u.getUsages());
                    for (Usage ux : u.getUsages()) {
                        if(isTimestampNeeded(ux.getTimestamp())) {
                            dataCount++;
                            totalUsage += ux.getValue(); // * diffHours;
                            usageData.add(new Entry(ux.getTimestamp(), ux.getValue()));
                        }
                    }

                    totalKwh = (totalUsage / dataCount);
                    totalKwh = totalKwh * numHours;
                    totalKwh = (float)(Math.round(totalKwh * 100d) / 100d);

                    estimatedCost = ((totalKwh * cost) / 100);
                    estimatedCost = (float)(Math.round(estimatedCost * 100d) / 100d);

                    device.totalKwh = totalKwh;
                    device.estimatedCost = estimatedCost;

                    mUsageView.showTotals(totalKwh, estimatedCost);
                    LineDataSet dataSetKilowatt = new LineDataSet(usageData, device.getDeviceName());
                    dataSetKilowatt.setLineWidth(3);
                  //  dataSetKilowatt.setDrawFilled(true);
                    dataSetKilowatt.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
                    dataSetKilowatt.setDrawCircles(false);
                    dataSetKilowatt.setDrawValues(false);
                    dataSetKilowatt.setColor(device.getColor());
                    chartDataSets.add(dataSetKilowatt);
                }
            }

            data = new LineData(chartDataSets);

        }
        mUsageView.showUsages(data);
    }

    private void updateUsageCost(){

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
    public void openFilter(View view){
        ListView lv = (ListView) view.findViewById(R.id.lv_filter);
        lv.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        lv.setItemsCanFocus(true);
      //  lv.setDividerHeight(20);
        lv.setMinimumHeight(80);
        lv.setAdapter(adapter);

        for ( int i=0; i < lv.getChildCount(); i++) {
            lv.setItemChecked(i, true);
        }

        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Shown Devices")
                .setView(view)
                //.setAdapter(adapter, null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO: 2017-04-17 on click
                        //  Your code when user clicked on OK
                        //  You can write the code  to save the selected item here
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on Cancel
                    }
                }).create();

        dialog.show();
    }

    @Override
    public void setChartTimeSpan(int timeSpan) {
        Calendar c = Calendar.getInstance();
        switch (timeSpan){
            case DAY_FORMAT:
                chartFormat = DAY_FORMAT;
                numHours = 24;
                getUsages(false, false);
                mUsageView.setChartFormatter(DAY_FORMAT);
                break;
            case WEEK_FORMAT:
                chartFormat = WEEK_FORMAT;
                numHours = 168;
                getUsages(false, false);
                mUsageView.setChartFormatter(WEEK_FORMAT);
                break;
            case MONTH_FORMAT:
                chartFormat = MONTH_FORMAT;
                int monthMaxDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                numHours = (monthMaxDays * 24);
                getUsages(false, false);
                mUsageView.setChartFormatter(MONTH_FORMAT);
                break;
            case YEAR_FORMAT:
                chartFormat = YEAR_FORMAT;
                int yearMaxDays = c.getActualMaximum(Calendar.DAY_OF_YEAR);
                numHours = (yearMaxDays * 24);
                getUsages(false, false);
                mUsageView.setChartFormatter(YEAR_FORMAT);
                break;
            case MAX_FORMAT:
                chartFormat = MAX_FORMAT;
                break;
        }
    }

    @Override
    public void updateCost() {
        processUsages(DummyContent.MY_USAGES);
    }


}
