package com.example.kyle.joulieapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {


    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_general);

        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        EditTextPreference editTextPref = (EditTextPreference) findPreference("peak_start_time");
        editTextPref.setSummary(sp.getString("peak_start_time", ""));

        EditTextPreference editTextPref1 = (EditTextPreference) findPreference("peak_end_time");
        editTextPref1.setSummary(sp.getString("peak_end_time", ""));

        EditTextPreference editTextPref2 = (EditTextPreference) findPreference("peak_cost");
        editTextPref2.setSummary(sp.getString("peak_cost", ""));

        EditTextPreference editTextPref3 = (EditTextPreference) findPreference("off_peak_cost");
        editTextPref3.setSummary(sp.getString("off_peak_cost", ""));

        initTimeInput();

        EditText editText2 = ((EditTextPreference) findPreference("peak_cost"))
                .getEditText();
        EditText editText3 = ((EditTextPreference) findPreference("off_peak_cost"))
                .getEditText();
        initCostInput(editText2);
        initCostInput(editText3);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        Preference pref = findPreference(key);
        if (pref instanceof EditTextPreference) {
            EditTextPreference etp = (EditTextPreference) pref;
            pref.setSummary(etp.getText());
        }
    }

    private void initTimeInput(){
        // editTextOnPeakStartTime = (EditText) findViewById(R.id.onPeakStartTime_input);
        // editTextOnPeakEndTime = (EditText) findViewById(R.id.onPeakEndTime_input);

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

        //  editTextOnPeakStartTime.setFilters(timeFilter);
        // editTextOnPeakEndTime.setFilters(timeFilter);
        EditText editText = ((EditTextPreference) findPreference("peak_start_time"))
                .getEditText();
        editText.setFilters(timeFilter);

        EditText editText1 = ((EditTextPreference) findPreference("peak_end_time"))
                .getEditText();
        editText1.setFilters(timeFilter);
    }

    private void initCostInput(final EditText et){

        //the following InputFilter related code was borrowed from:
        //https://gist.github.com/gaara87/3607765
        et.setFilters(new InputFilter[] {
                new DigitsKeyListener(Boolean.FALSE, Boolean.TRUE) {
                    int beforeDecimal = 5, afterDecimal = 2;

                    @Override
                    public CharSequence filter(CharSequence source, int start, int end,
                                               Spanned dest, int dstart, int dend) {
                        String temp = et.getText() + source.toString();

                        if (temp.equals(".")) {
                            return "0.";
                        }
                        else if (temp.equals("0")) {
                            return ""; // don't allow beginning with a 0
                        }
                        else if (temp.toString().indexOf(".") == -1) {
                            // no decimal point placed yet
                            if (temp.length() > beforeDecimal) {
                                return "";
                            }
                        } else {
                            temp = temp.substring(temp.indexOf(".") + 1);
                            if (temp.length() > afterDecimal) {
                                return "";
                            }
                        }

                        return super.filter(source, start, end, dest, dstart, dend);
                    }
                }
        });
    }
}
