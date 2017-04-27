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
        ApiClient.getInstance(context.getApplicationContext()).getmAuth0ApiService()
                .findUser("user_id","email:" + email)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        mShareView.showFoundUser();
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        //// TODO: 2017-04-05 throw error, display message to user
                        mShareView.showRequestFailed(t.getMessage());
                    }
                });
    }

    @Override
    public void shareDeviceWithUser(String deviceID, String userID) {
        ApiClient.getInstance(context.getApplicationContext()).getCloudApiService()
                .shareDevice(deviceID, userID)
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
