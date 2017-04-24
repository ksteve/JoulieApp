package com.example.kyle.joulieapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserProfile;
import com.example.kyle.joulieapp.R;

import java.util.HashMap;

/**
 * Created by Kyle on 2017-01-20.
 */

public class CredentialsManager {

    public static void saveCredentials(Context context, Credentials credentials){
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.auth0_preferences), Context.MODE_PRIVATE);

        sharedPref.edit()
                .putString(Constants.ID_TOKEN, credentials.getIdToken())
                .putString(Constants.REFRESH_TOKEN, credentials.getRefreshToken())
                .putString(Constants.ACCESS_TOKEN, credentials.getAccessToken())
                .putString(Constants.CREDENTIAL_TYPE, credentials.getType())
                .commit();
    }

    public static Credentials getCredentials(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.auth0_preferences), Context.MODE_PRIVATE);

        Credentials credentials = new Credentials(
                sharedPref.getString(Constants.ID_TOKEN, null),
                sharedPref.getString(Constants.ACCESS_TOKEN, null),
                sharedPref.getString(Constants.CREDENTIAL_TYPE, null),
                sharedPref.getString(Constants.REFRESH_TOKEN, null));

        return credentials;
    }

    public static void deleteCredentials(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.auth0_preferences), Context.MODE_PRIVATE);

        sharedPref.edit()
                .putString(Constants.ID_TOKEN, null)
                .putString(Constants.REFRESH_TOKEN, null)
                .putString(Constants.ACCESS_TOKEN, null)
                .putString(Constants.CREDENTIAL_TYPE, null)
                .commit();
    }

    public static void saveUserProfile(Context context, UserProfile profile){
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.auth0_preferences), Context.MODE_PRIVATE);

        sharedPref.edit()
                .putString(context.getString(R.string.user_name), profile.getName())
                .putString(context.getString(R.string.user_email), profile.getEmail())
                .putString(context.getString(R.string.user_picture), profile.getPictureURL())
                .commit();
    }

    public static HashMap<String, String> getUserProfile(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.auth0_preferences), Context.MODE_PRIVATE);

        HashMap<String, String> profile = new HashMap<String, String>();
        profile.put(context.getString(R.string.user_name), sharedPref.getString(context.getString(R.string.user_name), null));
        profile.put(context.getString(R.string.user_email), sharedPref.getString(context.getString(R.string.user_email), null));
        profile.put(context.getString(R.string.user_picture), sharedPref.getString(context.getString(R.string.user_picture), null));

        return profile;
    }

    public static void deleteUserProfile(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.auth0_preferences), Context.MODE_PRIVATE);

        sharedPref.edit()
                .putString(context.getString(R.string.user_name), null)
                .putString(context.getString(R.string.user_email), null)
                .putString(context.getString(R.string.user_picture), null)
                .commit();
    }

}
