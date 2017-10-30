package com.nnkti.friendlocator.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.nnkti.friendlocator.helpers.MQTTHelper;
import com.nnkti.friendlocator.helpers.SharedPreferencesHelper;

/**
 * Created by nnkti on 10/20/2017.
 */

public class HomeFragment extends Fragment implements View.OnClickListener {
    View view;
    EditText etNickname;
    ImageButton btnSave;
    MQTTHelper mqttHelper;
    String nickname;

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
        btnSave = (ImageButton) view.findViewById(R.id.btn_save_nickname);
        btnSave.setOnClickListener(this);
        String nickname = SharedPreferencesHelper.readStringSharedPreferences(getActivity(), MQTTHelper.CLIENT_ID);
        etNickname.setText(nickname);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == view.findViewById(R.id.btn_save_nickname)) {
            String nickname = etNickname.getText().toString();
            if (!nickname.isEmpty()) {
                SharedPreferencesHelper.writeStringSharedPreferences(getActivity(), MQTTHelper.CLIENT_ID, nickname);
                Toast.makeText(getContext(),"User Name Created",Toast.LENGTH_SHORT).show();
                ((MainActivity)getActivity()).getLocationPermission();
            } else {
                Toast.makeText(getContext(),"Invalid User Name",Toast.LENGTH_SHORT).show();
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
