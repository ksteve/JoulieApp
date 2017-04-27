package com.example.kyle.joulieapp.Views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.kyle.joulieapp.Contracts.NewRuleContract;
import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.Models.Rule;
import com.example.kyle.joulieapp.Presenters.NewRulePresenter;
import com.example.kyle.joulieapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public class NewRuleActivity extends AppCompatActivity implements NewRuleContract.View{

    private TimePicker timePicker;
    private EditText ruleName;
    private Spinner deviceDropdown;
    private ToggleButton turnOnOff;
    private List<String> deviceList;
    private ToggleButton tbtnSn;
    private ToggleButton tbtnM;
    private ToggleButton tbtnT;
    private ToggleButton tbtnW;
    private ToggleButton tbtnTh;
    private ToggleButton tbtnF;
    private ToggleButton tbtnSt;
    private NewRuleContract.Presenter mNewRulePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_rule);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Create New Rule");
        ab.setDisplayHomeAsUpEnabled(true);

        new NewRulePresenter(this, this);

        ruleName = (EditText) findViewById(R.id.ruleName_input);
        timePicker = (TimePicker) findViewById(R.id.timePicker);

        //populate device dropdown with device names
        deviceDropdown = (Spinner) findViewById(R.id.device_dropdown);
        deviceList = new ArrayList<String>();
        for (int i = 0; i < DummyContent.MY_DEVICES.size(); i++){
            if(DummyContent.MY_DEVICES.get(i).getOwned() == 1) {
                deviceList.add(DummyContent.MY_DEVICES.get(i).getDeviceName());
            }
        }
        ArrayAdapter<String> deviceDataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, deviceList);
        deviceDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deviceDropdown.setAdapter(deviceDataAdapter);

        if (deviceList.size() == 0){
            Toast.makeText(getApplicationContext(), "Error: no devices found", Toast.LENGTH_SHORT).show();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deviceList.size() == 0 || ruleName.getText().toString().trim().isEmpty()) {
                    if (deviceList.size() == 0){
                        Toast.makeText(getApplicationContext(), "Error: no devices found", Toast.LENGTH_SHORT).show();
                    }
                    if (ruleName.getText().toString().trim().isEmpty()) {
                        ruleName.setError("Rule Name is Required");
                    }
                }
                else {
                    ruleName = (EditText) findViewById(R.id.ruleName_input);
                    timePicker = (TimePicker) findViewById(R.id.timePicker);
                    turnOnOff = (ToggleButton) findViewById(R.id.toggleButton);
                    int nOnOff = 0;
                    Device dev = null;
                    String localTime = timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();
                    String time = localToGMT(timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                    String days = "";
                    tbtnSn = (ToggleButton) findViewById(R.id.tbtnSn);
                    tbtnM = (ToggleButton) findViewById(R.id.tbtnM);
                    tbtnT = (ToggleButton) findViewById(R.id.tbtnT);
                    tbtnW = (ToggleButton) findViewById(R.id.tbtnW);
                    tbtnTh = (ToggleButton) findViewById(R.id.tbtnTh);
                    tbtnF = (ToggleButton) findViewById(R.id.tbtnF);
                    tbtnSt = (ToggleButton) findViewById(R.id.tbtnSt);

                    for (int i = 0; i < DummyContent.MY_DEVICES.size(); i++){
                        if (deviceDropdown.getSelectedItem().toString().equals(DummyContent.MY_DEVICES.get(i).getDeviceName())){
                            dev = DummyContent.MY_DEVICES.get(i);
                        }
                    }

                    if (turnOnOff.isChecked()){
                        nOnOff = 1;
                    } else {
                        nOnOff = 2;
                    }

                    days += tbtnSn.isChecked() ? "1" : "0";
                    days += tbtnM.isChecked() ? "1" : "0";
                    days += tbtnT.isChecked() ? "1" : "0";
                    days += tbtnW.isChecked() ? "1" : "0";
                    days += tbtnTh.isChecked() ? "1" : "0";
                    days += tbtnF.isChecked() ? "1" : "0";
                    days += tbtnSt.isChecked() ? "1" : "0";
                    days = days.trim();

                    int daysbits = Integer.valueOf(days);
                    int timenum = Integer.valueOf(time);
                    daysbits = Integer.parseInt(days, 2);
                    mNewRulePresenter.createRule(ruleName.getText().toString(), dev, nOnOff, timenum, localTime, daysbits);
                }
            }
        });

    }

    private String localToGMT(int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        Date date = cal.getTime();
        SimpleDateFormat isoFormat = new SimpleDateFormat("HH:mm");
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String utcTime = isoFormat.format(date);
        utcTime = utcTime.replace(":", "");
        return utcTime;
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
    public void setLoadingIndicator(boolean active) {

    }

    @Override
    public void showTitle(String title) {

    }

    @Override
    public void ruleReady(Rule rule) {
      //  DummyContent.addRule(rule);
        finish();
    }

    @Override
    public void showRequestFailed(String message) {
        //TODO: display error
        finish();
    }

    @Override
    public void showRequestSuccess(String message) {

    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void setPresenter(NewRuleContract.Presenter presenter) {
        mNewRulePresenter = presenter;
    }
}
