package com.example.kyle.joulieapp;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.Models.Rule;

import java.util.ArrayList;
import java.util.List;

public class NewRuleActivity extends AppCompatActivity implements RuleFragment.OnListFragmentInteractionListener{

    private EditText editTextTime;
    private Spinner socketDropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_rule);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Create New Rule");
        ab.setDisplayHomeAsUpEnabled(true);

        //set input filter for time input so only valid input can be entered
        initTimeInput();

        //start socket dropdown with one entry for socket 1
        socketDropdown = (Spinner) findViewById(R.id.socket_dropdown);
        List<String> list = new ArrayList<String>();
        list.add("1");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        socketDropdown.setAdapter(dataAdapter);
    }

    @Override
    public void onListFragmentInteraction(Rule item) {
        DummyContent.notify = true;
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

}
