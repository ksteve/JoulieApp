package com.example.kyle.joulieapp;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.Models.Device;

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
        public final ImageButton mRemoveStream;
        public Device mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mView.setOnLongClickListener(this);
            mStreamImage = (ImageView) view.findViewById(R.id.streamImage);
            mContentView = (TextView) view.findViewById(R.id.content);
            mRemoveStream = (ImageButton) view.findViewById(R.id.remove_btn);
            mRemoveStream.setOnClickListener(this);
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
            DummyContent.removeDevice(mValues.get(getAdapterPosition()));
            notifyItemRemoved(getAdapterPosition());
        }
    }
}
