package com.example.kyle.joulieapp;

import android.os.AsyncTask;

import com.amazonaws.services.iot.model.ListThingsRequest;
import com.amazonaws.services.iot.model.ListThingsResult;

/**
 * Created by Kyle on 2016-12-03.
 */
public class IoTApi {
    private static IoTApi ourInstance = new IoTApi();

    public static IoTApi getInstance() {
        return ourInstance;
    }

    private IoTApi() {
    }

    public void callListThings(onResultListener listener){
        new ListThings(listener).execute(null, null, null);
    }

    public class ListThings extends AsyncTask<Object, Integer, ListThingsResult> {

        private onResultListener mListener;

        private ListThings(onResultListener caller){
            mListener = caller;
        }

        @Override
        protected ListThingsResult doInBackground(Object[] objects) {
            ListThingsResult res = LoginActivity.androidIoTClient.listThings(new ListThingsRequest());
            return res;
        }

        @Override
        protected void onPostExecute(ListThingsResult result) {
            super.onPostExecute(result);
                mListener.onResult(result);
        }
    }

    public interface onResultListener{
        void onResult(ListThingsResult result);
    }



}
