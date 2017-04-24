package com.example.kyle.joulieapp.Views;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.kyle.joulieapp.Contracts.ShareContract;
import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.R;

import java.util.ArrayList;
import java.util.List;

public class ShareActivity extends AppCompatActivity implements ShareContract.View{

    private ShareContract.Presenter mSharePresenter;
    private Spinner permissionDropdown;
    private Button btnShare;
    private Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Bundle extras = getIntent().getExtras();
        int device_index = -1;
        if (extras != null) {
            device_index = extras.getInt("index");
            //The key argument here must match that used in the other activity
        }

        ActionBar ab = getSupportActionBar();
        if (device_index == -1){
            ab.setTitle("Share");
        }
        else{
            ab.setTitle("Share " + DummyContent.MY_DEVICES.get(device_index).getDeviceName());
        }

        ab.setDisplayHomeAsUpEnabled(true);

        //populate permission dropdown
        permissionDropdown = (Spinner) findViewById(R.id.permission_dropdown);
        List<String> permissionList = new ArrayList<String>();
        permissionList.add("Can edit");
        permissionList.add("Can view only");

        ArrayAdapter<String> permissionDataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, permissionList);
        permissionDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        permissionDropdown.setAdapter(permissionDataAdapter);

        //start with permission dropdown and share button as disabled
        permissionDropdown.setEnabled(false);

        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });

        btnShare = (Button) findViewById(R.id.btnShare);
        btnShare.setEnabled(false);


    }

    @Override
    public void setPresenter(ShareContract.Presenter presenter) {
        mSharePresenter = presenter;
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
    public void showFoundUser() {

    }

    @Override
    public void showRequestFailed(String message) {

    }

    @Override
    public void showDeviceShared() {

    }
}
