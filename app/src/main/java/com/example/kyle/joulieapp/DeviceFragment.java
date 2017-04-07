package com.example.kyle.joulieapp;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.Rule;
import com.example.kyle.joulieapp.api.ApiClient;
import com.example.kyle.joulieapp.api.ApiService;
import com.example.kyle.joulieapp.presenter.DevicePresenter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class DeviceFragment extends Fragment implements DevicePresenter.DevicePresenterListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private  RecyclerView recyclerView;
    private TextView emptyView;
    private FloatingActionButton fabRemove;
    private DevicePresenter devicePresenter;

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
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        devicePresenter = new DevicePresenter(this, getActivity());
        devicePresenter.subscribe();
        devicePresenter.getDevices();

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
        recyclerView.setAdapter(new MyDeviceRecyclerViewAdapter(getActivity(),DummyContent.MY_DEVICES, mListener));

        if (recyclerView.getAdapter().getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        fabRemove = (FloatingActionButton) view.findViewById(R.id.fabRemove);

        fabRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (final Device device: ((MyDeviceRecyclerViewAdapter) recyclerView.getAdapter()).selectedDevices){
                    ApiService apiService = ApiClient
                            .getInstance(getActivity().getApplicationContext())
                            .getApiService();

                    Call<String> call = apiService.deleteDevice(device.getDeviceName());
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            DummyContent.MY_DEVICES.remove(device);
                            ((MyDeviceRecyclerViewAdapter) recyclerView.getAdapter()).selectedDevices.remove(device);
                            recyclerView.getAdapter().notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            //TODO: show error msg in toast
                        }
                    });
                   // ((MyDeviceRecyclerViewAdapter) recyclerView.getAdapter()).selectedDevices.clear();

                }

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
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
        devicePresenter.unsubscribe();
    }

    public void getDevices() {
        ApiService apiService = ApiClient.getInstance(getActivity().getApplicationContext()).getApiService();
        Call<List<Device>> call = apiService.getDevices();
        call.enqueue(new Callback<List<Device>>() {
            @Override
            public void onResponse(Call<List<Device>> call, Response<List<Device>> response) {
                MyDeviceRecyclerViewAdapter adapter = (MyDeviceRecyclerViewAdapter) recyclerView.getAdapter();
                if(response.body() != null) {
                   DummyContent.setMyDevices(response.body());
                    adapter.setmValues(response.body());
                    //adapter.notifyDataSetChanged();
                    if (recyclerView.getAdapter().getItemCount() == 0) {
                        recyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                    else {
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Device>> call, Throwable t) {
                Log.d("getDevices", t.getMessage());
                //// TODO: 2017-03-22 error hanlding
            }
        });
    }

    public void notifyAdapter(){
        recyclerView.getAdapter().notifyDataSetChanged();

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
    public void devicesReady(List<Device> devices) {
        MyDeviceRecyclerViewAdapter adapter = (MyDeviceRecyclerViewAdapter) recyclerView.getAdapter();
        DummyContent.setMyDevices(devices);
        adapter.setmValues(devices);
        notifyAdapter();
    }


    @Override
    public void deviceRemoved() {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Device item);
    }
}
