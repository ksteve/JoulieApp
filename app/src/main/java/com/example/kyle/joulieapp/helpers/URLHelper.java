package com.example.kyle.joulieapp.helpers;

import android.net.Uri;
import android.provider.SyncStateContract;

/**
 * Created by Kyle on 2017-01-21.
 */

public class URLHelper {

    final static String BASE_URL = "joulie.herokuapp.com";
    final static  String LOCAL_URL = "localhost";

    public static final String buildUrl(){

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority(LOCAL_URL);
        return builder.build().toString();
    }


}
