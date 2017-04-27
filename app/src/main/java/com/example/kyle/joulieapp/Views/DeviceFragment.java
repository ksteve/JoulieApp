package com.example.kyle.joulieapp.Views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kyle.joulieapp.Api.JoulieSocketIOAPI;
import com.example.kyle.joulieapp.Base.BasePresenter;
import com.example.kyle.joulieapp.Contracts.DeviceContract;
import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.R;
import com.example.kyle.joulieapp.Presenters.DevicePresenter;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnDeviceFragmentInteractionListener}
 * interface.
 */
public class DeviceFragment extends Fragment implements DeviceContract.View, JoulieSocketIOAPI.ResponseListener{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnDeviceFragmentInteractionListener mListener;
    private  RecyclerView recyclerView;
    private TextView emptyView;
    private DeviceContract.Presenter devicePresenter;
    private boolean showDeleteMenuItem = false;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DeviceFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static DeviceFragment newInstance(int columnCount) {
        DeviceFragment fragment = new DeviceFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }


        new DevicePresenter(this, getActivity());

        JoulieSocketIOAPI socketClient = JoulieSocketIOAPI.getInstance();
        socketClient.registerListener(this);
        socketClient.connect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_list, container, false);

        // Set the adapter
        Context context = view.getContext();

        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        emptyView = (TextView) view.findViewById(R.id.empty_view);

        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        recyclerView.setAdapter(new DeviceRecyclerViewAdapter(mItemListener, getActivity(),DummyContent.MY_DEVICES, mListener));

        if (recyclerView.getAdapter().getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void setPresenter(@NonNull DeviceContract.Presenter presenter) {
        devicePresenter = presenter;
    }

    public DeviceContract.Presenter getPresenter() {
        return this.devicePresenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        devicePresenter.start();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDeviceFragmentInteractionListener) {
            mListener = (OnDeviceFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       // mPresenter.result(requestCode, resultCode);
    }

    @Override
    public void refreshList(){
        devicePresenter.loadDevices(true);
    }

    @Override
    public void showRequestFailed(String message) {
        Snackbar snackbar = Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT);
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }

    @Override
    public void showRequestSuccess(String message) {
        Snackbar snackbar = Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public void notifyAdapter(){
       // ((DeviceRecyclerViewAdapter) recyclerView.getAdapter()).notifyDataSetChanged();

        if (DummyContent.MY_DEVICES.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setLoadingIndicator(boolean active) {

    }

    @Override
    public void showDevices(List<Device> devices) {
        mListener.OnDevicesLoaded();
        DeviceRecyclerViewAdapter adapter = (DeviceRecyclerViewAdapter) recyclerView.getAdapter();
        adapter.setmValues(devices);
        notifyAdapter();
    }

    @Override
    public void showAddDevice() {

    }

    @Override
    public void showDeviceRemoved(String deviceId) {
        ((DeviceRecyclerViewAdapter) recyclerView.getAdapter()).selectedDevices.remove(deviceId);
        ((DeviceRecyclerViewAdapter) recyclerView.getAdapter()).setmValues(DummyContent.MY_DEVICES);
        updateRemoveDevicesButton(false);
        //recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void updateRemoveDevicesButton(boolean show) {
        showDeleteMenuItem = show;
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void updateDevicePowerButton(boolean state, String message) {
        Snackbar snackbar = Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @Override
    public void showDeviceDetailsUi(int deviceId) {
        Intent deviceDetailIntent = new Intent(getContext(), DeviceDetailActivity.class);
        deviceDetailIntent.putExtra("index",deviceId);
        startActivity(deviceDetailIntent);
    }

    @Override
    public void showNoDevices() {

    }

    /**
     * Listener for clicks on tasks in the ListView.
     */
    DeviceRecyclerViewAdapter.DeviceItemListener mItemListener = new DeviceRecyclerViewAdapter.DeviceItemListener() {
        @Override
        public void onDeviceClick(Device clickedDeice) {
            devicePresenter.openDeviceDetails(clickedDeice);
        }

        @Override
        public void onTogglePower(Device device, boolean state) {
            devicePresenter.toggleDevicePower(device, state);
        }

        @Override
        public void onDeviceChecked(boolean checkState) {
            updateRemoveDevicesButton(checkState);
        }
    };

    @Override
    public void onStateUpdate(Device device, Boolean onOrOff) {
        notifyAdapter();
    }

    @Override
    public void onConnected(String message) {

    }

    @Override
    public void onDisconnected(String message) {

    }

    public interface OnDeviceFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Device item);
        void OnDevicesLoaded();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.list_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_delete).setVisible(showDeleteMenuItem);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_delete){
            if (((DeviceRecyclerViewAdapter) recyclerView.getAdapter()).selectedDevices.size() == 0){
                Toast.makeText(getActivity(), "Error: no device(s) selected to remove", Toast.LENGTH_SHORT).show();
                return true;
            }
            devicePresenter.deleteDevices(((DeviceRecyclerViewAdapter) recyclerView.getAdapter()).selectedDevices.values());
            return  true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
