package com.example.kyle.joulieapp.Views;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

import com.example.kyle.joulieapp.Contracts.RuleContract;
import com.example.kyle.joulieapp.Contracts.UsageContract;
import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.Models.Rule;
import com.example.kyle.joulieapp.R;
import com.example.kyle.joulieapp.Api.ApiClient;
import com.example.kyle.joulieapp.Api.ApiService;
import com.example.kyle.joulieapp.Presenters.RulePresenter;

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
public class RuleFragment extends Fragment implements RuleContract.View {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private FloatingActionButton fabRemove;
    private RuleContract.Presenter rulePresenter;
    private boolean showDeleteMenuItem = false;

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
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        new RulePresenter(this, getActivity());

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

        recyclerView.setAdapter(new RuleRecyclerViewAdapter(mItemListener, DummyContent.MY_RULES, mListener));

        if (DummyContent.MY_RULES.isEmpty()) {
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
    public void onResume() {
        super.onResume();
        rulePresenter.start();
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
    public void setPresenter(RuleContract.Presenter presenter) {
        rulePresenter = presenter;
    }

    public RuleContract.Presenter getPresenter() {
        return this.rulePresenter;
    }

    @Override
    public void setLoadingIndicator(boolean active) {

    }

    @Override
    public void showRules(List<Rule> rules) {
        DummyContent.setMyRules(rules);
        RuleRecyclerViewAdapter adapter = (RuleRecyclerViewAdapter) recyclerView.getAdapter();
        adapter.setmValues(rules);
        notifyAdapter();
    }

    @Override
    public void showAddRule() {

    }

    @Override
    public void showRuleRemoved(String ruleID) {
        ((RuleRecyclerViewAdapter) recyclerView.getAdapter()).selectedRules.remove(ruleID);
        ((RuleRecyclerViewAdapter) recyclerView.getAdapter()).setmValues(DummyContent.MY_RULES);
        updateRemoveRulesButton(false);
    }

    @Override
    public void updateRemoveRulesButton(boolean show) {
        showDeleteMenuItem = show;
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void updateRulePowerButton(boolean state, String message) {
        Snackbar snackbar = Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @Override
    public void showRuleDetailsUi(String ruleId) {

    }

    @Override
    public void showNoRules() {

    }

    @Override
    public void refreshList() {
        rulePresenter.loadRules(true);
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

    RuleRecyclerViewAdapter.RuleItemListener mItemListener = new RuleRecyclerViewAdapter.RuleItemListener() {
        @Override
        public void onRuleClick(Rule clickedRule) {

        }

        @Override
        public void onActivateClicked(Rule rule, boolean state) {

        }

        @Override
        public void onRuleChecked(boolean checkState) {
            updateRemoveRulesButton(checkState);
        }
    };


    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Rule item);
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
            if (((RuleRecyclerViewAdapter) recyclerView.getAdapter()).selectedRules.size() == 0){
                Toast.makeText(getActivity(), "Error: no rule(s) selected to remove", Toast.LENGTH_SHORT).show();
                return true;
            }
            rulePresenter.deleteRules(((RuleRecyclerViewAdapter) recyclerView.getAdapter()).selectedRules.values());
            return  true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}
