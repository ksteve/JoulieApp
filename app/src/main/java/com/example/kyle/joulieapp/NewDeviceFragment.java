package com.example.kyle.joulieapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.presenter.NewDevicePresenter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewDeviceFragment.OnAddDeviceListener} interface
 * to handle interaction events.
 * Use the {@link NewDeviceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewDeviceFragment extends Fragment implements NewDevicePresenter.NewDevicePresenterListener {
    private static final String ARG_DEVICE_TYPE = "deviceType";

    // TODO: Rename and change types of parameters
    private int mDeviceType;

    private OnAddDeviceListener mListener;
    private NewDevicePresenter newDevicePresenter;

    private EditText deviceNameView;
    private EditText deviceIP;
    private EditText devicePort;
    private ImageView deviceImageView;
    private Drawable defaultDeviceImage;
    private int deviceType;

    public NewDeviceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewDeviceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewDeviceFragment newInstance(int deviceType) {
        NewDeviceFragment fragment = new NewDeviceFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DEVICE_TYPE, deviceType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDeviceType = getArguments().getInt(ARG_DEVICE_TYPE);
        }
        newDevicePresenter = new NewDevicePresenter(this, getActivity());
        //newDevicePresenter.subscribe();
        ((NewDeviceActivity)getActivity()).getSupportActionBar().setTitle("Add a New Device");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_device, container, false);

        //Intent intent = getIntent();
        // deviceType = intent.getIntExtra(DeviceTypeActivity.DEVICE_TYPE, Constants.TYPE_WEMO);

        deviceImageView = (ImageView) view.findViewById(R.id.deviceImage);
        deviceNameView = (EditText) view.findViewById(R.id.device_name);

        deviceIP = (EditText) view.findViewById(R.id.ip_address);
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start,
                                       int end, Spanned dest, int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, dstart) +
                            source.subSequence(start, end) +
                            destTxt.substring(dend);
                    if (!resultingTxt.matches ("^\\d{1,3}(\\." +
                            "(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                        return "";
                    } else {
                        String[] splits = resultingTxt.split("\\.");
                        for (int i=0; i<splits.length; i++) {
                            if (Integer.valueOf(splits[i]) > 255) {
                                return "";
                            }
                        }
                    }
                }
                return null;
            }
        };

        deviceIP.setFilters(filters);
        devicePort = (EditText) view.findViewById(R.id.port);
        defaultDeviceImage = ContextCompat.getDrawable(getActivity(), R.drawable.ic_smart_plug);
        setUpDeviceImage();

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (deviceNameView.getText().toString().trim().isEmpty()) {
                    deviceNameView.setError("Device Name is Required");
                } else {

                    newDevicePresenter.createDevice(deviceType,
                            deviceNameView.getText().toString(),
                            deviceIP.getText().toString(),
                            devicePort.getText().toString(),
                            defaultDeviceImage
                    );
                }
            }
        });


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onAddDevice();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddDeviceListener) {
            mListener = (OnAddDeviceListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setUpDeviceImage(){
        if(mDeviceType == Device.TYPE_WEMO){
            deviceImageView.setImageDrawable(getResources().getDrawable(R.drawable.wemo_device));
        } else if(mDeviceType == Device.TYPE_TPLINK){
            deviceImageView.setImageDrawable(getResources().getDrawable(R.drawable.tplink_device));
        }
    }

    @Override
    public void deviceReady(Device device) {
        if(getActivity() != null) {
            //mListener.onAddDevice();
            getActivity().finish();
        }
    }

    @Override
    public void onError(String message) {
        if(getActivity() != null) {
            getActivity().finish();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnAddDeviceListener {
        // TODO: Update argument type and name
        void onAddDevice();
    }
}
