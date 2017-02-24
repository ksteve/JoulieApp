package com.example.kyle.joulieapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.api.ApiService;
import com.example.kyle.joulieapp.utils.CredentialsManager;
import com.example.kyle.joulieapp.utils.JoulieAPI;
import com.example.kyle.joulieapp.utils.VolleyRequestQueue;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewDeviceActivity extends AppCompatActivity {

    //controls
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
        deviceNameView = (EditText) findViewById(R.id.device_name);

        defaultDeviceImage = ContextCompat.getDrawable(NewDeviceActivity.this, R.mipmap.ic_outlet);
        deviceImageView.setImageDrawable(defaultDeviceImage);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (deviceNameView.getText().toString().trim().isEmpty()) {
                    deviceNameView.setError("Device Name is Required");
                } else {

                    final Device device = new Device("Test-Device", deviceNameView.getText().toString(), defaultDeviceImage);
                    ApiService apiService = ApiService.retrofit.create(ApiService.class);
                    Call<Device> call = apiService.createDevice(device);
                    call.enqueue(new Callback<Device>() {
                        @Override
                        public void onResponse(Call<Device> call, Response<Device> response) {
                            DummyContent.MY_DEVICES.add(device);
//                            try {
//
//                                if(response.){
//                                    result = response.getString("error");
//                                } else if (response.has("result")) {
//                                    result = response.getString("result");
//                                    DummyContent.addDevice(new Device("dsf", deviceNameView.getText().toString(), defaultDeviceImage));
//                                    Intent resultIntent = new Intent();
//                                    resultIntent.putExtra("result", result);
//                                    setResult(Activity.RESULT_OK, resultIntent);
//                                } else {
//                                    DummyContent.addDevice(new Device("dsf", deviceNameView.getText().toString(), defaultDeviceImage));
//                                }
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
                            finish();
                        }

                        @Override
                        public void onFailure(Call<Device> call, Throwable t) {
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
}
