package com.example.kyle.joulieapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.utils.CredentialsManager;
import com.example.kyle.joulieapp.utils.JoulieAPI;
import com.example.kyle.joulieapp.utils.VolleyRequestQueue;

import org.json.JSONObject;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Device} and makes a call to the
 * specified {@link DeviceFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyDeviceRecyclerViewAdapter extends RecyclerView.Adapter<MyDeviceRecyclerViewAdapter.ViewHolder> {

    private final List<Device> mValues;
    private final DeviceFragment.OnListFragmentInteractionListener mListener;
    private SparseBooleanArray selectedItems;

    public MyDeviceRecyclerViewAdapter(List<Device> items, DeviceFragment.OnListFragmentInteractionListener listener) {
        selectedItems = new SparseBooleanArray();
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_device, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mStreamImage.setImageDrawable(mValues.get(position).image);
        holder.mContentView.setText(mValues.get(position).deviceName);

        //Populate NumberPicker values from minimum and maximum value range
        //Set the minimum value of NumberPicker
        holder.mSpinner.setMinValue(0);
        //Specify the maximum value/number of NumberPicker
        //
        holder.mSpinner.setMaxValue(25);
        holder.mSpinner.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                JoulieAPI.getInstance().registerListener(new JoulieAPI.ResponseListener() {
                    @Override
                    public void onResSuccess(JSONObject response) {

                    }

                    @Override
                    public void onResError(String errorMessage) {

                    }
                });
                JoulieAPI.getInstance().updateTempRequest(
                        VolleyRequestQueue.getInstance(numberPicker.getContext().getApplicationContext()).getRequestQueue(),
                        CredentialsManager.getCredentials(numberPicker.getContext().getApplicationContext()).getIdToken());
            }
        });


        holder.mView.setClickable(true);

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



    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        public final View mView;
        public final ImageView mStreamImage;
        public final TextView mContentView;
        public final Switch mSwitch;
        public final NumberPicker mSpinner;
        //public final ImageButton mRemoveStream;
        public Device mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onListFragmentInteraction(mItem);
                }
            });
            mView.setOnLongClickListener(this);
            mStreamImage = (ImageView) view.findViewById(R.id.streamImage);
            mContentView = (TextView) view.findViewById(R.id.device_name);
            mSpinner = (NumberPicker) view.findViewById(R.id.temp_spinner);
            mSwitch = (Switch) view.findViewById(R.id.my_switch);
            //mRemoveStream = (ImageButton) view.findViewById(R.id.remove_btn);
            //mRemoveStream.setOnClickListener(this);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

        @Override
        public boolean onLongClick(View view) {
            if (selectedItems.get(getAdapterPosition(), false)) {
                selectedItems.delete(getAdapterPosition());
                view.findViewById(R.id.stream_linearlayout).setSelected(false);
            }
            else {
                selectedItems.put(getAdapterPosition(), true);
                view.setSelected(true);
                view.findViewById(R.id.stream_linearlayout).setSelected(true);
               // ((MainActivity)view.getContext()).onCreateOptionsMenu()
            }
            return false;
        }

        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

            builder.setMessage("Delete this device?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    DummyContent.removeDevice(mValues.get(getAdapterPosition()));
                    notifyItemRemoved(getAdapterPosition());
                }
            });
            builder.setNegativeButton("No", null);
            builder.create().show();
        }
    }
}
