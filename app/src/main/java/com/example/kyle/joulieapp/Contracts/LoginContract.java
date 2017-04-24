package com.example.kyle.joulieapp.Contracts;

import com.auth0.android.lock.Lock;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserProfile;
import com.example.kyle.joulieapp.Base.BasePresenter;
import com.example.kyle.joulieapp.Base.BaseView;

/**
 * Created by Kyle on 2017-04-16.
 */

public interface LoginContract {

    interface View extends BaseView<Presenter> {
        void showUserCreationFailed();
        void showMainActivity();
        void showAuthFailed();
        void showLoginCancelled();
        void showLoginError(String message);
        void login();
    }

    interface Presenter extends BasePresenter {
        void createNewUser(Credentials credentials);
        void loginUser(String idToken);


    }
}
