package com.example.kyle.joulieapp;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.unmarshallers.IntegerSetUnmarshaller;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.Models.PiElectricity;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.example.kyle.joulieapp.LoginActivity.LOG_TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UsageOverviewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UsageOverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsageOverviewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private AmazonDynamoDBClient ddbClient;
    private DynamoDBMapper mapper;
    private GraphView graph;

    private TextView totalUsageView;

    public UsageOverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UsageOverviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UsageOverviewFragment newInstance(String param1, String param2) {
        UsageOverviewFragment fragment = new UsageOverviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_usage_overview, container, false);

        ddbClient = new AmazonDynamoDBClient(LoginActivity.credentialsProvider);
        ddbClient.setRegion(Region.getRegion(Regions.US_WEST_2));
        mapper = new DynamoDBMapper(ddbClient);

        //setup graph
        graph = (GraphView) view.findViewById(R.id.graph);
        graph.setFocusable(true);
        graph.setFocusableInTouchMode(true);
        //graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
       // graph.getViewport().setScrollable(true); // enables horizontal scrolling
        //graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        totalUsageView = (TextView) view.findViewById(R.id.total_usage);


        new getUsageData().execute();

        return  view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void updateUsageData(){
        new getUsageData().execute();
    }

    private class getUsageData extends AsyncTask<Void, String, Integer> {

        private int[] colours = new int[]{Color.GREEN, Color.RED, Color.BLUE, Color.BLACK};
        Map<String, ArrayList<DataPoint>> deviceData = new HashMap<>();
        float totalUsage = 0;
        int numDataPoints = 0;

        @Override
        protected Integer doInBackground(Void... voids) {


            PaginatedScanList<PiElectricity> result = DynamoDBManager.getInstance().getUsageData();

            for (PiElectricity x:result) {
                try {

                    JSONObject jObject = new JSONObject(x.getDeviceID());

                    String deviceID = jObject.getString("DeviceID");
                    int timestamp = jObject.getInt("timestamp");
                    int value = jObject.getInt("value");

                    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                    cal.setTimeInMillis(timestamp);
                    Date date = cal.getTime();
                    //String date = DateFormat.format("dd-MM-yyyy", cal).toString();
                   // return date;

                    if(deviceData.containsKey(deviceID)){
                        deviceData.get(deviceID).add(new DataPoint(deviceData.get(deviceID).size(), value));
                    } else {
                        ArrayList<DataPoint> data = new ArrayList<>();
                        data.add(new DataPoint(data.size(), value));
                        deviceData.put(deviceID, data);
                    }
                    totalUsage += value;
                    numDataPoints++;
                }
                catch (JSONException e){
                    Log.e(LOG_TAG,
                            "Json parsing exception",
                            e);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer value) {
            super.onPostExecute(value);
            int i = 0;
            graph.removeAllSeries();

            for(Map.Entry<String, ArrayList<DataPoint>> entry: deviceData.entrySet()){
                Collections.sort(entry.getValue(), new TimeStampComparator());
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(entry.getValue().toArray(new DataPoint[entry.getValue().size()]));
                series.setColor(colours[i]);
                series.setTitle(entry.getKey());
                graph.addSeries(series);
                i++;
            }
            float avgUsage = (totalUsage/numDataPoints);
            totalUsageView.setText(String.valueOf(avgUsage) + " Watts");
        }
    }

    private class TimeStampComparator implements Comparator<DataPoint> {
        @Override
        public int compare(DataPoint datapoint, DataPoint t1) {

            if (datapoint.getX() > t1.getX()) {
                return 0;
            } else {
                return 1;
            }

        }
    }


}
