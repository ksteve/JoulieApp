package com.example.kyle.joulieapp.presenter;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.Models.Rule;
import com.example.kyle.joulieapp.api.ApiClient;
import com.example.kyle.joulieapp.api.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kyle on 2017-04-05.
 */

public class NewRulePresenter {

    private final Context context;
    private final NewRulePresenterListener mListener;
    private final ApiService apiService;

    public interface NewRulePresenterListener{
        void ruleReady(Rule rule);
        void onError(String message);
    }

    public void subscribe(){
        //EventBus.getDefault().register(this);
    }

    public void unsubscribe(){
      //  RxBus.get().unregister(this);
    }

    public NewRulePresenter(NewRulePresenterListener listener, Context context){
        this.mListener = listener;
        this.context = context;
        this.apiService = ApiClient.getInstance(this.context).getApiService();
    }

    public void createRule(String id, String ruleName, Device device, int turnOnOff, String time, String days){

        final Rule rule = new Rule(
                id,
                ruleName,
                device,
                turnOnOff,
                time,
                days
                );

        apiService
                .createRule(rule)
                .enqueue(new Callback<Rule>() {
                    @Override
                    public void onResponse(Call<Rule> call, Response<Rule> response) {
                        if (response.body() != null){
                            DummyContent.MY_RULES.add(response.body());
                        }
                        mListener.ruleReady(response.body());
                    }

                    @Override
                    public void onFailure(Call<Rule> call, Throwable t) {
                        //// TODO: 2017-04-05 throw error, display message to user
                        mListener.onError(t.getMessage());
                    }
                });
    }
}
