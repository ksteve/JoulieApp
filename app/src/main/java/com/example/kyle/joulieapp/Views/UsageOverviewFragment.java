package com.example.kyle.joulieapp.Views;

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
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kyle.joulieapp.Contracts.UsageContract;
import com.example.kyle.joulieapp.Presenters.UsagePresenter;
import com.example.kyle.joulieapp.R;
import com.example.kyle.joulieapp.Utils.DateAxisValueFormatter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.w3c.dom.Text;

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
public class UsageOverviewFragment extends Fragment implements UsageContract.View {
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

    private UsageContract.Presenter mUsagePresenter;
    private OnFragmentInteractionListener mListener;
    //private GraphView graph;
    private LineChart mLineChart;
    private TabLayout tabLayout;

    private TextView tvTotalCostView;
    private TextView lblTotalCost;
    private TextView tvTotalUsageView;
    private TextView lblTotalUsage;
    private TextView tvUsageTrend;
    private TextView tvCostTrend;



    private Spinner spChartDisplay;
    private RadioGroup rgChartDisplay;
    private RadioButton rbKilowatt;
    private RadioButton rbDollars;
    private ImageButton btnFilter;
    private float fCost;
    private List<ILineDataSet> dataSets;
    private LineDataSet dataSetKilowatt1;
    private LineDataSet dataSetDollars1;
    private LineDataSet dataSetKilowatt2;
    private LineDataSet dataSetDollars2;
    private LineDataSet dataSetKilowatt3;
    private LineDataSet dataSetDollars3;
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

        SharedPreferences sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(getActivity());
     //  float tests =  sharedPreferences.getFloat("");



        new UsagePresenter(this, sharedPreferences, getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_usage_overview, container, false);
        tvTotalUsageView = (TextView) view.findViewById(R.id.avg_usage);
        tvUsageTrend = (TextView) view.findViewById(R.id.usage_trend);
        tvTotalCostView = (TextView) view.findViewById(R.id.avg_cost);
        tvCostTrend = (TextView) view.findViewById(R.id.cost_trend);
        lblTotalUsage = (TextView) view.findViewById(R.id.avg_usage_label);
        lblTotalCost = (TextView) view.findViewById(R.id.avg_cost_label);


        rbKilowatt = (RadioButton) view.findViewById(R.id.rbKilowatt);
        rbDollars = (RadioButton) view.findViewById(R.id.rbDollars);
        rgChartDisplay = (RadioGroup) view.findViewById(R.id.rgChartDisplayType);
        rbKilowatt = (RadioButton) view.findViewById(R.id.rbKilowatt);
        rbDollars = (RadioButton) view.findViewById(R.id.rbDollars);
        rgChartDisplay.check(rbKilowatt.getId());
        btnFilter = (ImageButton) view.findViewById(R.id.filterBtn);

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = getLayoutInflater(null);
                View listView = (View) inflater.inflate(R.layout.filter_list, null);
                mUsagePresenter.openFilter(listView);
            }
        });


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
        mLineChart.setDrawBorders(false);
        mLineChart.getXAxis().setDrawGridLines(false);
        mLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        setChartFormatter(tabLayout.getSelectedTabPosition());
        mLineChart.setDrawGridBackground(false);
        mLineChart.getLegend().setEnabled(false);
        mLineChart.getLegend().setDrawInside(true);
        mLineChart.getLegend().setYOffset(100);
//        mLineChart.setContentDescription("");
        Description ds = new Description();
        ds.setEnabled(false);
        mLineChart.setDescription(ds);
//        // use the interface ILineDataSet
        //  dataSets = new ArrayList<>();

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
//        dataSets.clear();
//
//        if (dollars){
//            dataSets.add(dataSetDollars1);
//            dataSets.add(dataSetDollars2);
//            dataSets.add(dataSetDollars3);
//        }
//        else{
//            dataSets.add(dataSetKilowatt1);
//            dataSets.add(dataSetKilowatt2);
//            dataSets.add(dataSetKilowatt3);
//        }
//
//        mLineChart.clear();
//        LineData data = new LineData(dataSets);
//        mLineChart.setData(data);
//        mLineChart.animateXY(500, 500);
//        mLineChart.invalidate(); // refresh
    }

    private void setChartFormatter(int formatType){
        Calendar cal = Calendar.getInstance();
        long currentTime;
        switch(formatType){
            case DAY_FORMAT:

                lblTotalUsage.setText("Usage Today");
                lblTotalCost.setText("Estimated Cost Today");


                currentTime = cal.getTimeInMillis()/1000;
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                long beginningOfDay = cal.getTimeInMillis()/1000;

                mLineChart.getXAxis().setAxisMinimum(beginningOfDay);
                mLineChart.getXAxis().setAxisMaximum(currentTime);

                mLineChart.getXAxis().setValueFormatter(new DateAxisValueFormatter(DateAxisValueFormatter.DAY));
                mLineChart.getXAxis().setGranularity(3600f);
                mLineChart.fitScreen();
                mLineChart.invalidate();
                break;
            case WEEK_FORMAT:

                lblTotalUsage.setText("Usage This Week");
                lblTotalCost.setText("Estimated Cost This Week");

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

                lblTotalUsage.setText("Usage This Month");
                lblTotalCost.setText("Estimated Cost This Month");

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

                lblTotalUsage.setText("Average Usage");
                lblTotalCost.setText("Estimated Cost");

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
    public void setPresenter(UsageContract.Presenter presenter) {
        mUsagePresenter = presenter;
    }

    public UsageContract.Presenter getPresenter() {
        return this.mUsagePresenter;
    }

    @Override
    public void setLoadingIndicator(boolean active) {

    }

    @Override
    public void showUsages(LineData data) {
        mLineChart.setData(data);
        mLineChart.animateXY(500, 500);
        mLineChart.invalidate(); // refresl
    }

    @Override
    public void showTotals(float totalUsage, float totalCost) {
        tvTotalUsageView.setText(String.valueOf(totalUsage) + " kWh");
        tvTotalCostView.setText("$" + String.valueOf(totalCost));
    }

    @Override
    public void showNoUsage() {

    }

    @Override
    public void refreshList() {

    }

    @Override
    public void showRequestFailed(String message) {

    }

    @Override
    public void showRequestSuccess(String message) {

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
        mUsagePresenter.start();
    }
}
