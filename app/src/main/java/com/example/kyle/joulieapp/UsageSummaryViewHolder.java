package com.example.kyle.joulieapp;

import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.kyle.joulieapp.Models.Usage;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kyle on 2017-03-18.
 */

public class UsageSummaryViewHolder extends RecyclerView.ViewHolder {

    public final View mView;
    public final TabLayout mTabLayout;
    public final LineChart mLineChart;
    public Usage mItem;

    public UsageSummaryViewHolder(View view) {
        super(view);
        mView = view;
        mTabLayout = (TabLayout) mView.findViewById(R.id.graph_tabs);
        mLineChart = (LineChart) mView.findViewById(R.id.chart);

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
        mLineChart.getAxis(YAxis.AxisDependency.LEFT).setEnabled(false);
        mLineChart.getAxisRight().disableGridDashedLine();

        mLineChart.getXAxis().setDrawGridLines(false);
        mLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        mLineChart.setDrawGridBackground(false);

        mLineChart.getLegend().setDrawInside(true);
        mLineChart.getLegend().setYOffset(150);

        //populate some fake hardcoded data to test
        List<Entry> entries = new ArrayList<Entry>();

        entries.add(new Entry(0f, 0.125f));
        entries.add(new Entry(6f, 0.25f));
        entries.add(new Entry(12f, 0.50f));
        entries.add(new Entry(18f, 1.0f));
        entries.add(new Entry(23f, 1.2f));
        entries.add(new Entry(23.75f, 1.25f));


        LineDataSet dataSet = new LineDataSet(entries, "Device1"); // add entries to dataset
        dataSet.setColors(new int[] { R.color.red1}, mView.getContext());
        dataSet.setLineWidth(4);
        //for one line on chart then just use these next 3 lines
        //LineData lineData = new LineData(dataSet);
        //chart.setData(lineData);
        //chart.invalidate(); // refresh

        //add 2nd line
        List<Entry> entries2 = new ArrayList<Entry>();

        entries2.add(new Entry(0f, 0.15f));
        entries2.add(new Entry(6f, 0.35f));
        entries2.add(new Entry(12f, 0.70f));
        entries2.add(new Entry(18f, 1.1f));
        entries2.add(new Entry(23f, 1.3f));
        entries2.add(new Entry(23.75f, 1.5f));

        LineDataSet dataSet2 = new LineDataSet(entries2, "Device2"); // add entries to dataset
        dataSet2.setColors(new int[] { R.color.blue1}, mView.getContext());
        dataSet2.setLineWidth(4);

        // use the interface ILineDataSet
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataSet);
        dataSets.add(dataSet2);

        LineData data = new LineData(dataSets);
        mLineChart.setData(data);
        mLineChart.animateXY(500, 500);
        mLineChart.invalidate(); // refresh

    }

}
