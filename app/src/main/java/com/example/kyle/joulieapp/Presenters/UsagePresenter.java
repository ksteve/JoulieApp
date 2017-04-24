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

    private final Context context;
    private final UsageContract.View mUsageView;
    private final SharedPreferences mSharedPreferences;
    private FilterListAdapter adapter;
    private boolean mFirstLoad = true;
    private float estimatedCost;
    private float totalUsage;

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
                            //mListener.UsagesReady(response.body());
                            processUsages(response.body());
                        }

                        @Override
                        public void onFailure(Call<List<UsageResponse>> call, Throwable t) {
                            //// TODO: 2017-04-05 throw error, display message to user
                            mUsageView.showRequestFailed(t.getMessage());
                        }
                    });
        }
    }

    private void processUsages(List<UsageResponse> usageDataSet){

        List<ILineDataSet> chartDataSets = new ArrayList<>();
        float totalUsage = 0;
        float totalKwh = 0;
        float estimatedCost = 0;
        String mid_peak_cost;
        float cost = 0;

        mid_peak_cost = mSharedPreferences.getString("mid_peak_cost", "");
        if(!mid_peak_cost.isEmpty()){
            cost = Float.valueOf(mid_peak_cost);
        }


        if(usageDataSet != null && !usageDataSet.isEmpty()) {
            List<Entry> usageData = new ArrayList<>();

            for(UsageResponse u: usageDataSet){

                Date previousTime = null;
                Device device = Tools.findDeviceByID(u.getDeviceID());

                if(device != null) {

                    //set usage data for that device
                    device.setDeviceUsage(u.getUsages());

                    for (Usage ux : u.getUsages()) {

                        //mSharedPreferences.get

//                        Calendar calendar = Calendar.getInstance();
//                        calendar.setTimeInMillis((long)ux.getTimestamp() * 1000);
//                        Date usageTime = calendar.getTime();
//
//                        if(previousTime == null){
//                            previousTime = usageTime;
//                        } else {
                            //long diff = previousTime.getTime() - usageTime.getTime();
                            //long diffHours = diff / (60 * 60 * 1000);
                        totalUsage += ux.getValue(); // * diffHours;
                      //  }



                        usageData.add(new Entry(ux.getTimestamp(), ux.getValue()));
                    }

                    totalKwh = totalUsage / u.getUsages().size();
                    totalKwh = totalKwh * 24;

                    totalKwh = (float)(Math.round(totalKwh * 100d) / 100d);
                    estimatedCost = ((totalKwh * cost) / 100);
                    estimatedCost = (float)(Math.round(estimatedCost * 100d) / 100d);

                    device.totalKwh = totalKwh;
                    device.estimatedCost = estimatedCost;

                  //  context.getSharedPreferences()

                    mUsageView.showTotals(totalKwh, estimatedCost);

                    LineDataSet dataSetKilowatt = new LineDataSet(usageData, u.getDeviceID());
                    dataSetKilowatt.setLineWidth(4);
                    dataSetKilowatt.setDrawFilled(true);
                    dataSetKilowatt.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
                    dataSetKilowatt.setDrawCircles(false);
                    dataSetKilowatt.setDrawValues(false);
                    dataSetKilowatt.setColor(device.getColor());
                    chartDataSets.add(dataSetKilowatt);
                }
            }

            LineData data = new LineData(chartDataSets);
            mUsageView.showUsages(data);
        }
    }

    private void updateUsageCost(){

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


}
