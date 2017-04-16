package com.example.kyle.joulieapp.presenter;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.support.annotation.MainThread;
import android.util.Log;

import com.example.kyle.joulieapp.Models.Usage;
import com.example.kyle.joulieapp.api.ApiClient;
import com.example.kyle.joulieapp.api.ApiService;
import com.example.kyle.joulieapp.utils.NetworkServiceDisc;
import com.example.kyle.joulieapp.utils.NetworkUtil;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kyle on 2017-04-05.
 */

public class MainPresenter implements NsdManager.DiscoveryListener {

    private final Context context;
    private final MainPresenterListener mListener;
    private final NetworkServiceDisc mNsd;
    private static final String TAG = "Main Presenter";



    public interface MainPresenterListener{
        void onConnectionChanged(int connectionType);
        void refreshLists();
    }

    public MainPresenter(MainPresenterListener listener, Context context){
        this.mListener = listener;
        this.context = context;
        this.mNsd = new NetworkServiceDisc(this.context.getApplicationContext());
       // this.mNsd.initializeNsd(this);
    }

    public void onStart(){
        mListener.onConnectionChanged(ApiClient.CLOUD);
        testConnection();
        //mNsd.discoverServices();
    }

    public void onPause(){
        mNsd.stopDiscovery();
    }


    public void testConnection(){

        int status = NetworkUtil.getConnectivityStatusString(context);

        switch (status){
            case NetworkUtil.NETWORK_STATUS_NOT_CONNECTED:
                // TODO: 2017-04-13 dispaly not connected to internet
              //  mListener.onConnectionChanged(-1);
                return;
            case NetworkUtil.NETWORK_STATUS_MOBILE:
                apiConnectionChanged(ApiClient.CLOUD);
                //nothing to do yet
                break;
            case NetworkUtil.NETWORK_STAUS_WIFI:
                //start looking for joulie local server
                mNsd.discoverServices(this);
                break;
        }

    }

    public void checkConnection(){

        int status = NetworkUtil.getConnectivityStatusString(context);

        switch (status){
            case NetworkUtil.NETWORK_STATUS_NOT_CONNECTED:
                // TODO: 2017-04-13 dispaly not connected to internet
            //    mListener.onConnectionChanged(-1);
                return;
            case NetworkUtil.NETWORK_STATUS_MOBILE:
                apiConnectionChanged(ApiClient.CLOUD);
                //nothing to do yet
                break;
            case NetworkUtil.NETWORK_STAUS_WIFI:
                apiConnectionChanged(ApiClient.CLOUD);
                //start looking for joulie local server
                break;
        }

    }


    private void apiConnectionChanged(int serverType) {
        ApiClient.getInstance(context.getApplicationContext()).changeApiBaseUrl(serverType);
        mListener.onConnectionChanged(serverType);
    }

//        if(status == NetworkUtil.NETWORK_STAUS_WIFI) {
//            mNsd.discoverServices();
//
//
//        } else if (status == NetworkUtil.NETWORK_STATUS_MOBILE){
//            ApiClient.getInstance(context.getApplicationContext()).changeApiBaseUrl(ApiClient.CLOUD);
//        }

//        ApiClient.getInstance(this.context).getApiService().ping()
//                .enqueue(new Callback<Void>() {
//                    @Override
//                    public void onResponse(Call call, retrofit2.Response response) {
//                        if(ApiClient.getInstance(context).getConnectionType() == ApiClient.LOCAL) {
//                            mListener.onConnectionChanged("Local Server");
//                        } else{
//                            mListener.onConnectionChanged("Cloud Server");
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call call, Throwable t) {
//                        if(ApiClient.getInstance(context).getConnectionType() == ApiClient.LOCAL) {
//                            mListener.onConnectionChanged("Cloud Server");
//                            ApiClient.getInstance(context.getApplicationContext()).changeApiBaseUrl(ApiClient.CLOUD);
//                            mListener.refreshLists();
//                        } else {
//                            mListener.onConnectionChanged("Connection Failed");
//                        }
//                    }
//                });

    @Override
    public void onDiscoveryStarted(String s) {
        Log.d(TAG, "Service discovery started");
    }

    @Override
    public void onStartDiscoveryFailed(String s, int i) {
        Log.e(TAG, "Discovery failed: Error code:" + i);
    }

    @Override
    public void onStopDiscoveryFailed(String s, int i) {
        Log.e(TAG, "Discovery failed: Error code:" + i);
    }

    @Override
    public void onDiscoveryStopped(String s) {
        Log.i(TAG, "Discovery stopped: " + s);
    }

    @Override
    public void onServiceFound(NsdServiceInfo service) {
        Log.d(TAG, "Service discovery success" + service);
        if (!service.getServiceType().equals(mNsd.SERVICE_TYPE)) {
            Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
        } else if (service.getServiceName().equals(mNsd.mServiceName)) {
            Log.d(TAG, "Same machine: " + mNsd.mServiceName);
        } else if (service.getServiceName().contains(mNsd.mServiceName)){
            mNsd.mNsdManager.resolveService(service, new NsdManager.ResolveListener() {
                @Override
                public void onResolveFailed(NsdServiceInfo nsdServiceInfo, int i) {
                    Log.e(TAG, "Resolve failed" + i);
                }

                @Override
                public void onServiceResolved(NsdServiceInfo serviceInfo) {
                    Log.e(TAG, "Resolve Succeeded. " + serviceInfo);

                    String ip = serviceInfo.getHost().getHostAddress();
                    String port = String.valueOf(serviceInfo.getPort());

                    ApiClient.getInstance(context.getApplicationContext()).setLocalUrl(ip, "8000");
                    apiConnectionChanged(ApiClient.LOCAL);
                    mNsd.mService = serviceInfo;
                }
            });
        }
    }

    @Override
    public void onServiceLost(NsdServiceInfo service) {
        Log.e(TAG, "service lost" + service);
        apiConnectionChanged(ApiClient.CLOUD);
        if (mNsd.mService == service) {
            mNsd.mService = null;
        }
    }
}
