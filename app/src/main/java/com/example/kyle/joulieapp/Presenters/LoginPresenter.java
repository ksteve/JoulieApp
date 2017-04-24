package com.example.kyle.joulieapp.Presenters;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.facebook.FacebookAuthHandler;
import com.auth0.android.facebook.FacebookAuthProvider;
import com.auth0.android.google.GoogleAuthHandler;
import com.auth0.android.google.GoogleAuthProvider;
import com.auth0.android.lock.AuthenticationCallback;
import com.auth0.android.lock.Lock;
import com.auth0.android.lock.LockCallback;
import com.auth0.android.lock.utils.LockException;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserProfile;
import com.example.kyle.joulieapp.Api.ApiClient;
import com.example.kyle.joulieapp.Contracts.LoginContract;
import com.example.kyle.joulieapp.Models.Usage;
import com.example.kyle.joulieapp.R;
import com.example.kyle.joulieapp.Utils.CredentialsManager;
import com.example.kyle.joulieapp.Views.LoginActivity;
import com.example.kyle.joulieapp.Views.MainActivity;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kyle on 2017-04-05.
 */

public class LoginPresenter implements LoginContract.Presenter {

    private final LoginContract.View mLoginView;
    private final Context mContext;
    private AuthenticationAPIClient aClient;

    public LoginPresenter(LoginContract.View loginView, AuthenticationAPIClient client, Context context){
        this.aClient = client;
        this.mContext = context;
        this.mLoginView = loginView;
        mLoginView.setPresenter(this);
    }

    @Override
    public void start() {
       mLoginView.login();
    }

    @Override
    public void loginUser(String idToken) {
        //aClient
        aClient.tokenInfo(idToken)
                .start(new BaseCallback<UserProfile, AuthenticationException>() {
                    @Override
                    public void onSuccess(final UserProfile payload) {
                        CredentialsManager.saveUserProfile(mContext.getApplicationContext(), payload);
                        mLoginView.showMainActivity();
                    }

                    @Override
                    public void onFailure(AuthenticationException error) {
                        if(!error.getMessage().equals("Request failed")){
                            CredentialsManager.deleteCredentials(mContext.getApplicationContext());
                            CredentialsManager.deleteUserProfile(mContext.getApplicationContext());
                        }
                        mLoginView.showAuthFailed();
                    }
                });
    }

    @Override
    public void createNewUser(final Credentials credentials) {
        HashMap<String,String> body = new HashMap<>();
        ApiClient.getInstance(mContext.getApplicationContext()).getCloudApiService()
            .newUser(body)
            .enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("tag", response.message());
                CredentialsManager.saveCredentials(mContext.getApplicationContext(), credentials);
                loginUser(CredentialsManager.getCredentials(mContext.getApplicationContext()).getIdToken());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                mLoginView.showUserCreationFailed();
                Log.d("tag", t.getMessage());
            }
        });
    }
}
