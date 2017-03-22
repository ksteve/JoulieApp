package com.example.kyle.joulieapp;

import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.example.kyle.joulieapp.UsageFragment.OnListFragmentInteractionListener;
import com.example.kyle.joulieapp.Models.Usage;
import com.example.kyle.joulieapp.Models.DummyContent;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Usage} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyUsageRecyclerViewAdapter extends RecyclerView.Adapter {

    private final int SUMMARY_TYPE = 0;
    private final int TRENDS_TYPE = 1;
    private final int COSTS_TYPE = 2;
    private final int DEVICES_TYPE = 3;

    private final List<Integer> mValues;
    private final OnListFragmentInteractionListener mListener;
    private SparseBooleanArray whichMedias = new SparseBooleanArray(3);

    public MyUsageRecyclerViewAdapter(List<Usage> items, OnListFragmentInteractionListener listener) {

        mValues = new ArrayList<>();
        //mValues.add(TRENDS_TYPE);
        mValues.add(SUMMARY_TYPE);
      //  mValues.add(COSTS_TYPE);
        //mValues.add(DEVICES_TYPE);

        //mValues = items;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch(viewType){
            case SUMMARY_TYPE:
                view= LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_usage_overview, parent, false);
                return new UsageSummaryViewHolder(view);

            case TRENDS_TYPE:
                view= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.viewholder_trends, parent, false);
                return new TrendsViewHolder(view);

            case COSTS_TYPE:
                view= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_usage_overview, parent, false);
                return new UsageSummaryViewHolder(view);

            case DEVICES_TYPE:
                view= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.viewholder_devices_usage, parent, false);
                return new DevicesUsageViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return mValues.get(position);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        //holder.mItem = mValues.get(position);

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}
