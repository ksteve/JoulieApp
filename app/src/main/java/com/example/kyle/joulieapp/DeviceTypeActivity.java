package com.example.kyle.joulieapp;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import com.example.kyle.joulieapp.Models.Device;

public class DeviceTypeActivity extends AppCompatActivity {

    private CardView tplink;
    private CardView wemo;
    final static String DEVICE_TYPE = "Device Type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_type);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Choose a Device Type");
        ab.setDisplayHomeAsUpEnabled(true);

        tplink = (CardView) findViewById(R.id.tplink_card);
        wemo = (CardView) findViewById(R.id.wemo_card);


        wemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewActivity(Device.TYPE_WEMO);
            }
        });

        tplink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewActivity(Device.TYPE_TPLINK);
            }
        });
    }

    void startNewActivity(int DeviceType){
        Intent intent = new Intent(DeviceTypeActivity.this, NewDeviceActivity.class);
        intent.putExtra(DEVICE_TYPE, DeviceType);
        startActivity(intent);
       // finish();
    }

}
