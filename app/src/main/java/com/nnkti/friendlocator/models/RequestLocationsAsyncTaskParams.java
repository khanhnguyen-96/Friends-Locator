package com.nnkti.friendlocator.models;

import android.support.v4.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.nnkti.friendlocator.Listener.OnDataUpdate;
import com.nnkti.friendlocator.helpers.MQTTHelper;

/**
 * Created by nnkti on 10/24/2017.
 */

public class RequestLocationsAsyncTaskParams {
    private OnDataUpdate onDataUpdate;
    private String ipAnalyzer;

    public String getIpAnalyzer() {
        return ipAnalyzer;
    }

    public void setIpAnalyzer(String ipAnalyzer) {
        this.ipAnalyzer = ipAnalyzer;
    }

    public OnDataUpdate getOnDataUpdate() {
        return onDataUpdate;
    }

    public void setOnDataUpdate(OnDataUpdate onDataUpdate) {
        this.onDataUpdate = onDataUpdate;
    }

    public RequestLocationsAsyncTaskParams(OnDataUpdate onDataUpdate, String ipAnalyzer) {
        this.onDataUpdate = onDataUpdate;
        this.ipAnalyzer = ipAnalyzer;
    }
}
