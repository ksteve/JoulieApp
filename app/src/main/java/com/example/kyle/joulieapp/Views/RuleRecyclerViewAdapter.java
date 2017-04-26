package com.example.kyle.joulieapp.Views;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.kyle.joulieapp.Contracts.DeviceContract;
import com.example.kyle.joulieapp.Contracts.RuleContract;
import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.Rule;
import com.example.kyle.joulieapp.R;
import com.example.kyle.joulieapp.Views.RuleFragment.OnListFragmentInteractionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Rule} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class RuleRecyclerViewAdapter extends RecyclerView.Adapter<RuleRecyclerViewAdapter.ViewHolder> {

    private List<Rule> mValues;
    private final OnListFragmentInteractionListener mListener;
    public final HashMap<String, Rule> selectedRules;
    private RuleItemListener mItemListener;

    public RuleRecyclerViewAdapter(@NonNull RuleItemListener itemListener, List<Rule> items, OnListFragmentInteractionListener listener) {
        this.mItemListener = itemListener;
        mValues = items;
        mListener = listener;
        selectedRules = new HashMap<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_rule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.checkBox.setChecked(false);

        String onOff = "off";

        if (mValues.get(position).state == 1){
            onOff = "on";
        }

        holder.mItem = mValues.get(position);

        boolean b = selectedRules.containsKey(mValues.get(position).getId());
        holder.checkBox.setChecked(b);

        //holder.mContentView.setText(mValues.get(position).ruleName + ":\n" + mValues.get(position).device.getDeviceName() + "\n" + onOff + "@" + mValues.get(position).runTime + "\n" + mValues.get(position).repeat);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    selectedRules.put(mValues.get(position).getId(), mValues.get(position));
                }
                else{
                    selectedRules.remove(mValues.get(position).getId());
                }

                mItemListener.onRuleChecked(selectedRules.size() > 0);
            }
        });
    }

    public void setmValues(List<Rule> rules){
        this.mValues = rules;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public List<Rule> getItems() {
        return mValues;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final TextView mContentView;
        public Rule mItem;
        public final CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.content);
            checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        }



        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

        @Override
        public void onClick(View view) {

        }
    }


    interface RuleItemListener {

        void onRuleClick(Rule clickedRule);

        void onActivateClicked(Rule rule, boolean state);

        void onRuleChecked(boolean checkState);
    }
}
