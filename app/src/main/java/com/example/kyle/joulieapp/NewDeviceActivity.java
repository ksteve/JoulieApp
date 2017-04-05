package com.example.kyle.joulieapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.api.ApiClient;
import com.example.kyle.joulieapp.api.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewDeviceActivity extends AppCompatActivity {

    //controls
    private EditText deviceNameView;
    private EditText deviceIP;
    private EditText devicePort;
    private ImageView deviceImageView;
    private Drawable defaultDeviceImage;
    private int deviceType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Add Device");
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        deviceType = intent.getIntExtra(DeviceTypeActivity.DEVICE_TYPE, Constants.TYPE_WEMO);

        deviceImageView = (ImageView) findViewById(R.id.deviceImage);
        deviceNameView = (EditText) findViewById(R.id.device_name);

        deviceIP = (EditText) findViewById(R.id.ip_address);
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start,
                                       int end, Spanned dest, int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, dstart) +
                            source.subSequence(start, end) +
                            destTxt.substring(dend);
                    if (!resultingTxt.matches ("^\\d{1,3}(\\." +
                            "(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                        return "";
                    } else {
                        String[] splits = resultingTxt.split("\\.");
                        for (int i=0; i<splits.length; i++) {
                            if (Integer.valueOf(splits[i]) > 255) {
                                return "";
                            }
                        }
                    }
                }
                return null;
            }
        };

        deviceIP.setFilters(filters);
        devicePort = (EditText) findViewById(R.id.port);
        defaultDeviceImage = ContextCompat.getDrawable(NewDeviceActivity.this, R.drawable.ic_smart_plug);
        setUpDeviceImage();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (deviceNameView.getText().toString().trim().isEmpty()) {
                    deviceNameView.setError("Device Name is Required");
                } else {


                    final Device device = new Device(
                            deviceType,
                            deviceNameView.getText().toString(),
                            deviceIP.getText().toString(),
                            devicePort.getText().toString(),
                            defaultDeviceImage);

                    ApiService apiService = ApiClient
                            .getInstance(getApplicationContext())
                            .getApiService();



                    Call<Device> call = apiService.createDevice(device);
                    call.enqueue(new Callback<Device>() {
                        @Override
                        public void onResponse(Call<Device> call, Response<Device> response) {
                            /// TODO: 2017-04-01 check respsonse status
                            // TODO: 2017-04-01 check if device has correct fields
                            String message = "";
                            int result;
                            if(response.body() != null){
                                DummyContent.MY_DEVICES.add(response.body());
                                message = "Device Connected Succesfully";
                                result = RESULT_OK;
                            } else {
                                message = "Error: Could not connect to Device";
                                result = RESULT_CANCELED;
                            }

                            Intent data = new Intent();
                            data.putExtra("message", message);
                            setResult(result,data);
                            finish();
                        }

                        @Override
                        public void onFailure(Call<Device> call, Throwable t) {
                            String message = t.getMessage();
                            Intent data = new Intent();
                            data.putExtra("message", message);
                            setResult(RESULT_CANCELED,data);
                            finish();
                        }
                    });
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpDeviceImage(){
        if(deviceType == Device.TYPE_WEMO){
            deviceImageView.setImageDrawable(getResources().getDrawable(R.drawable.wemo_device));
        } else if(deviceType == Device.TYPE_TPLINK){
            deviceImageView.setImageDrawable(getResources().getDrawable(R.drawable.tplink_device));
        }
    }
}
