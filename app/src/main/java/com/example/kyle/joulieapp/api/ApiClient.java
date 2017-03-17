package com.example.kyle.joulieapp.api;

import android.content.Context;

import com.example.kyle.joulieapp.utils.CredentialsManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Kyle on 2017-03-10.
 */

public class ApiClient {

    private static ApiClient instance = null;
    private String apiBaseUrl = Constants.BASE_URL;
    private OkHttpClient mOkHttpClient;
    private Retrofit mRetrofit = null;
    private ApiService mApiService;
    private Context mContext;

    public static synchronized ApiClient getInstance(Context context){
        if(instance == null){
            instance = new ApiClient(context);
        }

        return  instance;
    }

    private ApiClient(Context context){
        this.mContext = context.getApplicationContext();
        buildClient();
    }

    private void buildClient(){
        buildOkHttp();
        buildRetroFit();
        this.mApiService = mRetrofit.create(ApiService.class);
    }

    public void buildOkHttp(){
        if(mOkHttpClient == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();

                    Request request = original.newBuilder()
                            // .header("Accept", "application/json")
                            //  .header("Content-Type", "application/json")
                            .header("Authorization", "Bearer " + CredentialsManager.getCredentials(mContext).getIdToken())
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }
            });
            mOkHttpClient = httpClient.build();
        }
    }

    public void buildRetroFit(){
        if(mRetrofit == null) {
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(apiBaseUrl)
                    //.baseUrl("http://192.168.2.14:3000/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(mOkHttpClient)
                    .build();
        }
    }

    public void changeApiBaseUrl(String newApiBaseUrl){
        apiBaseUrl = newApiBaseUrl;
        mRetrofit = null;
        buildClient();
    }

    public ApiService getApiService(){
        return this.mApiService;
    }

}
