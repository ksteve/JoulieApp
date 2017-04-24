package com.example.kyle.joulieapp.Views;

import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.kyle.joulieapp.Contracts.NewDeviceContract;
import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Presenters.NewDevicePresenter;
import com.example.kyle.joulieapp.R;

public class NewDeviceActivity extends AppCompatActivity implements
        NewDeviceContract.View,
        DeviceTypeFragment.OnTypeSelectedListener,
        NewDeviceFragment.OnAddDeviceListener {

    //controls
    private NewDeviceContract.Presenter mNewDevicePresenter;
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

        new NewDevicePresenter(this, this);

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

    private void replaceFragment(String type){
        NewDeviceFragment newDeviceFragment = NewDeviceFragment.newInstance(type);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newDeviceFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }



    @Override
    public void deviceReady(Device device) {
        finish();
    }

    @Override
    public void showRequestFailed(String message) {
        finish();
    }

    @Override
    public void showRequestSuccess(String message) {

    }


    @Override
    public void onTypeSelected(String type) {
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

    @Override
    public void setPresenter(NewDeviceContract.Presenter presenter) {
        mNewDevicePresenter = presenter;
    }

    @Override
    public void onAddDevice(String deviceType, String deviceName, String deviceIP, String devicePort, Drawable deviceImage) {
        mNewDevicePresenter.createDevice(deviceType,
                deviceName,
                deviceIP,
                devicePort,
                defaultDeviceImage);
    }
}
