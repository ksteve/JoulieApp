package com.example.kyle.joulieapp.Views;

import android.content.Intent;
import android.graphics.LinearGradient;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.example.kyle.joulieapp.Contracts.LoginContract;
import com.example.kyle.joulieapp.Presenters.LoginPresenter;
import com.example.kyle.joulieapp.R;
import com.example.kyle.joulieapp.Api.ApiClient;
import com.example.kyle.joulieapp.Api.ApiService;
import com.example.kyle.joulieapp.Utils.CredentialsManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements LoginContract.View {

    private Lock lock;
    private GoogleAuthProvider gProvider;
    private FacebookAuthProvider fbProvider;
    private AuthenticationAPIClient aClient;
    private BaseCallback<UserProfile, AuthenticationException> mBaseCallback;
    private LoginContract.Presenter mLoginPresenter;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Auth0 auth0 = new Auth0(getResources().getString(R.string.auth0_client_id),
                getResources().getString(R.string.auth0_domain));

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("scope", "openid offline_access");
        aClient = new AuthenticationAPIClient(auth0);
        gProvider = new GoogleAuthProvider(getString(R.string.google_server_client_id), aClient);
        fbProvider = new FacebookAuthProvider(aClient);

        GoogleAuthHandler googleAuthHandler = new GoogleAuthHandler(gProvider);
        FacebookAuthHandler facebookAuthHandler = new FacebookAuthHandler(fbProvider);

        lock = Lock.newBuilder(auth0, callback)
                .withAuthenticationParameters(parameters)
                .withAuthHandlers(googleAuthHandler)
                .withAuthHandlers(facebookAuthHandler)
                .build(this);


        new LoginPresenter(this, aClient, this);

       }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        mLoginPresenter = presenter;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoginPresenter.start();
    }

    @Override
    public void login(){

        if (CredentialsManager.getCredentials(this).getIdToken() == null) {
            startActivity(lock.newIntent(this));
            return;
        }
        Log.d("TOKEN: ", CredentialsManager.getCredentials(LoginActivity.this).getIdToken());
        mLoginPresenter.loginUser(CredentialsManager.getCredentials(this).getIdToken());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lock.onDestroy(this);
        lock = null;
    }

    private LockCallback callback = new AuthenticationCallback() {
        @Override
        public void onAuthentication(Credentials credentials) {
            //Log.d("TOKEN: ", CredentialsManager.getCredentials(LoginActivity.this).getIdToken());
            mLoginPresenter.createNewUser(credentials);
        }

        @Override
        public void onCanceled() {
            // Login Cancelled response
            Snackbar.make(findViewById(R.id.activity_login), "Login Cancelled", Snackbar.LENGTH_SHORT).show();
        }

        @Override
        public void onError(LockException error){
            // Login Error response
            Snackbar.make(findViewById(R.id.activity_login), error.getMessage(), Snackbar.LENGTH_SHORT).show();
        }
    };

    @Override
    public void showUserCreationFailed() {

    }

    @Override
    public void showMainActivity() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    @Override
    public void showAuthFailed() {
      //  Log.d("FAILED", error.getMessage());
        LoginActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                Snackbar.make(findViewById(R.id.activity_login), "Session Expired, please Log In", Snackbar.LENGTH_SHORT).show();
            }
        });
        startActivity(lock.newIntent(LoginActivity.this));
    }

    @Override
    public void showLoginCancelled() {
        // Login Cancelled response
        Snackbar.make(findViewById(R.id.activity_login), "Login Cancelled", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showLoginError(String message) {
        // Login Error response
        Snackbar.make(findViewById(R.id.activity_login), message, Snackbar.LENGTH_SHORT).show();
    }
}
