package com.example.kyle.joulieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
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

import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.utils.DateAxisValueFormatter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DeviceDetailActivity extends AppCompatActivity {

    private Device currentDevice;
    private ImageView device_image;
    private TextView deviceName;
    private TextView deviceType;
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
        if (extras != null) {
            int device_index = extras.getInt("index");
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
    }
    private void setUpDeviceImage(){
        if(currentDevice.getType() == Device.TYPE_WEMO){
            device_image.setImageDrawable(getResources().getDrawable(R.drawable.wemo_device));
        } else if(currentDevice.getType() == Device.TYPE_TPLINK){
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
                if(mLineChart.getData() != null) {
                    mLineChart.getXAxis().setAxisMinimum(mLineChart.getData().getXMin());
                    mLineChart.getXAxis().setAxisMaximum(mLineChart.getData().getXMax());
                }

                mLineChart.getXAxis().setValueFormatter(new DateAxisValueFormatter(DateAxisValueFormatter.DAY));
                mLineChart.getXAxis().setGranularity(3600f);
                mLineChart.fitScreen();
                mLineChart.invalidate();
                break;
            case WEEK_FORMAT:
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
        mLineChart.getAxis(YAxis.AxisDependency.LEFT).setEnabled(false);
        mLineChart.getAxisRight().disableGridDashedLine();

        mLineChart.getXAxis().setDrawGridLines(false);
        mLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        setChartFormatter(tabLayout.getSelectedTabPosition());

        mLineChart.setDrawGridBackground(false);

        mLineChart.getLegend().setDrawInside(true);
        mLineChart.getLegend().setYOffset(150);

        //populate some fake hardcoded data to test
        List<Entry> entriesKilowatt = new ArrayList<Entry>();

        entriesKilowatt.add(new Entry(0f, 0.125f));
        entriesKilowatt.add(new Entry(6f, 0.25f));
        entriesKilowatt.add(new Entry(12f, 0.50f));
        entriesKilowatt.add(new Entry(18f, 1.0f));
        entriesKilowatt.add(new Entry(23f, 1.2f));
        entriesKilowatt.add(new Entry(23.75f, 1.25f));

        String sCost = "0";
        sCost = prefs.getString("peak_cost", "0");
        fCost = Float.parseFloat(sCost) / 100;
        List<Entry> entriesDollars = new ArrayList<>();

        for (int i = 0; i < entriesKilowatt.size(); i++){
            entriesDollars.add(new Entry(entriesKilowatt.get(i).getX(), entriesKilowatt.get(i).getY() * fCost));
        }


        dataSetKilowatt = new LineDataSet(entriesKilowatt, "Device1"); // add entries to dataset
        dataSetKilowatt.setColors(new int[] { R.color.red1}, DeviceDetailActivity.this);
        dataSetKilowatt.setLineWidth(4);

        dataSetDollars = new LineDataSet(entriesDollars, "Device1"); // add entries to dataset
        dataSetDollars.setColors(new int[] { R.color.red1}, DeviceDetailActivity.this);
        dataSetDollars.setLineWidth(4);
        //for one line on chart then just use these next 3 lines
        //LineData lineData = new LineData(dataSet);
        //chart.setData(lineData);
        //chart.invalidate(); // refresh

        //add 2nd line
        // use the interface ILineDataSet
        dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataSetKilowatt);

        LineData data = new LineData(dataSets);
        mLineChart.setData(data);
        mLineChart.animateXY(500, 500);
        mLineChart.invalidate(); // refresh

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
        dataSets.clear();

        if (dollars){
            dataSets.add(dataSetDollars);
        }
        else{
            dataSets.add(dataSetKilowatt);
        }

        mLineChart.clear();
        LineData data = new LineData(dataSets);
        mLineChart.setData(data);
        mLineChart.animateXY(500, 500);
        mLineChart.invalidate(); // refresh
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


}
