package com.example.kyle.joulieapp.Contracts;

import com.example.kyle.joulieapp.Base.BasePresenter;
import com.example.kyle.joulieapp.Base.BaseView;

/**
 * Created by Kyle on 2017-04-16.
 */

public interface MainContract {

    interface View extends BaseView<Presenter> {

        void hideTitle();

        void showTitle(String title);

        boolean isActive();

        void showConnectionChanged(final int connection);

        void showRequestFailed(String message);

        void showRequestSuccess(String message);
    }

    interface Presenter extends BasePresenter {

        void ResetLocalServer();
    }
}
