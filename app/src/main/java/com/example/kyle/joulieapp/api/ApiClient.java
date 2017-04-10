package com.example.kyle.joulieapp.api;

import android.content.Context;
import android.provider.SyncStateContract;
import android.util.Log;

import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.utils.CredentialsManager;
import com.example.kyle.joulieapp.utils.NetworkUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Kyle on 2017-03-10.
 */

public class ApiClient {

    private final String TAG = getClass().getSimpleName();

    public static final int LOCAL = 0;
    public static final int CLOUD = 1;


    private static ApiClient instance = null;
    private String apiBaseUrl = Constants.CLOUD_URL;
    private String localUrl;
    private int connectionType = CLOUD;
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

            //add logging
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(httpLoggingInterceptor);

            //add headers to each request
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

    public void changeApiBaseUrl(int network){
        if(network == LOCAL){
            setApiBaseUrl(localUrl);
            setConnectionType(LOCAL);
        } else if(network == CLOUD) {
            setApiBaseUrl(Constants.CLOUD_URL);
            setConnectionType(CLOUD);
        }
    }

    private void setApiBaseUrl(String newApiBaseUrl){
        apiBaseUrl = newApiBaseUrl;
        Log.d(TAG, "Setting Api URL -> " + apiBaseUrl);

        mRetrofit = null;
        buildClient();
    }

    public void setLocalUrl(String ip, String port) {
        String localUrl = "http://" + ip + ":" + port;
        this.localUrl = localUrl;
    }

    private void setConnectionType(int type){
        connectionType = type;
    }

    public int getConnectionType(){
        return this.connectionType;
    }

    public String getApiBaseUrl(){
        return this.apiBaseUrl;
    }

    public ApiService getApiService(){
        return this.mApiService;
    }

}
