package com.nnkti.friendlocator.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.nnkti.friendlocator.R;
import com.nnkti.friendlocator.activities.MainActivity;
import com.nnkti.friendlocator.asynctask.SendRequestLocationsAsyncTask;
import com.nnkti.friendlocator.helpers.MQTTHelper;
import com.nnkti.friendlocator.helpers.SharedPreferencesHelper;
import com.nnkti.friendlocator.models.RequestLocationsAsyncTaskParams;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by nnkti on 10/20/2017.
 */

public class HomeFragment extends Fragment implements View.OnClickListener {
    View view;
    EditText etNickname, etAnalyzer;
    ImageButton btnSave, btnSaveAnalyzer;
    MQTTHelper mqttHelper;
    String nickname;
    public static final String IP_ANALYZER = "IP_ANALYZER";

    public static HomeFragment createNewInstance(MQTTHelper mqttHelper) {
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.mqttHelper = mqttHelper;
        return homeFragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        etNickname = (EditText) view.findViewById(R.id.et_nickname);
        etAnalyzer = (EditText) view.findViewById(R.id.et_analyzer);
        btnSave = (ImageButton) view.findViewById(R.id.btn_save_nickname);
        btnSaveAnalyzer = (ImageButton) view.findViewById(R.id.btn_save_analyzer);
        btnSave.setOnClickListener(this);
        btnSaveAnalyzer.setOnClickListener(this);
        String temp = SharedPreferencesHelper.readStringSharedPreferences(getActivity(), MQTTHelper.CLIENT_ID);
        etNickname.setText(temp);
        temp = SharedPreferencesHelper.readStringSharedPreferences(getActivity(), IP_ANALYZER);
        etAnalyzer.setText(temp);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == view.findViewById(R.id.btn_save_nickname)) {
            String nickname = etNickname.getText().toString();
            if (!nickname.isEmpty()) {
                SharedPreferencesHelper.writeStringSharedPreferences(getActivity(), MQTTHelper.CLIENT_ID, nickname);
                Toast.makeText(getContext(), "User Name Created", Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).getLocationPermission();
            } else {
                Toast.makeText(getContext(), "Invalid User Name", Toast.LENGTH_SHORT).show();
            }
        } else if (v == view.findViewById(R.id.btn_save_analyzer)) {
            String ipAnalyzer = etAnalyzer.getText().toString();
            if (!ipAnalyzer.isEmpty()) {
                SharedPreferencesHelper.writeStringSharedPreferences(getActivity(), IP_ANALYZER, ipAnalyzer);
                Toast.makeText(getContext(), "Analyzer IP saved", Toast.LENGTH_SHORT).show();
                SendRequestLocationsAsyncTask requestLocationsAsyncTask = new SendRequestLocationsAsyncTask();
                RequestLocationsAsyncTaskParams params = new RequestLocationsAsyncTaskParams(((MainActivity)getActivity()).onDataUpdate, ipAnalyzer);
                requestLocationsAsyncTask.execute(params);

//                Initialize data callback from analytic app

            } else {
                Toast.makeText(getContext(), "Invalid Analyzer IP", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.item_location_list).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
}
