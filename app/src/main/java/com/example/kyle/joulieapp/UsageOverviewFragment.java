package com.example.kyle.joulieapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kyle.joulieapp.Models.Usage;
import com.example.kyle.joulieapp.presenter.UsagePresenter;
import com.example.kyle.joulieapp.utils.DateAxisValueFormatter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UsageOverviewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UsageOverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsageOverviewFragment extends Fragment implements UsagePresenter.UsagePresenterListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int DAY_FORMAT = 0;
    private static final int WEEK_FORMAT = 1;
    private static final int MONTH_FORMAT = 2;
    private static final int YEAR_FORMAT = 3;
    private static final int MAX_FORMAT = 4;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private UsagePresenter usagePresenter;
    private OnFragmentInteractionListener mListener;
    //private GraphView graph;
    private LineChart mLineChart;
    private TabLayout tabLayout;

    private TextView totalUsageView;
    private Spinner spChartDisplay;
    private RadioGroup rgChartDisplay;
    private RadioButton rbKilowatt;
    private RadioButton rbDollars;
    private float fCost;
    private List<ILineDataSet> dataSets;
    private LineDataSet dataSetKilowatt1;
    private LineDataSet dataSetDollars1;
    private LineDataSet dataSetKilowatt2;
    private LineDataSet dataSetDollars2;
    private SharedPreferences prefs;

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

        usagePresenter = new UsagePresenter(this, getActivity());
        usagePresenter.getUsages();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_usage_overview, container, false);

        rbKilowatt = (RadioButton) view.findViewById(R.id.rbKilowatt);
        rbDollars = (RadioButton) view.findViewById(R.id.rbDollars);
        rgChartDisplay = (RadioGroup) view.findViewById(R.id.rgChartDisplayType);
        rbKilowatt = (RadioButton) view.findViewById(R.id.rbKilowatt);
        rbDollars = (RadioButton) view.findViewById(R.id.rbDollars);
        rgChartDisplay.check(rbKilowatt.getId());



        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //setup Tab layout
        tabLayout = (TabLayout) view.findViewById(R.id.graph_tabs);
        mLineChart = (LineChart) view.findViewById(R.id.chart);
        setupTabIcons();
        //setupChart();
        return  view;
    }

    private void setupTabIcons() {
        tabLayout.addTab(tabLayout.newTab().setText("1D"), true);
        tabLayout.addTab(tabLayout.newTab().setText("1W"));
        tabLayout.addTab(tabLayout.newTab().setText("1M"));
        tabLayout.addTab(tabLayout.newTab().setText("1Y"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setChartFormatter(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupChart(){
        mLineChart.getAxis(YAxis.AxisDependency.LEFT).setEnabled(false);
        mLineChart.getAxisRight().disableGridDashedLine();

        mLineChart.getXAxis().setDrawGridLines(false);
        mLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        setChartFormatter(tabLayout.getSelectedTabPosition());
        //mLineChart.getXAxis().setValueFormatter(new DateAxisValueFormatter(tabLayout.getSelectedTabPosition()));
       // mLineChart.getXAxis().setGranularity(3600f);
       // mLineChart.getXAxis().setAxisMaximum(1490327999f);
       // mLineChart.getXAxis().setAxisMinimum(1490241600f);

        mLineChart.setDrawGridBackground(false);

        mLineChart.getLegend().setDrawInside(true);
        mLineChart.getLegend().setYOffset(100);

        //populate some fake hardcoded data to test
        List<Entry> entriesKilowatt1 = new ArrayList<Entry>();

        entriesKilowatt1.add(new Entry(1490349600f, 0.125f));
        entriesKilowatt1.add(new Entry(1490353200f, 0.25f));
        entriesKilowatt1.add(new Entry(1490356800f, 0.50f));
        entriesKilowatt1.add(new Entry(1490360400f, 1.0f));
        entriesKilowatt1.add(new Entry(1490364000f, 1.2f));
        entriesKilowatt1.add(new Entry(1490367600f, 1.25f));

        String sCost = "0";
        sCost = prefs.getString("peak_cost", "0");
        if(sCost.isEmpty()) {
            sCost = "0";
        }

        fCost = Float.parseFloat(sCost) / 100;

        List<Entry> entriesDollars1 = new ArrayList<>();

        for (int i = 0; i < entriesKilowatt1.size(); i++){
            entriesDollars1.add(new Entry(entriesKilowatt1.get(i).getX(), entriesKilowatt1.get(i).getY() * fCost));
        }


        dataSetKilowatt1 = new LineDataSet(entriesKilowatt1, "Device1"); // add entries to dataset
        dataSetKilowatt1.setColors(new int[] { R.color.red1}, getActivity().getApplicationContext());
        dataSetKilowatt1.setLineWidth(4);

        dataSetDollars1 = new LineDataSet(entriesDollars1, "Device1"); // add entries to dataset
        dataSetDollars1.setColors(new int[] { R.color.red1}, getActivity().getApplicationContext());
        dataSetDollars1.setLineWidth(4);

        //for one line on chart then just use these next 3 lines
        //LineData lineData = new LineData(dataSet);
        //chart.setData(lineData);
        //chart.invalidate(); // refresh

        //add 2nd line
        List<Entry> entriesKilowatt2 = new ArrayList<Entry>();

        entriesKilowatt2.add(new Entry(1490349600f, 0.15f));
        entriesKilowatt2.add(new Entry(1490353200f, 0.35f));
        entriesKilowatt2.add(new Entry(1490356800f, 0.70f));
        entriesKilowatt2.add(new Entry(1490360400f, 1.1f));
        entriesKilowatt2.add(new Entry(1490364000f, 1.3f));
        entriesKilowatt2.add(new Entry(1490367600f, 1.5f));

        List<Entry> entriesDollars2 = new ArrayList<>();

        for (int i = 0; i < entriesKilowatt2.size(); i++){
            entriesDollars2.add(new Entry(entriesKilowatt2.get(i).getX(), entriesKilowatt2.get(i).getY() * fCost));
        }

        dataSetKilowatt2 = new LineDataSet(entriesKilowatt2, "Device2"); // add entries to dataset
        dataSetKilowatt2.setColors(new int[] { R.color.blue1}, getActivity().getApplicationContext());
        dataSetKilowatt2.setLineWidth(4);

        dataSetDollars2 = new LineDataSet(entriesDollars2, "Device2"); // add entries to dataset
        dataSetDollars2.setColors(new int[] { R.color.blue1}, getActivity().getApplicationContext());
        dataSetDollars2.setLineWidth(4);

        // use the interface ILineDataSet
        dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataSetKilowatt1);
        dataSets.add(dataSetKilowatt2);

        LineData data = new LineData(dataSets);
        mLineChart.setData(data);
        mLineChart.animateXY(1200, 1200);
        mLineChart.invalidate(); // refresh

        if (rbDollars.isChecked()){
            setChartData(true);
        }
        else {
            setChartData(false);
        }


        rgChartDisplay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                if (checkedId == rbDollars.getId()){
                    setChartData(true);
                }
                else {
                    setChartData(false);
                }
            }
        });

    }

    private void setChartData(boolean dollars){
        dataSets.clear();

        if (dollars){
            dataSets.add(dataSetDollars1);
            dataSets.add(dataSetDollars2);
        }
        else{
            dataSets.add(dataSetKilowatt1);
            dataSets.add(dataSetKilowatt2);
        }

        mLineChart.clear();
        LineData data = new LineData(dataSets);
        mLineChart.setData(data);
        mLineChart.animateXY(500, 500);
        mLineChart.invalidate(); // refresh
    }

    private void setChartFormatter(int formatType){
        Calendar cal = Calendar.getInstance();
        long currentTime;
        switch(formatType){
            case DAY_FORMAT:
                if(mLineChart.getData() != null) {
                    mLineChart.getXAxis().setAxisMinimum(mLineChart.getData().getXMin());
                    mLineChart.getXAxis().setAxisMaximum(mLineChart.getData().getXMax());
                }

                mLineChart.getXAxis().setValueFormatter(new DateAxisValueFormatter(DateAxisValueFormatter.DAY));
                mLineChart.getXAxis().setGranularity(3600f);
                mLineChart.fitScreen();
                mLineChart.invalidate();
                break;
            case WEEK_FORMAT:
                currentTime = cal.getTimeInMillis()/1000;

                cal.set(Calendar.DAY_OF_WEEK, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                long beginningOfWeek = cal.getTimeInMillis()/1000;

                mLineChart.getXAxis().setAxisMinimum(beginningOfWeek);
                mLineChart.getXAxis().setAxisMaximum(currentTime);

                mLineChart.getXAxis().setValueFormatter(new DateAxisValueFormatter(DateAxisValueFormatter.WEEK));
                mLineChart.getXAxis().setGranularity(86400f);
                mLineChart.fitScreen();
                mLineChart.invalidate();
                break;
            case MONTH_FORMAT:
                currentTime = cal.getTimeInMillis()/1000;
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                long beginningOfMonth = cal.getTimeInMillis()/1000;

                mLineChart.getXAxis().setAxisMinimum(beginningOfMonth);
                mLineChart.getXAxis().setAxisMaximum(currentTime);

                mLineChart.getXAxis().setValueFormatter(new DateAxisValueFormatter(DateAxisValueFormatter.MONTH));
                mLineChart.getXAxis().setGranularity(259200f);
                mLineChart.fitScreen();
                mLineChart.invalidate();
                break;
            case YEAR_FORMAT:
                currentTime = cal.getTimeInMillis()/1000;
                cal.set(Calendar.MONTH, Calendar.JANUARY);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                long oneMonthAgo = cal.getTimeInMillis()/1000;

                mLineChart.getXAxis().setAxisMinimum(oneMonthAgo);
                mLineChart.getXAxis().setAxisMaximum(currentTime);

                mLineChart.getXAxis().setValueFormatter(new DateAxisValueFormatter(DateAxisValueFormatter.YEAR));

                mLineChart.getXAxis().setGranularity(2500000f);
                mLineChart.fitScreen();
                mLineChart.invalidate();
                break;
            case MAX_FORMAT:
                break;
            default:
                break;

        }
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

    @Override
    public void UsagesReady(List<Usage> Usages) {

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

    @Override
    public void onResume() {
        super.onResume();
        setupChart();
    }
}
