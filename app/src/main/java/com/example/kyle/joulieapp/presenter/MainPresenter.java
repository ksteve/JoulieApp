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

public class MainPresenter {

    private final Context context;
    private final MainPresenterListener mListener;
    private final ApiService apiService;

    public interface MainPresenterListener{
        void onConnectionChanged(String connectionText);
    }

    public MainPresenter(MainPresenterListener listener, Context context){
        this.mListener = listener;
        this.context = context;
        this.apiService = ApiClient.getInstance(this.context).getApiService();
    }

    public void testConnection(){
        this.apiService.ping()
            .enqueue(new Callback() {
            @Override
            public void onResponse(Call call, retrofit2.Response response) {
                if(ApiClient.getInstance(context).getConnectionType() == ApiClient.LOCAL) {
                    mListener.onConnectionChanged("Local Server");
                } else{
                    mListener.onConnectionChanged("Cloud Server");
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                if(ApiClient.getInstance(context).getConnectionType() == ApiClient.LOCAL) {
                    mListener.onConnectionChanged("Cloud Server");
                } else {
                    mListener.onConnectionChanged("Local Server");
                }
            }
        });
    }
}
