package com.example.kyle.joulieapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.Models.Rule;
import com.example.kyle.joulieapp.api.ApiClient;
import com.example.kyle.joulieapp.api.ApiService;
import com.example.kyle.joulieapp.presenter.DevicePresenter;
import com.example.kyle.joulieapp.presenter.RulePresenter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.kyle.joulieapp.R.id.fabRemove;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class RuleFragment extends Fragment implements RulePresenter.RulePresenterListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private FloatingActionButton fabRemove;
    private RulePresenter rulePresenter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RuleFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RuleFragment newInstance(int columnCount) {
        RuleFragment fragment = new RuleFragment();
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

        rulePresenter = new RulePresenter(this, getActivity());
        rulePresenter.getRules();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rule_list, container, false);

        // Set the adapter
        Context context = view.getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        emptyView = (TextView) view.findViewById(R.id.empty_view);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        recyclerView.setAdapter(new MyRuleRecyclerViewAdapter(DummyContent.MY_RULES, mListener));

        if (DummyContent.MY_RULES.isEmpty()) {
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
                if (((MyRuleRecyclerViewAdapter) recyclerView.getAdapter()).selectedRules.size() == 0){
                    Toast.makeText(getActivity(), "Error: no rule(s) selected to remove", Toast.LENGTH_SHORT).show();
                }
                else{
                    for (Rule rule: ((MyRuleRecyclerViewAdapter) recyclerView.getAdapter()).selectedRules){
                        DummyContent.MY_RULES.remove(rule);
                    }
                    ((MyRuleRecyclerViewAdapter) recyclerView.getAdapter()).selectedRules.clear();
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //getRules();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
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

    public void getRules() {
        ApiService apiService = ApiClient.getInstance(getActivity().getApplicationContext()).getApiService();
        Call<List<Rule>> call = apiService.getRules();
        call.enqueue(new Callback<List<Rule>>() {
            @Override
            public void onResponse(Call<List<Rule>> call, Response<List<Rule>> response) {
                MyRuleRecyclerViewAdapter adapter = (MyRuleRecyclerViewAdapter) recyclerView.getAdapter();
                if(response.body() != null) {
                    DummyContent.setMyRules(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Rule>> call, Throwable t) {
                //// TODO: 2017-03-22 error hanlding
            }
        });
    }

    public void notifyAdapter(){
        if (DummyContent.MY_RULES.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void rulesReady(List<Rule> rules) {
        MyRuleRecyclerViewAdapter adapter = (MyRuleRecyclerViewAdapter) recyclerView.getAdapter();
        DummyContent.setMyRules(rules);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void ruleReady(Rule rule) {

    }

    @Override
    public void ruleRemoved() {

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
        void onListFragmentInteraction(Rule item);
    }
}
