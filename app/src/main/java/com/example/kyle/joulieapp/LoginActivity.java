package com.example.kyle.joulieapp;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.auth0.android.Auth0;
import com.auth0.android.lock.AuthenticationCallback;
import com.auth0.android.lock.Lock;
import com.auth0.android.lock.LockCallback;
import com.auth0.android.lock.utils.LockException;
import com.auth0.android.result.Credentials;

public class LoginActivity extends AppCompatActivity {

    private Lock lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Auth0 auth0 = new Auth0(getResources().getString(R.string.auth0_client_id),
                getResources().getString(R.string.auth0_domain));

        lock = Lock.newBuilder(auth0, callback)
                // Add parameters to the Lock Builder
                .build(this);

        startActivity(lock.newIntent(this));
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
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
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
