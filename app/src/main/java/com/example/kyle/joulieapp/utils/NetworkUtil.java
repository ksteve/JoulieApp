package com.example.kyle.joulieapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.text.format.Formatter;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

/**
 * Created by Kyle on 2017-03-17.
 */

public class NetworkUtil {
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;

    public static final int NETWORK_STATUS_NOT_CONNECTED = 0;
    public static final int NETWORK_STAUS_WIFI = 1;
    public static final int NETWORK_STATUS_MOBILE = 2;
    public static final String TAG = "NetworkUtil";

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static int getConnectivityStatusString(Context context) {
        int conn = NetworkUtil.getConnectivityStatus(context);
        int status = 0;
        if (conn == NetworkUtil.TYPE_WIFI) {
            status = NETWORK_STAUS_WIFI;
        } else if (conn == NetworkUtil.TYPE_MOBILE) {
            status = NETWORK_STATUS_MOBILE;
        } else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
            status = NETWORK_STATUS_NOT_CONNECTED;
        }
        return status;
    }

    public static WifiManager getWifiStatus(Context context) {
        new NetworkSniffTask(context).execute();
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.getConnectionInfo().getIpAddress();
        return wifiManager;
    }


    public static class NetworkSniffTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<Context> mContextRef;
        final String TAG = "NetworkUtil";

        public NetworkSniffTask(Context context) {
            mContextRef = new WeakReference<Context>(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Context mContext = mContextRef.get();

            Log.d(TAG, "Let's sniff the network");

            try {

                if (mContext != null) {
                    // Fetch local host

                    WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
                   // InetAddress localhost =  wifiManager.getConnectionInfo().getIpAddress();



                    int ip = wifiManager.getConnectionInfo().getIpAddress();

                    // Convert little-endian to big-endianif needed
                    if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
                        ip = Integer.reverseBytes(ip);
                    }

                    byte[] ipByteArray = BigInteger.valueOf(ip).toByteArray();

                    // IPv4 usage
                    String ipAddressString;
                    try {
                        ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
                    } catch (UnknownHostException ex) {
                        Log.e("WIFIIP", "Unable to get host address.");
                        ipAddressString = null;
                    }

                    // looping
                    for (int i = 1; i <= 254; i++)
                    {
                        ipByteArray[3] = (byte)i;
                        InetAddress address = InetAddress.getByAddress(ipByteArray);


                        if (address.isReachable(10))
                        {
                            NetworkInterface asdf = NetworkInterface.getByInetAddress(address);
                            if(asdf != null) {
                                Log.d(TAG, asdf.getDisplayName());
                                Log.d(TAG, asdf.getName());
                                Log.d(TAG, asdf.getHardwareAddress().toString());
                            }
                           //System.out.println(address + " - Pinging... Pinging");
                            Log.d(TAG, address + " - Pinging... Pinging");
                            Log.d(TAG, address.getHostName());
                            Log.d(TAG, address.getCanonicalHostName());
                            Log.d(TAG, address.getHostAddress());

                        }
                        else if (!address.getHostAddress().equals(address.getHostName()))
                        {
                           // System.out.println(address + " - DNS lookup known..");
                            Log.d(TAG, address + " - DNS lookup known..");
                        }
                        else
                        {
                           // System.out.println(address + " - the host address and the host name are same");
                            Log.d(TAG, address + " - the host address and the host name are same");
                        }
                    }
                }
            } catch (Throwable t) {
                Log.e(TAG, "Well that's not good.", t);
            }

            return null;
        }
    }
}
