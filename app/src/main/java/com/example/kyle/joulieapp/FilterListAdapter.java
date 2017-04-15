package com.example.kyle.joulieapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.kyle.joulieapp.Models.Device;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Kyle on 2017-04-13.
 */

public class FilterListAdapter extends ArrayAdapter<Device> {

    private CheckBox cb;
    private View vColor;
    private TextView tvName;
    public HashMap<Integer,Boolean> checked = new HashMap<Integer,Boolean>();

    public FilterListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public FilterListAdapter(Context context, int resource, List<Device> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.filter_list_item, parent, false);
        }

        Device device = getItem(position);

        if (device != null) {
            tvName = (TextView) v.findViewById(R.id.tv_deviceName);
            vColor = (View) v.findViewById(R.id.v_color);
            vColor.setBackgroundColor(device.getColor());

            cb = (CheckBox) v.findViewById(R.id.cb_select);
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    checked.put(position, b);
                }
            });

            Boolean isChecked = checked.get(position);

            if(isChecked == null){
                isChecked = true;
            }

            cb.setChecked(isChecked);

            if (tvName != null) {
                tvName.setText(device.getDeviceName());
            }
        }





        return v;
    }

    public  HashMap<Integer, Boolean> getCheckedItems (){
        return checked;
    }

}
