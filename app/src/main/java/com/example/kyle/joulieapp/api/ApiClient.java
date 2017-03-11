package com.example.kyle.joulieapp.api;

import android.content.Context;
import android.net.Credentials;
import android.provider.SyncStateContract;
import android.support.annotation.RestrictTo;

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

    private static Retrofit retrofit = null;

    public static Retrofit getClient(final Context context){
        if(retrofit == null){

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(new Interceptor() {
                  @Override
                  public Response intercept(Interceptor.Chain chain) throws IOException {
                      Request original = chain.request();

                      Request request = original.newBuilder()
                              .header("Authorization", "Bearer: " + CredentialsManager.getCredentials(context).getIdToken())
                              .method(original.method(), original.body())
                              .build();

                      return chain.proceed(request);
                    }
            });

            OkHttpClient client = httpClient.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl("https://joulie-cylon.herokuapp.com/")
                    //.baseUrl("http://192.168.2.14:3000/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

}
