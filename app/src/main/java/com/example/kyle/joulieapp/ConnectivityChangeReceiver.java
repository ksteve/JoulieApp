package com.example.kyle.joulieapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import com.example.kyle.joulieapp.utils.NetworkUtil;

public class ConnectivityChangeReceiver extends BroadcastReceiver {

    private String tag = getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        int status = NetworkUtil.getConnectivityStatusString(context);

        WifiManager wifiManager = NetworkUtil.getWifiStatus(context);
       //Log.v(tag, "wifi: " + wifiManager.getConnectionInfo());
//        Log.v(tag, wifiManager.getConnectionInfo().getBSSID());
//        Log.v(tag, wifiManager.getConnectionInfo().getSSID());
//        Log.v(tag, String.valueOf(wifiManager.getConnectionInfo().getIpAddress()));
//        Log.v(tag, String.valueOf(wifiManager.getConnectionInfo().getNetworkId()));
//        for(WifiConfiguration w: wifiManager.getConfiguredNetworks()){
//            Log.v(tag, w.SSID);
//        }

        Log.v(tag, "action: " + intent.getAction());
        Log.v(tag, "component: " + intent.getComponent());
        Bundle extras = intent.getExtras();
        if(extras != null){
            for (String key: extras.keySet()) {
                Log.v(tag, "key [" + key + "]: " +
                        extras.get(key));
            }
        }
        else {
            Log.v(tag, "no extras");
        }

    }



}
