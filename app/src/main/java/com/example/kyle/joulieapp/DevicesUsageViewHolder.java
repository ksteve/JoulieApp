package com.example.kyle.joulieapp;

import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.kyle.joulieapp.Models.Usage;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kyle on 2017-03-18.
 */

public class DevicesUsageViewHolder extends RecyclerView.ViewHolder {

    public final View mView;
    public final TabLayout mTabLayout;
    public final BarChart mBarChart;
    public Usage mItem;

    public DevicesUsageViewHolder(View view) {
        super(view);
        mView = view;
        mTabLayout = (TabLayout) mView.findViewById(R.id.graph_tabs);
        mBarChart = (BarChart) mView.findViewById(R.id.chart);

        setupTabs();
        setupChart();

    }

    @Override
    public String toString() {
        return super.toString() + " '" + "summary" + "'";
    }

    private void setupTabs() {
        mTabLayout.addTab(mTabLayout.newTab().setText("1D"), true);
        mTabLayout.addTab(mTabLayout.newTab().setText("1W"));
        mTabLayout.addTab(mTabLayout.newTab().setText("1M"));
        mTabLayout.addTab(mTabLayout.newTab().setText("1Y"));
    }

    private void setupChart(){
        mBarChart.getAxis(YAxis.AxisDependency.LEFT).setEnabled(false);
        mBarChart.getAxisRight().disableGridDashedLine();
        mBarChart.getXAxis().setDrawGridLines(false);
        mBarChart.setDrawGridBackground(false);
        //populate some fake hardcoded data to test
        List<BarEntry> entries = new ArrayList<BarEntry>();

        entries.add(new BarEntry(0f, 0.125f));
        entries.add(new BarEntry(6f, 0.25f));
        entries.add(new BarEntry(12f, 0.50f));
        entries.add(new BarEntry(18f, 1.0f));
        entries.add(new BarEntry(23f, 1.2f));
        entries.add(new BarEntry(23.75f, 1.25f));

        BarDataSet dataSet = new BarDataSet(entries, "device 1"); // add entries to dataset
        dataSet.setColors(new int[] { R.color.red1}, mView.getContext());
        //for one line on chart then just use these next 3 lines
        //LineData lineData = new LineData(dataSet);
        //chart.setData(lineData);
        //chart.invalidate(); // refresh

        //add 2nd line
        List<BarEntry> entries2 = new ArrayList<BarEntry>();

        entries2.add(new BarEntry(0f, 0.15f));
        entries2.add(new BarEntry(6f, 0.35f));
        entries2.add(new BarEntry(12f, 0.70f));
        entries2.add(new BarEntry(18f, 1.1f));
        entries2.add(new BarEntry(23f, 1.3f));
        entries2.add(new BarEntry(23.75f, 1.5f));

        BarDataSet dataSet2 = new BarDataSet(entries2, "Device2"); // add entries to dataset
        dataSet2.setColors(new int[] { R.color.blue1}, mView.getContext());
        //dataSet2.setLineWidth(4);

        // use the interface ILineDataSet
        List<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(dataSet);
        dataSets.add(dataSet2);

        BarData data = new BarData(dataSets);
        mBarChart.setData(data);
        mBarChart.animateXY(500, 500);
        mBarChart.invalidate(); // refresh

    }

}
