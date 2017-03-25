package com.example.kyle.joulieapp;

import android.content.Intent;
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
import com.example.kyle.joulieapp.api.ApiClient;
import com.example.kyle.joulieapp.api.ApiService;
import com.example.kyle.joulieapp.utils.CredentialsManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private Lock lock;
    private GoogleAuthProvider gProvider;
    private FacebookAuthProvider fbProvider;
    private AuthenticationAPIClient aClient;
    private BaseCallback<UserProfile, AuthenticationException> mBaseCallback;
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
        apiService = ApiClient.getInstance(this.getApplicationContext()).getApiService();

        GoogleAuthHandler googleAuthHandler = new GoogleAuthHandler(gProvider);
        FacebookAuthHandler facebookAuthHandler = new FacebookAuthHandler(fbProvider);
        lock = Lock.newBuilder(auth0, callback)
                .withAuthenticationParameters(parameters)
                .withAuthHandlers(googleAuthHandler)
                .withAuthHandlers(facebookAuthHandler)
                // Add parameters to the Lock Builder
                .build(this);

        if (CredentialsManager.getCredentials(this).getIdToken() == null) {
            startActivity(lock.newIntent(this));
            return;
        }

        Log.d("TOKEN: ", CredentialsManager.getCredentials(this).getIdToken());
        HashMap<String,String> body = new HashMap<>();
        Call<String> call = apiService.newUser(body);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("tag", response.message());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("tag", t.getMessage());
            }
        });

        //aClient
        aClient.tokenInfo(CredentialsManager.getCredentials(this).getIdToken())
                .start(new BaseCallback<UserProfile, AuthenticationException>() {
                    @Override
                    public void onSuccess(final UserProfile payload) {
                        CredentialsManager.saveUserProfile(getApplicationContext(),payload);
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(AuthenticationException error) {
                        LoginActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Snackbar.make(findViewById(R.id.activity_login), "Session Expired, please Log In", Snackbar.LENGTH_SHORT).show();
                            }
                        });
                        CredentialsManager.deleteCredentials(getApplicationContext());
                        CredentialsManager.deleteUserProfile(getApplicationContext());
                        startActivity(lock.newIntent(LoginActivity.this));
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Your own Activity code
        lock.onDestroy(this);
        lock = null;
    }

    private LockCallback callback = new AuthenticationCallback() {
        @Override
        public void onAuthentication(Credentials credentials) {
            // Login Success response
            CredentialsManager.saveCredentials(getApplicationContext(), credentials);
            aClient.tokenInfo(CredentialsManager.getCredentials(LoginActivity.this).getIdToken())
                    .start(new BaseCallback<UserProfile, AuthenticationException>() {
                        @Override
                        public void onSuccess(final UserProfile payload) {
                            CredentialsManager.saveUserProfile(getApplicationContext(),payload);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }

                        @Override
                        public void onFailure(AuthenticationException error) {
                            LoginActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Snackbar.make(findViewById(R.id.activity_login), "Session Expired, please Log In", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                            CredentialsManager.deleteCredentials(getApplicationContext());
                            CredentialsManager.deleteUserProfile(getApplicationContext());
                            startActivity(lock.newIntent(LoginActivity.this));
                        }
                    });
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


}
