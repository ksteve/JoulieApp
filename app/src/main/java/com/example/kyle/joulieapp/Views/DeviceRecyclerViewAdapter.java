package com.example.kyle.joulieapp.Views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.kyle.joulieapp.Contracts.DeviceContract;
import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.R;

import java.util.HashMap;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Device} and makes a call to the
 * specified {@link DeviceFragment.OnDeviceFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class DeviceRecyclerViewAdapter extends RecyclerView.Adapter<DeviceRecyclerViewAdapter.ViewHolder> {

    private List<Device> mValues;
    public final HashMap<String, Device> selectedDevices;
    private final DeviceFragment.OnDeviceFragmentInteractionListener mListener;
    private DeviceItemListener mItemListener;
    private Context mContext;

    public DeviceRecyclerViewAdapter(@NonNull DeviceItemListener itemListener, Context context, List<Device> items, DeviceFragment.OnDeviceFragmentInteractionListener listener) {
        this.mItemListener = itemListener;
        this.mValues = items;
        this.mListener = listener;
        this.selectedDevices = new HashMap<>();
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
        holder.mItem = mValues.get(position);
        if(holder.mItem.getType() == Device.TYPE_WEMO){
            holder.mDeviceImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wemo_device));
            holder.mDeviceType.setText(Device.WEMO_DISPLAY);
        } else if(holder.mItem.getType() == Device.TYPE_TPLINK){
            holder.mDeviceImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.tplink_device));
            holder.mDeviceType.setText(Device.TPLINK_DISPLAY);
        }
        boolean b = selectedDevices.containsKey(mValues.get(position).getId());
        holder.checkBox.setChecked(b);
        holder.mIsShared.setVisibility(holder.mItem.getOwned() == 1 ? View.GONE : View.VISIBLE);
        holder.mDeviceName.setText(mValues.get(position).getDeviceName());

        holder.mSwitch.setChecked(mValues.get(position).getPowerState());
        holder.mColor.setBackgroundColor(holder.mItem.getColor());

        holder.mView.setClickable(true);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    //mListener.onListFragmentInteraction(holder.mItem);
                    mItemListener.onDeviceClick(holder.mItem);
                }
            }
        });

        holder.mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton compoundButton, final boolean b) {
                //mDevicePresenter.toggleDevicePower(holder.mItem, b);
                mItemListener.onTogglePower(holder.mItem, b);
            }
        });

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    selectedDevices.put(mValues.get(position).getId(), mValues.get(position));
                }
                else{
                    selectedDevices.remove(mValues.get(position).getId());
                }
                //mDevicePresenter.showRemoveDevices(selectedDevices.size() > 0);
                mItemListener.onDeviceChecked(selectedDevices.size() > 0);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final ImageView mDeviceImage;
        public final ImageView mIsShared;
        public final TextView mDeviceName;
        public final TextView mDeviceType;
        public final CardView mColor;
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
           // mView.setOnLongClickListener(this);
            mDeviceImage = (ImageView) view.findViewById(R.id.deviceImage);
            mIsShared = (ImageView) view.findViewById(R.id.isShared);
            mDeviceName = (TextView) view.findViewById(R.id.device_name);
            mDeviceType = (TextView) view.findViewById(R.id.device_type);
            mSwitch = (Switch) view.findViewById(R.id.power_switch);
            checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            mColor = (CardView) view.findViewById(R.id.v_color);
            //mRemoveStream = (ImageButton) view.findViewById(R.id.remove_btn);
            //mRemoveStream.setOnClickListener(this);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDeviceName.getText() + "'";
        }

//        @Override
//        public boolean onLongClick(View view) {
//            if (selectedItems.get(getAdapterPosition(), false)) {
//                selectedItems.delete(getAdapterPosition());
//                view.findViewById(R.id.stream_linearlayout).setSelected(false);
//            }
//            else {
//                selectedItems.put(getAdapterPosition(), true);
//                view.setSelected(true);
//                view.findViewById(R.id.stream_linearlayout).setSelected(true);
//                // ((MainActivity)view.getContext()).onCreateOptionsMenu()
//            }
//            return false;
//        }

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

    interface DeviceItemListener {

        void onDeviceClick(Device clickedDeice);

        void onTogglePower(Device device, boolean state);

        void onDeviceChecked(boolean checkState);
    }

}
