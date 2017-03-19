package com.example.kyle.joulieapp;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.DummyContent;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import java.util.ArrayList;
import java.util.List;

public class DeviceDetailActivity extends AppCompatActivity {

    private Device currentDevice;
    private TextView deviceID;
    private TextView avg_usage;
    private ImageView device_image;
    //private GraphView graph;
    private LineChart mLineChart;
    private String guid = "6b5e49b13c5148b7a50c6c29fd1f282f";

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

        avg_usage = (TextView) findViewById(R.id.avg_usage);
        deviceID = (TextView) findViewById(R.id.deviceID);
        deviceID.setText(currentDevice.getId());
        device_image = (ImageView) findViewById(R.id.deviceImage);
        mLineChart = (LineChart) findViewById(R.id.chart);

        device_image.setImageDrawable(currentDevice.getImage());

        setupChart();

       // new getUsageData().execute(null, null, null);
    }

    private void setupChart(){
        mLineChart.getAxis(YAxis.AxisDependency.LEFT).setEnabled(false);
        mLineChart.getAxisRight().disableGridDashedLine();

        mLineChart.getXAxis().setDrawGridLines(false);
        mLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        mLineChart.setDrawGridBackground(false);

        mLineChart.getLegend().setDrawInside(true);
        mLineChart.getLegend().setYOffset(150);

        //populate some fake hardcoded data to test
        List<Entry> entries = new ArrayList<Entry>();

        entries.add(new Entry(0f, 0.125f));
        entries.add(new Entry(6f, 0.25f));
        entries.add(new Entry(12f, 0.50f));
        entries.add(new Entry(18f, 1.0f));
        entries.add(new Entry(23f, 1.2f));
        entries.add(new Entry(23.75f, 1.25f));


        LineDataSet dataSet = new LineDataSet(entries, "Device1"); // add entries to dataset
        dataSet.setColors(new int[] { R.color.red1}, DeviceDetailActivity.this);
        dataSet.setLineWidth(4);
        //for one line on chart then just use these next 3 lines
        //LineData lineData = new LineData(dataSet);
        //chart.setData(lineData);
        //chart.invalidate(); // refresh

        //add 2nd line
        // use the interface ILineDataSet
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataSet);

        LineData data = new LineData(dataSets);
        mLineChart.setData(data);
        mLineChart.animateXY(500, 500);
        mLineChart.invalidate(); // refresh

    }


    //subscribeClick event handler
    //used to subscribe to a topic
    private void subscribe(){
        final String topic = "pi/sockets_status/" + guid;


    }


    private void publishSwitch(int number, boolean isOn){
       // final String guid = currentDevice.id;
        final String topic = "pi/sockets/" + guid;
        String status = isOn ? "on" : "off";
        final String msg = "{\"deviceID\" : \""+ guid +"\",\"socketNumber\" : " + number + ",\"status\" : \"" + status + "\"}";

        try {

        } catch (Exception e) {

        }
    }



    private class getUsageData extends AsyncTask<Void, String, Integer> {

        private int[] colours = new int[]{Color.GREEN, Color.RED, Color.BLUE, Color.BLACK};
       // Map<String, ArrayList<DataPoint>> deviceData = new HashMap<>();
        float totalUsage = 0;
        int numDataPoints = 0;

        @Override
        protected Integer doInBackground(Void... voids) {

            //PaginatedScanList<PiElectricity> result = DynamoDBManager.getInstance().getUsageData();

//            for (PiElectricity x:result) {
//                try {
//
//                    JSONObject jObject = new JSONObject(x.getDeviceID());
//
//                    String deviceID = jObject.getString("DeviceID");
//                    int timestamp = jObject.getInt("timestamp");
//                    int value = jObject.getInt("value") / 1000;
//
//                    if(deviceID.equals(currentDevice.id)) {
//
//                        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
//                        cal.setTimeInMillis(timestamp);
//                        Date date = cal.getTime();
//                        //String date = DateFormat.format("dd-MM-yyyy", cal).toString();
//                        // return date;
//
//
//                        if (deviceData.containsKey(deviceID)) {
//                            deviceData.get(deviceID).add(new DataPoint(deviceData.get(deviceID).size(), value));
//                        } else {
//                            ArrayList<DataPoint> data = new ArrayList<>();
//                            data.add(new DataPoint(data.size(), value));
//                            deviceData.put(deviceID, data);
//                        }
//                        totalUsage += value;
//                        numDataPoints++;
//                    }
//                }
//                catch (JSONException e){
//                    Log.e(LOG_TAG,
//                            "Json parsing exception",
//                            e);
//                }
//            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer value) {
            super.onPostExecute(value);
            //int i = 0;
            //graph.removeAllSeries();

            // TODO: 2017-03-10 test this code with actual received data
            mLineChart.clearValues();

//            for(Map.Entry<String, ArrayList<DataPoint>> entry: deviceData.entrySet()){
//               // Collections.sort(entry.getValue(), new UsageOverviewFragment.TimeStampComparator());
//                //LineGraphSeries<DataPoint> series = new LineGraphSeries<>(entry.getValue().toArray(new DataPoint[entry.getValue().size()]));
//                //series.setColor(Color.GREEN);
//                //series.setTitle(entry.getKey());
//                //graph.addSeries(series);
//                //i++;
//
//                //populate data
//                List<Entry> entries = new ArrayList<Entry>();
//                for (DataPoint dp: entry.getValue().toArray(new DataPoint[entry.getValue().size()])) {
//                    entries.add(new Entry((float) dp.getX(), (float) dp.getY()));
//                }
//
//                LineDataSet dataSet = new LineDataSet(entries, "Device1"); // add entries to dataset
//                dataSet.setColors(new int[] { R.color.red1}, getApplicationContext());
//
//                // use the interface ILineDataSet
//                List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
//                dataSets.add(dataSet);
//                LineData data = new LineData(dataSets);
//                chart.setData(data);
//                chart.invalidate(); // refresh
//            }
//            float avgUsage = (totalUsage/numDataPoints);
//            avg_usage.setText(String.valueOf(avgUsage) + " kWatts");
        }
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
                publishSwitch(0, true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
