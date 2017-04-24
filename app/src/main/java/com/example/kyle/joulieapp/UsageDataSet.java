package com.example.kyle.joulieapp;

import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;

import com.example.kyle.joulieapp.Models.Usage;
import com.github.mikephil.charting.data.BaseDataSet;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.List;

/**
 * Created by Kyle on 2017-04-19.
 */

public class UsageDataSet extends LineDataSet implements ILineDataSet {


    public UsageDataSet(List<Entry> yVals, String label) {
        super(yVals, label);
    }
}
