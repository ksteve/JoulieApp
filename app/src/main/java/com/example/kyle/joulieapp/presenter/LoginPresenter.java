package com.example.kyle.joulieapp.presenter;

import android.content.Context;

import com.example.kyle.joulieapp.Models.Usage;
import com.example.kyle.joulieapp.api.ApiClient;
import com.example.kyle.joulieapp.api.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kyle on 2017-04-05.
 */

public class LoginPresenter {

    private final Context context;
    private final LoginPresenterListener mListener;

    public interface LoginPresenterListener{
        void UsagesReady(List<Usage> Usages);
    }

    public LoginPresenter(LoginPresenterListener listener, Context context){
        this.mListener = listener;
        this.context = context;
    }


}
