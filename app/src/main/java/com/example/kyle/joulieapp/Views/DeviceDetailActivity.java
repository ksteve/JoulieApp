package com.example.kyle.joulieapp.Views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.preference.PreferenceManager;

import com.example.kyle.joulieapp.Contracts.DeviceDetailContract;
import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.Presenters.DeviceDetailPresenter;
import com.example.kyle.joulieapp.R;
import com.example.kyle.joulieapp.Utils.DateAxisValueFormatter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DeviceDetailActivity extends AppCompatActivity implements DeviceDetailContract.View {

    private DeviceDetailContract.Presenter mDeviceDetailPresenter;
    private Device currentDevice;
    private ImageView device_image;
    private TextView deviceName;
    private TextView deviceType;

    private TextView tvTotalCostView;
    private TextView lblTotalCost;
    private TextView tvTotalUsageView;
    private TextView lblTotalUsage;
    private TextView tvUsageTrend;
    private TextView tvCostTrend;

    private LineChart mLineChart;
    private RadioGroup rgChartDisplay;
    private RadioButton rbKilowatt;
    private RadioButton rbDollars;
    private float fCost;
    private List<ILineDataSet> dataSets;
    private LineDataSet dataSetKilowatt;
    private LineDataSet dataSetDollars;
    private FloatingActionButton fabShare;
    private SharedPreferences prefs;
    private TabLayout tabLayout;
    private static final int DAY_FORMAT = 0;
    private static final int WEEK_FORMAT = 1;
    private static final int MONTH_FORMAT = 2;
    private static final int YEAR_FORMAT = 3;
    private static final int MAX_FORMAT = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);
        Bundle extras = getIntent().getExtras();

        int device_index = 0;

        if (extras != null) {
            device_index = extras.getInt("index");
            currentDevice = DummyContent.MY_DEVICES.get(device_index);
            //The key argument here must match that used in the other activity
        }
        //Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(currentDevice.getDeviceName());
        ab.setDisplayHomeAsUpEnabled(true);

        deviceType = (TextView) findViewById(R.id.device_type);
        deviceType.setText(currentDevice.getTypeName());
        deviceName = (TextView) findViewById(R.id.device_name);
        deviceName.setText(currentDevice.getDeviceName());

        tvTotalUsageView = (TextView) findViewById(R.id.avg_usage);
        tvUsageTrend = (TextView) findViewById(R.id.usage_trend);
        tvTotalCostView = (TextView) findViewById(R.id.avg_cost);
        tvCostTrend = (TextView) findViewById(R.id.cost_trend);
        lblTotalUsage = (TextView) findViewById(R.id.avg_usage_label);
        lblTotalCost = (TextView) findViewById(R.id.avg_cost_label);

        mLineChart = (LineChart) findViewById(R.id.chart);

        device_image = (ImageView) findViewById(R.id.deviceImage);
        setUpDeviceImage();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //setup Tab layout
        tabLayout = (TabLayout) findViewById(R.id.graph_tabs);
        setupTabIcons();
        setupChart();

       // new getUsageData().execute(null, null, null);

        rbKilowatt = (RadioButton) findViewById(R.id.rbKilowatt);
        rbDollars = (RadioButton) findViewById(R.id.rbDollars);
        rgChartDisplay = (RadioGroup) findViewById(R.id.rgChartDisplayType);
        rgChartDisplay.check(rbKilowatt.getId());

        fabShare = (FloatingActionButton) findViewById(R.id.fabShare);
        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(DeviceDetailActivity.this, ShareActivity.class);
                shareIntent.putExtra("index",DummyContent.MY_DEVICES.indexOf(currentDevice));
                startActivity(shareIntent);
            }
        });

        new DeviceDetailPresenter(device_index,this, this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mDeviceDetailPresenter.start();
    }

    @Override
    public void setPresenter(DeviceDetailContract.Presenter presenter) {
        this.mDeviceDetailPresenter = presenter;
    }

    private void setUpDeviceImage(){
        if(currentDevice.getType().equals(Device.TYPE_WEMO) || currentDevice.getType().equals("1")){
            device_image.setImageDrawable(getResources().getDrawable(R.drawable.wemo_device));
        } else if(currentDevice.getType().equals(Device.TYPE_TPLINK) || currentDevice.getType().equals("2")){
            device_image.setImageDrawable(getResources().getDrawable(R.drawable.tplink_device));
        }
    }

    private void setupTabIcons() {
        tabLayout.addTab(tabLayout.newTab().setText("1D"), true);
        tabLayout.addTab(tabLayout.newTab().setText("1W"));
        tabLayout.addTab(tabLayout.newTab().setText("1M"));
        tabLayout.addTab(tabLayout.newTab().setText("1Y"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setChartFormatter(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setChartFormatter(int formatType){
        Calendar cal = Calendar.getInstance();
        long currentTime;
        switch(formatType){
            case DAY_FORMAT:

                lblTotalUsage.setText("Usage Today");
                lblTotalCost.setText("Estimated Cost Today");


                currentTime = cal.getTimeInMillis()/1000;
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                long beginningOfDay = cal.getTimeInMillis()/1000;

                mLineChart.getXAxis().setAxisMinimum(beginningOfDay);
                mLineChart.getXAxis().setAxisMaximum(currentTime);

                mLineChart.getXAxis().setValueFormatter(new DateAxisValueFormatter(DateAxisValueFormatter.DAY));
                mLineChart.getXAxis().setGranularity(3600f);
                mLineChart.fitScreen();
                mLineChart.invalidate();
                break;
            case WEEK_FORMAT:

                lblTotalUsage.setText("Usage This Week");
                lblTotalCost.setText("Estimated Cost This Week");

                currentTime = cal.getTimeInMillis()/1000;

                cal.set(Calendar.DAY_OF_WEEK, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                long beginningOfWeek = cal.getTimeInMillis()/1000;

                mLineChart.getXAxis().setAxisMinimum(beginningOfWeek);
                mLineChart.getXAxis().setAxisMaximum(currentTime);

                mLineChart.getXAxis().setValueFormatter(new DateAxisValueFormatter(DateAxisValueFormatter.WEEK));
                mLineChart.getXAxis().setGranularity(86400f);
                mLineChart.fitScreen();
                mLineChart.invalidate();
                break;
            case MONTH_FORMAT:

                lblTotalUsage.setText("Usage This Month");
                lblTotalCost.setText("Estimated Cost This Month");

                currentTime = cal.getTimeInMillis()/1000;
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                long beginningOfMonth = cal.getTimeInMillis()/1000;

                mLineChart.getXAxis().setAxisMinimum(beginningOfMonth);
                mLineChart.getXAxis().setAxisMaximum(currentTime);

                mLineChart.getXAxis().setValueFormatter(new DateAxisValueFormatter(DateAxisValueFormatter.MONTH));
                mLineChart.getXAxis().setGranularity(259200f);
                mLineChart.fitScreen();
                mLineChart.invalidate();
                break;
            case YEAR_FORMAT:

                lblTotalUsage.setText("Average Usage");
                lblTotalCost.setText("Estimated Cost");

                currentTime = cal.getTimeInMillis()/1000;
                cal.set(Calendar.MONTH, Calendar.JANUARY);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                long oneMonthAgo = cal.getTimeInMillis()/1000;

                mLineChart.getXAxis().setAxisMinimum(oneMonthAgo);
                mLineChart.getXAxis().setAxisMaximum(currentTime);

                mLineChart.getXAxis().setValueFormatter(new DateAxisValueFormatter(DateAxisValueFormatter.YEAR));

                mLineChart.getXAxis().setGranularity(2500000f);
                mLineChart.fitScreen();
                mLineChart.invalidate();
                break;
            case MAX_FORMAT:
                break;
            default:
                break;

        }
    }

    private void setupChart(){
        mLineChart.setNoDataText("No Usage Data Available");
        mLineChart.setDrawGridBackground(false);
        mLineChart.setDrawBorders(false);
        mLineChart.getLegend().setEnabled(false);
        mLineChart.setDragEnabled(false);
        mLineChart.setScaleEnabled(false);
        mLineChart.setPinchZoom(false);
        mLineChart.setContentDescription("");
        Description ds = new Description();
        ds.setEnabled(false);
        mLineChart.setDescription(ds);
        mLineChart.setDoubleTapToZoomEnabled(false);

        //setup y axis
        mLineChart.getAxis(YAxis.AxisDependency.LEFT).setEnabled(false);
        mLineChart.getAxisRight().disableGridDashedLine();

        //setup x axis
        mLineChart.getXAxis().setDrawGridLines(false);
        mLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        setChartFormatter(tabLayout.getSelectedTabPosition());

        rbKilowatt = (RadioButton) findViewById(R.id.rbKilowatt);
        rbDollars = (RadioButton) findViewById(R.id.rbDollars);
        rgChartDisplay = (RadioGroup) findViewById(R.id.rgChartDisplayType);

        rgChartDisplay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                if (checkedId == rbDollars.getId()){
                    setChartData(true);
                }
                else {
                    setChartData(false);
                }
            }
        });

    }

    private void setChartData(boolean dollars){
       // dataSets.clear();

//        if (dollars){
//            dataSets.add(dataSetDollars);
//        }
//        else{
//            dataSets.add(dataSetKilowatt);
//        }
//
//        mLineChart.clear();
//        LineData data = new LineData(dataSets);
//        mLineChart.setData(data);
//        mLineChart.animateXY(500, 500);
//        mLineChart.invalidate(); // refresh
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.device_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_refresh:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setLoadingIndicator(boolean active) {

    }

    @Override
    public void showShareUI() {

    }

    @Override
    public void showUsage(LineData data) {
        mLineChart.setData(data);
        mLineChart.animateXY(500, 500);
        mLineChart.invalidate(); // refresl
    }

    @Override
    public void showRequestFailed(String message) {

    }

    @Override
    public void showRequestSuccess(String message) {

    }
}
