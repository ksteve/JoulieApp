package com.example.kyle.joulieapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.api.ApiClient;
import com.example.kyle.joulieapp.api.ApiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Device} and makes a call to the
 * specified {@link DeviceFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyDeviceRecyclerViewAdapter extends RecyclerView.Adapter<MyDeviceRecyclerViewAdapter.ViewHolder> {

    private List<Device> mValues;
    public final List<Device> selectedDevices;
    private final DeviceFragment.OnListFragmentInteractionListener mListener;
    private SparseBooleanArray selectedItems;
    private ApiService apiService;
    private Context mContext;

    public MyDeviceRecyclerViewAdapter(Context context, List<Device> items, DeviceFragment.OnListFragmentInteractionListener listener) {
        selectedItems = new SparseBooleanArray();
        mValues = items;
        mListener = listener;
        apiService = ApiClient.getInstance(context.getApplicationContext()).getApiService();
        selectedDevices = new ArrayList<>();
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_device, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.checkBox.setChecked(false);
        holder.mItem = mValues.get(position);
        if(holder.mItem.getType() == Device.TYPE_WEMO){
            holder.mDeviceImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wemo_device));
            holder.mDeviceType.setText(Device.WEMO_DISPLAY);
        } else if(holder.mItem.getType() == Device.TYPE_TPLINK){
            holder.mDeviceImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.tplink_device));
            holder.mDeviceType.setText(Device.TPLINK_DISPLAY);
        }

        holder.mIsShared.setVisibility(holder.mItem.getOwned() == 1 ? View.GONE : View.VISIBLE);
        holder.mDeviceName.setText(mValues.get(position).getDeviceName());

        holder.mSwitch.setChecked(mValues.get(position).getPowerState());

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

        holder.mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton compoundButton, final boolean b) {

                String state = (b) ? "1" : "0";
                HashMap<String,String> body = new HashMap<>();
                body.put("state", state);
                String device_id = holder.mItem.getId();
                String command = "set_power_state";

                Call<String> call = apiService.sendCommand(device_id, command, body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d("tag", response.message());
                        Snackbar snackbar = Snackbar.make(compoundButton.getRootView(), response.message(), Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("tag", t.getMessage());
                        Snackbar snackbar = Snackbar.make(compoundButton.getRootView(), t.getMessage(), Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                });

            }
        });

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    selectedDevices.add(mValues.get(position));
                }
                else{
                    selectedDevices.remove(mValues.get(position));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setmValues(List<Device> devices){
        this.mValues = devices;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        public final View mView;
        public final ImageView mDeviceImage;
        public final ImageView mIsShared;
        public final TextView mDeviceName;
        public final TextView mDeviceType;
        public final Switch mSwitch;
        //public final ImageButton mRemoveStream;
        public Device mItem;
        public final CheckBox checkBox;

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
            mDeviceImage = (ImageView) view.findViewById(R.id.deviceImage);
            mIsShared = (ImageView) view.findViewById(R.id.isShared);
            mDeviceName = (TextView) view.findViewById(R.id.device_name);
            mDeviceType = (TextView) view.findViewById(R.id.device_type);
            mSwitch = (Switch) view.findViewById(R.id.power_switch);
            checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            //mRemoveStream = (ImageButton) view.findViewById(R.id.remove_btn);
            //mRemoveStream.setOnClickListener(this);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDeviceName.getText() + "'";
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
