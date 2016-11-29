package com.example.kyle.joulieapp.Models;

import android.content.Context;

import com.example.kyle.joulieapp.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Device> MY_DEVICES = new ArrayList<>();
    public static final List<Usage> MY_USAGES = new ArrayList<>();
    public static final List<Rule> MY_RULES = new ArrayList<>();
    private static Context mContext;
    public static boolean notify = false;

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final List<Device> DEVICE_OPTIONS = new ArrayList<>();
    public static final List<Usage> USAGE_OPTIONS = new ArrayList<>();
    public static final List<Rule> RULE_OPTIONS = new ArrayList<>();

    private static final int COUNT = 5;

    public static void populate(Context context){
        mContext = context.getApplicationContext();

        if(DEVICE_OPTIONS.size() == 0) {
            //Drawable googlePlay = ResourcesCompat.getDrawable(context.getResources(), R.mipmap.ic_google_music, null);
            //Drawable spotify = ResourcesCompat.getDrawable(context.getResources(), R.mipmap.ic_spotify, null);
            //addDeviceOptions(new Device("1", googlePlay, "Google Play"));
            //addRuleOptions(new Rule("1", null,"Howard Stem"));
            //loadUsages();
        }
    }

    private static  void loadUsages(){
        InputStream is = mContext.getResources().openRawResource(R.raw.artist_song);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] RowData = line.split(",");
                addUsageOptions(new Usage("1"));
                // do something with "data" and "value"
            }
        }
        catch (IOException ex) {
            // handle exception
        }
        finally {
            try {
                is.close();
            }
            catch (IOException e) {
                // handle exception
            }
        }
    }


    private static void addDeviceOptions(Device item) {
        DEVICE_OPTIONS.add(item);
    }

    private static void addUsageOptions(Usage item) {
        USAGE_OPTIONS.add(item);
    }

    private static void addRuleOptions(Rule item) {
        RULE_OPTIONS.add(item);
    }


    public static void addDevice(Device item) {
        MY_DEVICES.add(item);
    }

    public static void addUsage(Usage item) {
        MY_USAGES.add(item);
    }

    public static void addRule(Rule item) {
        MY_RULES.add(item);
    }


    public static void removeDevice(Device item) {
        MY_DEVICES.remove(item);
    }

    public static void removeUsage(Usage item) {
        MY_USAGES.remove(item);
    }

    public static void removeRule(Rule item) {
        MY_RULES.remove(item);
    }

}
