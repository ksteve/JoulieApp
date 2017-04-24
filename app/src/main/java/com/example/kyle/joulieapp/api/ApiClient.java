package com.example.kyle.joulieapp.Api;

import android.content.Context;
import android.util.Log;

import com.example.kyle.joulieapp.Utils.CredentialsManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
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
    private String localUrl;
    private int currentConnection;
    private OkHttpClient mOkHttpClient;
    private ApiService mCloudApiService;
    private ApiService mLocalApiService;

    private Context mContext;

    public static synchronized ApiClient getInstance(Context context){
        if(instance == null){
            instance = new ApiClient(context);
        }

        return  instance;
    }

    private ApiClient(Context context){
        this.mContext = context.getApplicationContext();
        buildOkHttp();
        setCurrentConnection(CLOUD);
        buildCloudRetroFit();
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

    public void buildCloudRetroFit(){
           Retrofit mRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.CLOUD_URL)
                    //.baseUrl("http://192.168.2.14:3000/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(mOkHttpClient)
                    .build();

        this.mCloudApiService = mRetrofit.create(ApiService.class);
    }

    public void buildLocalRetroFit(){
            Retrofit mRetrofit = new Retrofit.Builder()
                    .baseUrl(localUrl)
                    //.baseUrl("http://192.168.2.14:3000/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(mOkHttpClient)
                    .build();

        this.mLocalApiService = mRetrofit.create(ApiService.class);
    }

    public void setCurrentConnection(int endpoint){
        currentConnection = endpoint;
    }

    public int getCurrentConnection(){
        return this.currentConnection;
    }

    public void setLocalUrl(String ip, String port) {
        String localUrl = "http://" + ip + ":" + port;
        this.localUrl = localUrl;
        buildLocalRetroFit();
    }

    public ApiService getApiService() {
        switch (currentConnection){
            case LOCAL:
                return getLocalApiService();

            case CLOUD:
                return getCloudApiService();
        }

        return  null;
    }

    public ApiService getCloudApiService(){
        return this.mCloudApiService;
    }

    public ApiService getLocalApiService(){
        return this.mLocalApiService;
    }

}
