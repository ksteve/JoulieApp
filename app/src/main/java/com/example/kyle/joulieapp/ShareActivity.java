package com.example.kyle.joulieapp;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.DummyContent;

import java.util.ArrayList;
import java.util.List;

public class ShareActivity extends AppCompatActivity {

    private Spinner permissionDropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Share");
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
    }
}
