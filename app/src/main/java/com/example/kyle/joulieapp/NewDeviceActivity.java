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
import com.example.kyle.joulieapp.Models.DummyContent;

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
                Device device = new Device(idView.getText().toString(), deviceNameView.getText().toString());
                if(device != null) {
                    device.image = deviceImageView.getDrawable();
                    DummyContent.addDevice(device);
                }

                Intent intent=new Intent();
                intent.putExtra("MESSAGE","");
                setResult(1,intent);
                finish();
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
