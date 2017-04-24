package com.example.kyle.joulieapp.Models;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import com.example.kyle.joulieapp.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
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
    public static List<Device> MY_DEVICES = new ArrayList<>();
    public static List<UsageResponse> MY_USAGES = new ArrayList<>();
    public static List<Rule> MY_RULES = new ArrayList<>();
    public static List<String> colors = new ArrayList<>();
    private static Context mContext;
    public static boolean notify = false;

    private static final int COUNT = 5;
    private static final String LOG_TAG = "DummyContent";

    public static void init(Context context){
        mContext = context;
        colors.addAll(Arrays.asList(context.getResources().getStringArray(R.array.colors)));
    }

    public static void setMyDevices(List<Device> devices) {
        MY_DEVICES.clear();
        for (Device x: devices) {
            DummyContent.addDevice(x);
        }
    }

    public static void setMyRules(List<Rule> rules){
        MY_RULES = rules;
    }

    public static void addDevice(Device item) {

        MY_DEVICES.add(item);
        //set device color for charts
        int i = MY_DEVICES.indexOf(item);

        if(i == colors.size()){
            i = 0;
        }

        if (i > colors.size()){
            i = (i % colors.size()) - 1;
        }

        if(item.getType() == "1") {
            item.setType(Device.TYPE_WEMO);
        }

        if(item.getType() == "2") {
            item.setType(Device.TYPE_TPLINK);
        }

        item.setColor(Color.parseColor(colors.get(i)));


    }

    public static void addUsage(UsageResponse item) {
        MY_USAGES.add(item);
    }

    public static void addRule(Rule item) {
        MY_RULES.add(item);
    }

    public static void removeDevice(Device item) {
        MY_DEVICES.remove(item);
    }

    public static void removeUsage(UsageResponse item) {
        MY_USAGES.remove(item);
    }

    public static void removeRule(Rule item) {
        MY_RULES.remove(item);
    }

}
