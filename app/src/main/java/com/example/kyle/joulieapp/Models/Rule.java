package com.example.kyle.joulieapp.Models;

import android.graphics.Bitmap;

/**
 * Created by Kyle on 2016-10-30.
 */

public class Rule {

    public final String id;
    public String ruleName;
    public boolean isActive = false;
    public Device device;

    public Rule(String id) {
        this.id = id;
    }


}
