package com.example.kyle.joulieapp;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.Models.PiElectricity;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.example.kyle.joulieapp.LoginActivity.LOG_TAG;

public class DeviceDetailActivity extends AppCompatActivity {

    private Device currentDevice;
    private TextView deviceID;
    private TextView avg_usage;
    private GraphView graph;
    private ToggleButton toggleBtn1;
    private ToggleButton toggleBtn2;
    private ToggleButton toggleBtn3;


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

        ActionBar ab = getSupportActionBar();
        ab.setTitle(currentDevice.deviceName);
        ab.setDisplayHomeAsUpEnabled(true);

        deviceID = (TextView) findViewById(R.id.deviceID);
        deviceID.setText(currentDevice.id);
        graph = (GraphView) findViewById(R.id.graph);
        toggleBtn1 = (ToggleButton) findViewById(R.id.toggle_power1);
        toggleBtn2 = (ToggleButton) findViewById(R.id.toggle_power2);
        toggleBtn3 = (ToggleButton) findViewById(R.id.toggle_power3);

        toggleBtn1 = (ToggleButton) findViewById(R.id.toggle_power1);
        toggleBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publishSwitch(1, toggleBtn1.isChecked());
            }
        });

        toggleBtn2 = (ToggleButton) findViewById(R.id.toggle_power2);
        toggleBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publishSwitch(2, toggleBtn2.isChecked());
            }
        });

        toggleBtn3 = (ToggleButton) findViewById(R.id.toggle_power3);
        toggleBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publishSwitch(3, toggleBtn3.isChecked());
            }
        });

        subscribe();
        new getUsageData().execute(null, null, null);
    }

    private void setControls(boolean status){
        toggleBtn1.setEnabled(status);
        toggleBtn2.setEnabled(status);
        toggleBtn3.setEnabled(status);
       // btnRefresh.setEnabled(status);
    }

    //subscribeClick event handler
    //used to subscribe to a topic
    private void subscribe(){
        final String topic = "pi/sockets_status";

        Log.d(LOG_TAG, "topic = " + topic);

        try {
            LoginActivity.mqttManager.subscribeToTopic(topic, AWSIotMqttQos.QOS0,
                    new AWSIotMqttNewMessageCallback() {
                        @Override
                        public void onMessageArrived(final String topic, final byte[] data) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String message = new String(data, "UTF-8");
                                        Log.d(LOG_TAG, "Message arrived:");
                                        Log.d(LOG_TAG, "   Topic: " + topic);
                                        Log.d(LOG_TAG, " Message: " + message);

                                       //tvLastMessage.setText(message);

                                        JSONObject json = new JSONObject(message);
                                        int number = json.getInt("socketNumber");
                                        String status = json.getString("status");
                                        switch (number) {
                                            case 1:
                                                updateButton(toggleBtn1, status);
                                                break;
                                            case 2:
                                                updateButton(toggleBtn2, status);
                                                break;
                                            case 3:
                                                updateButton(toggleBtn3, status);
                                                break;
                                        }

                                    } catch (UnsupportedEncodingException e) {
                                        Log.e(LOG_TAG, "Message encoding error.", e);
                                    } catch (JSONException e) {
                                        Log.e(LOG_TAG, "Message json format error.", e);
                                    }
                                }
                            });
                        }
                    });
        } catch (Exception e) {
            Log.e(LOG_TAG, "Subscription error.", e);
        }
    }

    private void updateButton(ToggleButton button, String isOn){
        if ("on".equals(isOn)){
            button.setChecked(true);
        }else if ("off".equals(isOn)){
            button.setChecked(false);
        }
    }

    //publishClick event handler
    //used to publish to a topic
    View.OnClickListener publishClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String guid = currentDevice.id;
            final String topic = "pi/sockets";
            String status = toggleBtn1.isChecked() ? "on" : "off";
            final String msg = "{\"deviceID\" : \""+ guid +"\",\"socketNumber\" : 1,\"status\" : \"" + status + "\"}";

            try {
                LoginActivity.mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Publish error.", e);
            }

        }
    };

    private void publishSwitch(int number, boolean isOn){
        final String guid = currentDevice.id;
        final String topic = "pi/sockets";
        String status = isOn ? "on" : "off";
        final String msg = "{\"deviceID\" : \""+ guid +"\",\"socketNumber\" : " + number + ",\"status\" : \"" + status + "\"}";

        try {
            LoginActivity.mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Publish error.", e);
        }
    }



    private class getUsageData extends AsyncTask<Void, String, Integer> {

        private int[] colours = new int[]{Color.GREEN, Color.RED, Color.BLUE, Color.BLACK};
        Map<String, ArrayList<DataPoint>> deviceData = new HashMap<>();
        float totalUsage = 0;
        int numDataPoints = 0;

        @Override
        protected Integer doInBackground(Void... voids) {


            PaginatedScanList<PiElectricity> result = DynamoDBManager.getInstance().getUsageData();

            for (PiElectricity x:result) {
                try {

                    JSONObject jObject = new JSONObject(x.getDeviceID());

                    String deviceID = jObject.getString("DeviceID");
                    int timestamp = jObject.getInt("timestamp");
                    int value = jObject.getInt("value");

                    if(deviceID == currentDevice.id) {

                        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                        cal.setTimeInMillis(timestamp);
                        Date date = cal.getTime();
                        //String date = DateFormat.format("dd-MM-yyyy", cal).toString();
                        // return date;


                        if (deviceData.containsKey(deviceID)) {
                            deviceData.get(deviceID).add(new DataPoint(deviceData.get(deviceID).size(), value));
                        } else {
                            ArrayList<DataPoint> data = new ArrayList<>();
                            data.add(new DataPoint(data.size(), value));
                            deviceData.put(deviceID, data);
                        }
                        totalUsage += value;
                        numDataPoints++;
                    }
                }
                catch (JSONException e){
                    Log.e(LOG_TAG,
                            "Json parsing exception",
                            e);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer value) {
            super.onPostExecute(value);
            int i = 0;
            graph.removeAllSeries();

            for(Map.Entry<String, ArrayList<DataPoint>> entry: deviceData.entrySet()){
               // Collections.sort(entry.getValue(), new UsageOverviewFragment.TimeStampComparator());
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(entry.getValue().toArray(new DataPoint[entry.getValue().size()]));
                series.setColor(Color.GREEN);
                series.setTitle(entry.getKey());
                graph.addSeries(series);
                i++;
            }
            float avgUsage = (totalUsage/numDataPoints);
            avg_usage.setText(String.valueOf(avgUsage) + " Watts");
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
