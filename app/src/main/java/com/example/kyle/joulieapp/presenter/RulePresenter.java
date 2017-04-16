package com.example.kyle.joulieapp.presenter;

import android.content.Context;

import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.Rule;
import com.example.kyle.joulieapp.api.ApiClient;
import com.example.kyle.joulieapp.api.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kyle on 2017-04-05.
 */

public class RulePresenter {

    private final Context context;
    private final RulePresenterListener mListener;

    public interface RulePresenterListener{
        void rulesReady(List<Rule> rules);
        void ruleReady(Rule rule);
        void ruleRemoved();
    }

    public RulePresenter(RulePresenterListener listener, Context context){
        this.mListener = listener;
        this.context = context;
    }

    public void createRule(Rule rule){
        ApiClient.getInstance(context.getApplicationContext()).getApiService()
                .createRule(rule)
                .enqueue(new Callback<Rule>() {
                    @Override
                    public void onResponse(Call<Rule> call, Response<Rule> response) {
                        mListener.ruleReady(response.body());
                    }

                    @Override
                    public void onFailure(Call<Rule> call, Throwable t) {
                        //// TODO: 2017-04-05 throw error, display message to user
                    }
                });
    }

    public void removeRule(Rule rule){
        ApiClient.getInstance(context.getApplicationContext()).getApiService()
                .deleteRule(rule.getId())
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        mListener.ruleRemoved();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        //// TODO: 2017-04-05 throw error, display message to user
                    }
                });
    }

    public void getRules(){
        ApiClient.getInstance(context.getApplicationContext()).getApiService()
                .getRules()
                .enqueue(new Callback<List<Rule>>() {
                    @Override
                    public void onResponse(Call<List<Rule>> call, Response<List<Rule>> response) {
                        mListener.rulesReady(response.body());
                    }

                    @Override
                    public void onFailure(Call<List<Rule>> call, Throwable t) {
                        //// TODO: 2017-04-05 throw error, display message to user
                    }
                });
    }
}
