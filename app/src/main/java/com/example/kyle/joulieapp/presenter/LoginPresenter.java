package com.example.kyle.joulieapp.presenter;

import android.content.Context;

import com.example.kyle.joulieapp.Models.Usage;
import com.example.kyle.joulieapp.api.ApiClient;
import com.example.kyle.joulieapp.api.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kyle on 2017-04-05.
 */

public class LoginPresenter {

    private final Context context;
    private final LoginPresenterListener mListener;
    private final ApiService apiService;

    public interface LoginPresenterListener{
        void UsagesReady(List<Usage> Usages);
    }

    public LoginPresenter(LoginPresenterListener listener, Context context){
        this.mListener = listener;
        this.context = context;
        this.apiService = ApiClient.getInstance(this.context).getApiService();
    }

    public void getUsages(){
        apiService
                .getUsages()
                .enqueue(new Callback<List<Usage>>() {
                    @Override
                    public void onResponse(Call<List<Usage>> call, Response<List<Usage>> response) {
                        mListener.UsagesReady(response.body());
                    }

                    @Override
                    public void onFailure(Call<List<Usage>> call, Throwable t) {
                        //// TODO: 2017-04-05 throw error, display message to user
                    }
                });
    }
}
