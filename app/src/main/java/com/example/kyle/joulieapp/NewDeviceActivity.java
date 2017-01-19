package com.example.kyle.joulieapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import com.example.kyle.joulieapp.Models.Device;

public class NewDeviceActivity extends AppCompatActivity {

    //controls
    private EditText idView;
    private EditText deviceNameView;
    private ImageView deviceImageView;

    private Drawable defaultDeviceImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Add Device");
        ab.setDisplayHomeAsUpEnabled(true);

        deviceImageView = (ImageView) findViewById(R.id.deviceImage);
        idView = (EditText) findViewById(R.id.id);
        deviceNameView = (EditText) findViewById(R.id.device_name);

        defaultDeviceImage = ContextCompat.getDrawable(NewDeviceActivity.this, R.mipmap.ic_outlet);
        deviceImageView.setImageDrawable(defaultDeviceImage);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(idView.getText().toString().trim().isEmpty()){
                    idView.setError("Device ID is Required");
                }

                if(deviceNameView.getText().toString().trim().isEmpty()){
                    deviceNameView.setError("Device Name is Required");
                }

                if(!idView.getText().toString().trim().isEmpty() && !deviceNameView.getText().toString().trim().isEmpty()){

                    final Device device = new Device(idView.getText().toString(), deviceNameView.getText().toString(), defaultDeviceImage);
                    if (device != null) {

                        //insert device into database

                    }
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
}
