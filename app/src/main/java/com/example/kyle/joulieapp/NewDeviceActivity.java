package com.example.kyle.joulieapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.transition.Slide;
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
import com.example.kyle.joulieapp.presenter.DevicePresenter;
import com.example.kyle.joulieapp.presenter.NewDevicePresenter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewDeviceActivity extends AppCompatActivity implements
        NewDevicePresenter.NewDevicePresenterListener,
        DeviceTypeFragment.OnTypeSelectedListener,
        NewDeviceFragment.OnAddDeviceListener{

    //controls
    private EditText deviceNameView;
    private EditText deviceIP;
    private EditText devicePort;
    private ImageView deviceImageView;
    private Drawable defaultDeviceImage;
    private int deviceType;
    private static final int DEVICE_TYPE = 1;
    private static final int DEVICE_ADD = 2;

    private static final String TYPE_FRAGMENT = "DeviceTypeFrag";
    private static final String ADD_FRAGMENT = "NewDeviceFrag";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Add a New Device");
        ab.setDisplayHomeAsUpEnabled(true);

        DeviceTypeFragment deviceTypeFragment =
                (DeviceTypeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (deviceTypeFragment == null) {
            // Create the fragment
            deviceTypeFragment = DeviceTypeFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, deviceTypeFragment);
            //transaction.add(deviceTypeFragment, TYPE_FRAGMENT );
            transaction.commit();
        }
    }

    private void replaceFragment(int type){
        NewDeviceFragment newDeviceFragment = NewDeviceFragment.newInstance(type);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newDeviceFragment);
        transaction.addToBackStack(null);
        transaction.setTransition(Slide.MODE_IN);
        transaction.commit();
    }

    @Override
    public void onAddDevice() {

    }

    @Override
    public void deviceReady(Device device) {
        finish();
    }

    @Override
    public void onError(String message) {
        finish();
    }

    @Override
    public void onTypeSelected(int type) {
        replaceFragment(type);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
      //  newDevicePresenter.unsubscribe();
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
