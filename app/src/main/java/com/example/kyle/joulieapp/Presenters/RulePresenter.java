package com.example.kyle.joulieapp.Presenters;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.kyle.joulieapp.Contracts.DeviceContract;
import com.example.kyle.joulieapp.Contracts.RuleContract;
import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.Rule;
import com.example.kyle.joulieapp.Api.ApiClient;

import java.util.Collection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kyle on 2017-04-05.
 */

public class RulePresenter implements RuleContract.Presenter {

    private final Context context;
    private final RuleContract.View mRuleView;
    private boolean mFirstLoad = true;

    public RulePresenter(@NonNull RuleContract.View ruleView, Context context){
        this.mRuleView = ruleView;
        this.mRuleView.setPresenter(this);
        this.context = context;
    }

    @Override
    public void start() {
       // loadRules(false);
    }

    @Override
    public void deleteRule(@NonNull final Rule rule) {
        ApiClient.getInstance(context.getApplicationContext()).getApiService()
                .deleteRule(rule.getId())
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        mRuleView.showRuleRemoved(rule.getId());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        //// TODO: 2017-04-05 throw error, display message to user
                    }
                });
    }

    @Override
    public void deleteRules(@NonNull Collection<Rule> rules) {

    }

    @Override
    public void loadRules(boolean forceUpdate) {
        // Simplification for sample: a network reload will be forced on first load.
        getRules(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    @Override
    public void showRemoveRules(boolean show) {
        mRuleView.updateRemoveRulesButton(show);
    }

    @Override
    public void openRuleDetails(@NonNull Rule rule) {

    }

    @Override
    public void addNewRule(@NonNull Rule rule) {
        ApiClient.getInstance(context.getApplicationContext()).getApiService()
                .createRule(rule)
                .enqueue(new Callback<Rule>() {
                    @Override
                    public void onResponse(Call<Rule> call, Response<Rule> response) {
                //        mRuleView.ruleReady(response.body());
                    }

                    @Override
                    public void onFailure(Call<Rule> call, Throwable t) {
                        //// TODO: 2017-04-05 throw error, display message to user
                    }
                });
    }

    @Override
    public void toggleRulePower(@NonNull Rule rule, boolean state) {

    }

    public void getRules(boolean forceUpdate, final boolean showLoadingUI){

        if (showLoadingUI) {
            //     mTasksView.setLoadingIndicator(true);
        }
        if (forceUpdate) {

            ApiClient.getInstance(context.getApplicationContext()).getApiService()
                    .getRules()
                    .enqueue(new Callback<List<Rule>>() {
                        @Override
                        public void onResponse(Call<List<Rule>> call, Response<List<Rule>> response) {
                            if(response.body()!= null) {
                                processRules(response.body());
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Rule>> call, Throwable t) {
                            //// TODO: 2017-04-05 throw error, display message to user
                            mRuleView.showRequestFailed(t.getMessage());
                        }
                    });
        }
    }

    private void processRules(List<Rule> rules) {
        if (rules.isEmpty()) {
            mRuleView.showNoRules();
        } else {
            mRuleView.showRules(rules);
        }
    }
}
