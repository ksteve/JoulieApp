package com.example.kyle.joulieapp.presenter;

import android.content.Context;

import com.example.kyle.joulieapp.Models.Usage;
import com.example.kyle.joulieapp.api.ApiClient;
import com.example.kyle.joulieapp.api.ApiService;
import com.example.kyle.joulieapp.utils.NetworkServiceDisc;
import com.example.kyle.joulieapp.utils.NetworkUtil;

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
    private final NetworkServiceDisc mNsd;

    public interface MainPresenterListener{
        void onConnectionChanged(String connectionText);
        void refreshLists();
    }

    public MainPresenter(MainPresenterListener listener, Context context){
        this.mListener = listener;
        this.context = context;
        this.apiService = ApiClient.getInstance(this.context).getApiService();
        this.mNsd = new NetworkServiceDisc(this.context);
        this.mNsd.initializeNsd();
    }

    public void onStart(){
        mNsd.discoverServices();
    }

    public void onPause(){
        mNsd.stopDiscovery();
    }


    public void testConnection(int status){

        if(status == NetworkUtil.NETWORK_STAUS_WIFI) {
            ApiClient.getInstance(context.getApplicationContext()).changeApiBaseUrl(ApiClient.LOCAL);


        } else if (status == NetworkUtil.NETWORK_STATUS_MOBILE){
            ApiClient.getInstance(context.getApplicationContext()).changeApiBaseUrl(ApiClient.CLOUD);
        }

        ApiClient.getInstance(this.context).getApiService().ping()
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
                            ApiClient.getInstance(context.getApplicationContext()).changeApiBaseUrl(ApiClient.CLOUD);
                            mListener.refreshLists();
                        } else {
                            mListener.onConnectionChanged("Connection Failed");
                        }
                    }
                });
    }
}
