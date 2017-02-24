package com.example.kyle.joulieapp.utils;

import android.provider.SyncStateContract;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 *  USING RETROFIT 2 For now
 *  Leave this code here in case change of mind
 */

/**
 * Created by Kyle on 2017-01-20.
 */
public class JoulieAPI {

    private ResponseListener mListener;

    private static JoulieAPI ourInstance = new JoulieAPI();

    public static JoulieAPI getInstance() {
        return ourInstance;
    }

    private JoulieAPI() {
    }

    public void restRequest(final RequestQueue queue, final String idToken){
        try {

            final String url = Constants.BASE_URL + Constants.CREATE_DEVICE_ENDPOINT;

            //compose request body
            final JSONObject jsonContainer = new JSONObject();
            final JSONObject jsonBody = new JSONObject();
            jsonBody.put("conn_name", "nest");
            jsonBody.put("adaptor", "nest");
            jsonBody.put("token",
                    "c.k9ESFrVN5RRMohA9drUlQRc5VINAUgdJUXKd1HK8aVveAWB6snK6wMvMN2zImZ8GlJIeqtcrxPofkUXePQdyWMqcnkPRYO5x6TYOEBgbVjiUnhBdczmh9TeEdCAfiR0pysSGkOwyjYKtn5gI");
            jsonBody.put("device_name", "thermostat");
            jsonBody.put("driver", "nest-thermostat");
            jsonBody.put("deviceId", "sPmk4pq4eGMa7nT5eiYy5G66DVALDY-J");
            jsonContainer.put("opts", jsonBody);

            JsonObjectRequest jaRequest = new JsonObjectRequest(Request.Method.POST, url, jsonContainer,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Display the first 500 characters of the response string.
//                            try {
                                mListener.onResSuccess(response);
//                            } catch (JSONException e) {

//                                e.printStackTrace();
//                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = null;
                    if (error instanceof NetworkError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (error instanceof ServerError) {
                        message = "The server could not be found. Please try again after some time!!";
                    } else if (error instanceof AuthFailureError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (error instanceof ParseError) {
                        message = "Parsing error! Please try again after some time!!";
                    } else if (error instanceof NoConnectionError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (error instanceof TimeoutError) {
                        message = "Connection TimeOut! Please check your internet connection.";
                    }

                    mListener.onResError(message);
                    //listener.onError(message);
                   // Log.v("", "error");
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    // Basic Authentication
                    //String auth = "Basic " + Base64.encodeToString(CONSUMER_KEY_AND_SECRET.getBytes(), Base64.NO_WRAP);

                    headers.put("Authorization", "Bearer " + idToken);
                    return headers;
                }
            };
            // Add the request to the RequestQueue.
            queue.add(jaRequest);

        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateTempRequest(final RequestQueue queue, final String idToken){
        try {

            final String url = Constants.BASE_URL + Constants.UPDATE_TEMP;

            //compose request body
            final JSONObject jsonContainer = new JSONObject();
            final JSONObject jsonBody = new JSONObject();
            //jsonBody.put("conn_name", "nest");
            jsonContainer.put("opts", "24");

            JsonObjectRequest jaRequest = new JsonObjectRequest(Request.Method.POST, url, jsonContainer,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Display the first 500 characters of the response string.
//                            try {
                            mListener.onResSuccess(response);
//                            } catch (JSONException e) {

//                                e.printStackTrace();
//                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = null;
                    if (error instanceof NetworkError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (error instanceof ServerError) {
                        message = "The server could not be found. Please try again after some time!!";
                    } else if (error instanceof AuthFailureError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (error instanceof ParseError) {
                        message = "Parsing error! Please try again after some time!!";
                    } else if (error instanceof NoConnectionError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (error instanceof TimeoutError) {
                        message = "Connection TimeOut! Please check your internet connection.";
                    }

                    mListener.onResError(message);
                    //listener.onError(message);
                    // Log.v("", "error");
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    // Basic Authentication
                    //String auth = "Basic " + Base64.encodeToString(CONSUMER_KEY_AND_SECRET.getBytes(), Base64.NO_WRAP);

                    headers.put("Authorization", "Bearer " + idToken);
                    return headers;
                }
            };
            // Add the request to the RequestQueue.
            queue.add(jaRequest);

        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void registerListener(ResponseListener listener){
        mListener = listener;
    }

    public interface ResponseListener {
        void onResSuccess(JSONObject response);
        void onResError(String errorMessage);
    }

}
