package com.nnkti.friendlocator.models;

import android.support.v4.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.nnkti.friendlocator.helpers.MQTTHelper;

/**
 * Created by nnkti on 10/24/2017.
 */

public class AsyncTaskParams {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FragmentActivity fragmentActivity;
    private MQTTHelper mqttHelper;

    public MQTTHelper getMqttHelper() {
        return mqttHelper;
    }

    public void setMqttHelper(MQTTHelper mqttHelper) {
        this.mqttHelper = mqttHelper;
    }

    public FusedLocationProviderClient getFusedLocationProviderClient() {
        return fusedLocationProviderClient;
    }

    public FragmentActivity getFragmentActivity() {
        return fragmentActivity;
    }

    public void setFusedLocationProviderClient(FusedLocationProviderClient fusedLocationProviderClient) {
        this.fusedLocationProviderClient = fusedLocationProviderClient;
    }

    public void setFragmentActivity(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
    }

    public AsyncTaskParams(FusedLocationProviderClient fusedLocationProviderClient, FragmentActivity fragmentActivity, MQTTHelper mqttHelper) {
        this.fusedLocationProviderClient = fusedLocationProviderClient;
        this.fragmentActivity = fragmentActivity;
        this.mqttHelper = mqttHelper;
    }
}
