package com.example.kyle.joulieapp.Presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import com.example.kyle.joulieapp.Api.ApiClient;
import com.example.kyle.joulieapp.Contracts.DeviceContract;
import com.example.kyle.joulieapp.Contracts.MainContract;
import com.example.kyle.joulieapp.Utils.CredentialsManager;
import com.example.kyle.joulieapp.Utils.NetworkServiceDisc;
import com.example.kyle.joulieapp.Utils.NetworkUtil;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kyle on 2017-04-05.
 */

public class MainPresenter implements MainContract.Presenter, NsdManager.DiscoveryListener {

    private final Context context;
    private final SharedPreferences mSharedPreferences;
    private final MainContract.View mMainView;
    private final NetworkServiceDisc mNsd;
    private static final String TAG = "Main Presenter";
    private boolean mFirstLoad = true;
    private boolean mNgrokSyncRequired = true;

    @Override
    public void start() {
        if(mFirstLoad) {
           // checkNgrokCloud();
            mMainView.showConnectionChanged(ApiClient.CLOUD);
            testConnection();
            mFirstLoad = false;
        }
    }

    public MainPresenter(MainContract.View mainView,SharedPreferences sharedPreferences, Context context){
        this.context = context;
        this.mSharedPreferences = sharedPreferences;
        this.mMainView = mainView;
        this.mMainView.setPresenter(this);
        this.mNsd = new NetworkServiceDisc(this.context.getApplicationContext());
       // this.mNsd.initializeNsd(this);
    }

    public void pause(){
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
        ApiClient.getInstance(context.getApplicationContext()).setCurrentConnection(serverType);
        mMainView.showConnectionChanged(serverType);
    }

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
                    mNsd.mService = serviceInfo;
                    String ip = serviceInfo.getHost().getHostAddress();
                    String port = String.valueOf(serviceInfo.getPort());

                    ApiClient.getInstance(context.getApplicationContext()).setLocalUrl(ip, "8000");
                    apiConnectionChanged(ApiClient.LOCAL);
                    if(mNgrokSyncRequired) {
                        getLocalUrl();
                    }
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

    private void getLocalUrl(){
        ApiClient.getInstance(context.getApplicationContext()).getLocalApiService()
                .getNgrokUrl()
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.body().isEmpty()){
                            mMainView.showRequestFailed("could not get local connection");
                        } else {
                            updateNgrokCloud(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        mMainView.showRequestFailed(t.getMessage());
                    }
                });
    }

    private void updateNgrokCloud(String url) {

        HashMap<String,String> body = new HashMap<>();
        body.put("url", url);

        ApiClient.getInstance(context.getApplicationContext()).getCloudApiService()
                .updateUser(body)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(!response.body().equals("user updated")){
                            mMainView.showRequestFailed("could not update local url");
                        } else {
                            mNgrokSyncRequired = false;
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        mMainView.showRequestFailed("could not update local url");
                    }
                });
    }

    @Override
    public void ResetLocalServer() {
        resetRobot();
    }

    private void resetRobot(){
        ApiClient.getInstance(context.getApplicationContext()).getApiService()
                .resetRobot()
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.body().equals("Done")) {
                            resetDevices();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        mMainView.showRequestFailed(t.getMessage());
                    }
                });
    }

    private void resetDevices(){

       String access_token = CredentialsManager.getCredentials(context.getApplicationContext()).getAccessToken();

        ApiClient.getInstance(context.getApplicationContext()).getApiService()
                .resetDevices(access_token)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                    //    if(response.body().equals("Done")) {
                            mMainView.showRequestSuccess("Server Reset");
                     //   }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        mMainView.showRequestFailed(t.getMessage());
                    }
                });
    }

}
