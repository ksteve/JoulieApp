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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

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
    //private GraphView graph;
    private LineChart chart;

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


        totalUsageView = (TextView) view.findViewById(R.id.total_usage);


      //  new getUsageData().execute();

        // in this example, a LineChart is initialized from xml
        chart = (LineChart) view.findViewById(R.id.chart);

        //populate some fake hardcoded data to test
        List<Entry> entries = new ArrayList<Entry>();

        entries.add(new Entry(0f, 0.125f));
        entries.add(new Entry(6f, 0.25f));
        entries.add(new Entry(12f, 0.50f));
        entries.add(new Entry(18f, 1.0f));
        entries.add(new Entry(23f, 1.2f));
        entries.add(new Entry(23.75f, 1.25f));

        LineDataSet dataSet = new LineDataSet(entries, "Device1"); // add entries to dataset
        dataSet.setColors(new int[] { R.color.red1}, this.getContext());

        //for one line on chart then just use these next 3 lines
        //LineData lineData = new LineData(dataSet);
        //chart.setData(lineData);
        //chart.invalidate(); // refresh

        //add 2nd line
        List<Entry> entries2 = new ArrayList<Entry>();

        entries2.add(new Entry(0f, 0.15f));
        entries2.add(new Entry(6f, 0.35f));
        entries2.add(new Entry(12f, 0.70f));
        entries2.add(new Entry(18f, 1.1f));
        entries2.add(new Entry(23f, 1.3f));
        entries2.add(new Entry(23.75f, 1.5f));

        LineDataSet dataSet2 = new LineDataSet(entries2, "Device2"); // add entries to dataset
        dataSet2.setColors(new int[] { R.color.blue1}, this.getContext());

        // use the interface ILineDataSet
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataSet);
        dataSets.add(dataSet2);

        LineData data = new LineData(dataSets);
        chart.setData(data);
        chart.invalidate(); // refresh

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
       // new getUsageData().execute();
    }
}
