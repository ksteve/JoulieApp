package com.example.kyle.joulieapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.api.ApiService;
import com.example.kyle.joulieapp.utils.CredentialsManager;
import com.example.kyle.joulieapp.utils.JoulieAPI;
import com.example.kyle.joulieapp.utils.VolleyRequestQueue;

import org.json.JSONObject;

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
        holder.mContentView.setText(mValues.get(position).getDeviceName());

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
            public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {

                String state = (b) ? "1" : "0";

                ApiService apiService = ApiService.retrofit.create(ApiService.class);
                Call<String> call = apiService.sendCommand("Test","Switch", "toggle_power", state);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d("tag", response.message());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("tag", t.getMessage());
                    }
                });

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
            mSwitch = (Switch) view.findViewById(R.id.power_switch);
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
