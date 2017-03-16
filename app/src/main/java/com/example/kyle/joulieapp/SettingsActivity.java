package com.example.kyle.joulieapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.MenuItem;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {

    private EditText editTextOnPeakStartTime;
    private EditText editTextOnPeakEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        editTextOnPeakStartTime = (EditText) findViewById(R.id.onPeakStartTime_input);
        editTextOnPeakEndTime = (EditText) findViewById(R.id.onPeakEndTime_input);

        //set input filter for time inputs so only valid input can be entered
        initTimeInput();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initTimeInput(){
        editTextOnPeakStartTime = (EditText) findViewById(R.id.onPeakStartTime_input);
        editTextOnPeakEndTime = (EditText) findViewById(R.id.onPeakEndTime_input);

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

        editTextOnPeakStartTime.setFilters(timeFilter);
        editTextOnPeakEndTime.setFilters(timeFilter);
    }
}
