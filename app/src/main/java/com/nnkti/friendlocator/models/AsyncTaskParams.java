package com.nnkti.friendlocator.models;

import android.support.v4.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.nnkti.friendlocator.Listener.OnDataUpdate;
import com.nnkti.friendlocator.helpers.MQTTHelper;

/**
 * Created by nnkti on 10/24/2017.
 */

public class AsyncTaskParams {
    private OnDataUpdate onDataUpdate;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FragmentActivity fragmentActivity;
    private MQTTHelper mqttHelper;

    public OnDataUpdate getOnDataUpdate() {
        return onDataUpdate;
    }

    public void setOnDataUpdate(OnDataUpdate onDataUpdate) {
        this.onDataUpdate = onDataUpdate;
    }

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

    public AsyncTaskParams(OnDataUpdate onDataUpdate, FusedLocationProviderClient fusedLocationProviderClient, FragmentActivity fragmentActivity, MQTTHelper mqttHelper) {
        this.onDataUpdate = onDataUpdate;
        this.fusedLocationProviderClient = fusedLocationProviderClient;
        this.fragmentActivity = fragmentActivity;
        this.mqttHelper = mqttHelper;
    }
}
