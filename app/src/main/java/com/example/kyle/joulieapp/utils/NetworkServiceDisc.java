package com.example.kyle.joulieapp.utils;

/**
 * Created by Kyle on 2017-04-09.
 */

import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.net.nsd.NsdManager;
import android.util.Log;

import com.example.kyle.joulieapp.api.ApiClient;

import static android.net.nsd.NsdManager.*;

public class NetworkServiceDisc {
    Context mContext;
    public NsdManager mNsdManager;
    private ResolveListener mResolveListener = null;
    private DiscoveryListener mDiscoveryListener = null;
    public static final String SERVICE_TYPE = "_workstation._tcp.";
    public static final String TAG = "NetworkServiceDiscovery";
    public String mServiceName = "joulie";
    public NsdServiceInfo mService;

    public NetworkServiceDisc(Context context) {
        mContext = context;
        mNsdManager = (NsdManager) mContext.getApplicationContext().getSystemService(Context.NSD_SERVICE);
    }
//    public void initializeNsd(ResolveListener resolveListener) {
//        this.mResolveListener = resolveListener;
//    }

//    public void initializeDiscoveryListener() {
//        mDiscoveryListener = new NsdManager.DiscoveryListener() {
//            @Override
//            public void onDiscoveryStarted(String regType) {
//                Log.d(TAG, "Service discovery started");
//            }
//            @Override
//            public void onServiceFound(NsdServiceInfo service) {
//                Log.d(TAG, "Service discovery success" + service);
//                if (!service.getServiceType().equals(SERVICE_TYPE)) {
//                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
//                } else if (service.getServiceName().equals(mServiceName)) {
//                    Log.d(TAG, "Same machine: " + mServiceName);
//                } else if (service.getServiceName().contains(mServiceName)){
//                    mNsdManager.resolveService(service, mResolveListener);
//                }
//            }
//            @Override
//            public void onServiceLost(NsdServiceInfo service) {
//                Log.e(TAG, "service lost" + service);
//                if (mService == service) {
//                    mService = null;
//                }
//            }
//            @Override
//            public void onDiscoveryStopped(String serviceType) {
//                Log.i(TAG, "Discovery stopped: " + serviceType);
//            }
//            @Override
//            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
//                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
//            }
//            @Override
//            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
//                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
//            }
//        };
//    }
//    public void initializeResolveListener() {
//        mResolveListener = new NsdManager.ResolveListener() {
//            @Override
//            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
//                Log.e(TAG, "Resolve failed" + errorCode);
//            }
//            @Override
//            public void onServiceResolved(NsdServiceInfo serviceInfo) {
//                Log.e(TAG, "Resolve Succeeded. " + serviceInfo);
//                if (serviceInfo.getServiceName().equals(mServiceName)) {
//                    Log.d(TAG, "Same IP.");
//                    return;
//                }
//
//                String ip = serviceInfo.getHost().toString();
//                String port = String.valueOf(serviceInfo.getPort());
//
//             //   ApiClient.getInstance(mContext.getApplicationContext()).setLocalUrl(serviceInfo.);
//                mService = serviceInfo;
//            }
//        };
//    }

    public void resolveService(NsdServiceInfo service){
        mNsdManager.resolveService(service, new ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo nsdServiceInfo, int i) {

            }

            @Override
            public void onServiceResolved(NsdServiceInfo nsdServiceInfo) {

            }
        });
    }

    public void discoverServices(DiscoveryListener discoveryListener) {
        stopDiscovery();  // Cancel any existing discovery request
        mDiscoveryListener = discoveryListener;
        mNsdManager.discoverServices(
                SERVICE_TYPE, PROTOCOL_DNS_SD, mDiscoveryListener);
    }
    public void stopDiscovery() {
        if (mDiscoveryListener != null) {
            try {
                mNsdManager.stopServiceDiscovery(mDiscoveryListener);
            } finally {
            }
            mDiscoveryListener = null;
        }
    }
    public NsdServiceInfo getChosenServiceInfo() {
        return mService;
    }
}
