package com.example.kyle.joulieapp.Presenters;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.example.kyle.joulieapp.Api.ApiClient;
import com.example.kyle.joulieapp.Contracts.NewDeviceContract;
import com.example.kyle.joulieapp.Contracts.ShareContract;
import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.DummyContent;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kyle on 2017-04-05.
 */

public class SharePresenter implements ShareContract.Presenter {

    private final Context context;
    private final ShareContract.View mShareView;
    private String user_id = "";

    public SharePresenter(ShareContract.View shareView, Context context){
        this.mShareView = shareView;
        mShareView.setPresenter(this);
        this.context = context;
    }

    @Override
    public void start() {

    }

    @Override
    public void findUserByEmail(String email) {
        ApiClient.getInstance(context.getApplicationContext()).getCloudApiService()
                .userSearch(email)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        if(!response.body().equals("none")) {
                            user_id = response.body();
                            mShareView.showFoundUser();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        //// TODO: 2017-04-05 throw error, display message to user
                        mShareView.showRequestFailed(t.getMessage());
                    }
                });
    }

    @Override
    public void shareDeviceWithUser(String deviceID) {
        ApiClient.getInstance(context.getApplicationContext()).getCloudApiService()
                .shareDevice(deviceID, user_id)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        mShareView.showDeviceShared();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        //// TODO: 2017-04-05 throw error, display message to user
                        mShareView.showRequestFailed(t.getMessage());
                    }
                });
    }
}
