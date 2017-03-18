package com.example.kyle.joulieapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.Models.Rule;
import com.example.kyle.joulieapp.utils.CredentialsManager;
import com.example.kyle.joulieapp.utils.JoulieAPI;
import com.example.kyle.joulieapp.utils.VolleyRequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static com.example.kyle.joulieapp.R.styleable.CoordinatorLayout;

public class NewRuleActivity extends AppCompatActivity implements JoulieAPI.ResponseListener{

    private EditText editTextTime;
    private EditText ruleName;
    private Spinner socketDropdown;
    private Spinner deviceDropdown;
    private ToggleButton turnOnOff;
    private List<String> deviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_rule);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Create New Rule");
        ab.setDisplayHomeAsUpEnabled(true);

        ruleName = (EditText) findViewById(R.id.ruleName_input);
        editTextTime = (EditText) findViewById(R.id.time_input);

        //set input filter for time input so only valid input can be entered
        initTimeInput();

        //start socket dropdown with one entry for socket 1
        socketDropdown = (Spinner) findViewById(R.id.socket_dropdown);
        List<String> socketList = new ArrayList<String>();
        socketList.add("1");
        ArrayAdapter<String> socketDataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, socketList);
        socketDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        socketDropdown.setAdapter(socketDataAdapter);

        //populate device dropdown with device names
        deviceDropdown = (Spinner) findViewById(R.id.device_dropdown);
        deviceList = new ArrayList<String>();
        for (int i = 0; i < DummyContent.MY_DEVICES.size(); i++){
            deviceList.add(DummyContent.MY_DEVICES.get(i).getDeviceName());
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

                if (deviceList.size() == 0 || ruleName.getText().toString().trim().isEmpty() || editTextTime.getText().toString().trim().isEmpty()) {
                    if (deviceList.size() == 0){
                        Toast.makeText(getApplicationContext(), "Error: no devices found", Toast.LENGTH_SHORT).show();
                    }
                    if (ruleName.getText().toString().trim().isEmpty()) {
                        ruleName.setError("Rule Name is Required");
                    }
                    if (editTextTime.getText().toString().trim().isEmpty()) {
                        editTextTime.setError("Time is Required");
                    }
                }
                else {
                    JoulieAPI.getInstance().registerListener(NewRuleActivity.this);
                    JoulieAPI.getInstance().restRequest(
                            VolleyRequestQueue.getInstance(getApplicationContext()).getRequestQueue(),
                            CredentialsManager.getCredentials(getApplicationContext()).getIdToken());
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

    private void initTimeInput(){
        editTextTime = (EditText) findViewById(R.id.time_input);

        //the following InputFilter related code was borrowed from
        //http://stackoverflow.com/questions/13120947/how-to-restrict-to-input-time-for-edittext-in-android
        //this code is used to filter input for the time so that only valid times can be entered
        InputFilter[] timeFilter = new InputFilter[1];

        timeFilter[0]   = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                                       int dstart, int dend) {


                if (source.length() == 0) {
                    return null;// deleting, keep original editing
                }
                String result = "";
                result += dest.toString().substring(0, dstart);
                result += source.toString().substring(start, end);
                result += dest.toString().substring(dend, dest.length());

                if (result.length() > 5) {
                    return "";// do not allow this edit
                }
                boolean allowEdit = true;
                char c;

                if (result.length() > 0) {
                    c = result.charAt(0);
                    allowEdit &= (c >= '0' && c <= '2' && !(Character.isLetter(c)));
                }
                if (result.length() > 1) {
                    //modified borrowed code here to fix bug that allowed
                    //invalid inputs like 24 to 29 for hour
                    char ch;
                    c = result.charAt(1);
                    ch = result.charAt(0);
                    if (ch == '2'){
                        allowEdit &= (c >= '0' && c <= '3' && !(Character.isLetter(c)));
                    }
                    else{
                        allowEdit &= (c >= '0' && c <= '9' && !(Character.isLetter(c)));
                    }
                }
                if (result.length() > 2) {
                    c = result.charAt(2);
                    allowEdit &= (c == ':'&&!(Character.isLetter(c)));
                }
                if (result.length() > 3) {
                    c = result.charAt(3);
                    allowEdit &= (c >= '0' && c <= '5' && !(Character.isLetter(c)));
                }
                if (result.length() > 4) {
                    c = result.charAt(4);
                    allowEdit &= (c >= '0' && c <= '9'&& !(Character.isLetter(c)));
                }
                return allowEdit ? null : "";
            }
        };

        editTextTime.setFilters(timeFilter);
    }

    @Override
    public void onResSuccess(JSONObject response) {
        ruleName = (EditText) findViewById(R.id.ruleName_input);
        editTextTime = (EditText) findViewById(R.id.time_input);
        socketDropdown = (Spinner) findViewById(R.id.socket_dropdown);
        turnOnOff = (ToggleButton) findViewById(R.id.toggleButton);
        int nOnOff = 0;
        Device dev = null;

        for (int i = 0; i < DummyContent.MY_DEVICES.size(); i++){
            if (deviceDropdown.getSelectedItem().toString() == DummyContent.MY_DEVICES.get(i).getDeviceName()){
                dev = DummyContent.MY_DEVICES.get(i);
            }
        }


        if (turnOnOff.isChecked()){
            nOnOff = 1;
        }

        try {
            String result;
            if(response.has("error")){
                result = response.getString("error");
            } else if (response.has("result")) {
                result = response.getString("result");
                DummyContent.addRule(new Rule(UUID.randomUUID().toString(), ruleName.getText().toString(), dev, nOnOff, editTextTime.getText().toString(), Integer.parseInt(socketDropdown.getSelectedItem().toString())));
                Intent resultIntent = new Intent();
                resultIntent.putExtra("result", result);
                setResult(Activity.RESULT_OK, resultIntent);
            } else {
                DummyContent.addRule(new Rule(UUID.randomUUID().toString(), ruleName.getText().toString(), dev, nOnOff, editTextTime.getText().toString(), Integer.parseInt(socketDropdown.getSelectedItem().toString())));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Sorting rules by device name to keep rules for each device together
        Collections.sort(DummyContent.MY_RULES, new Comparator<Rule>() {
            @Override
            public int compare(Rule rule2, Rule rule1)
            {

                return  rule2.device.getDeviceName().compareTo(rule1.device.getDeviceName());
            }
        });
        finish();
    }

    @Override
    public void onResError(String errorMessage) {
        finish();
    }

}
