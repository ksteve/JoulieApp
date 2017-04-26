package com.example.kyle.joulieapp.Presenters;

import android.content.Context;

import com.example.kyle.joulieapp.Contracts.NewRuleContract;
import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.Models.Rule;
import com.example.kyle.joulieapp.Api.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kyle on 2017-04-05.
 */

public class NewRulePresenter implements NewRuleContract.Presenter {

    private final Context context;
    private final NewRuleContract.View mNewRuleView;

    @Override
    public void start() {

    }


    public NewRulePresenter(NewRuleContract.View newRuleView, Context context){
        this.mNewRuleView = newRuleView;
        mNewRuleView.setPresenter(this);
        this.context = context;
    }

    public void createRule(String ruleName, Device device, int turnOnOff, int time, int days){

        final Rule rule = new Rule(
                ruleName,
                device,
                turnOnOff,
                time,
                days
                );

        ApiClient.getInstance(context.getApplicationContext()).getCloudApiService()
                .createRule(device.getId(),rule)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.body() != null){
                            DummyContent.MY_RULES.add(rule);
                        }
                        mNewRuleView.ruleReady(rule);
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        //// TODO: 2017-04-05 throw error, display message to user
                        mNewRuleView.showRequestFailed(t.getMessage());
                    }
                });
    }
}
