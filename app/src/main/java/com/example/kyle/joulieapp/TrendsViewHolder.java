package com.example.kyle.joulieapp;

import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.kyle.joulieapp.Models.Usage;
import com.github.mikephil.charting.charts.LineChart;

/**
 * Created by Kyle on 2017-03-18.
 */

public class TrendsViewHolder extends RecyclerView.ViewHolder{

    public final View mView;
    public final TextView mUsageTrend;
    public Usage mItem;

    public TrendsViewHolder(View view) {
        super(view);
        mView = view;
        //mTitle = (TabLayout) mView.findViewById(R.id.graph_tabs);
        mUsageTrend = (TextView) mView.findViewById(R.id.avg_usage);


        //setupTabs();
        //setupChart();

    }

    @Override
    public String toString() {
        return super.toString() + " '" + "summary" + "'";
    }
}
