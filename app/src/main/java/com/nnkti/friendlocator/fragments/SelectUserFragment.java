package com.nnkti.friendlocator.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.nnkti.friendlocator.Listener.OnDataUpdate;
import com.nnkti.friendlocator.R;
import com.nnkti.friendlocator.activities.MainActivity;
import com.nnkti.friendlocator.adapter.UserRecyclerViewAdapter;
import com.nnkti.friendlocator.helpers.MQTTHelper;
import com.nnkti.friendlocator.helpers.SharedPreferencesHelper;
import com.nnkti.friendlocator.models.SimpleLocation;

import java.util.ArrayList;

/**
 * Created by nnkti on 10/20/2017.
 */

public class SelectUserFragment extends Fragment implements View.OnClickListener, UserRecyclerViewAdapter.RecyclerViewClickListener {
    ArrayList<SimpleLocation> locations;
    View view;
    ImageButton btnExit;
    RecyclerView rvUser;
    LinearLayoutManager linearLayoutManager;
    UserRecyclerViewAdapter userRecyclerViewAdapter;
    OnDataUpdate onDataUpdate;

    public static SelectUserFragment createNewInstance(ArrayList<SimpleLocation> locations) {
        SelectUserFragment selectUserFragment = new SelectUserFragment();
        selectUserFragment.locations = locations;
        return selectUserFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_select_user, container, false);
        btnExit = (ImageButton) view.findViewById(R.id.btn_exit_fragment);
        btnExit.setOnClickListener(this);
        rvUser = (RecyclerView) view.findViewById(R.id.rv_user);
        linearLayoutManager = new LinearLayoutManager(this.getContext());
        rvUser.setLayoutManager(linearLayoutManager);
        userRecyclerViewAdapter = new UserRecyclerViewAdapter(locations,
                SharedPreferencesHelper.readStringSharedPreferences(getActivity(), MQTTHelper.CLIENT_ID),
                getContext(),
                this
                );
        rvUser.setAdapter(userRecyclerViewAdapter);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_exit_fragment) {
            getActivity().getSupportFragmentManager().popBackStack();
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
        onDataUpdate = ((MainActivity)getActivity()).onDataUpdate;
    }

    @Override
    public void onClick(View view, int position) {
        onDataUpdate.menuListener.getClickedMarker(position);
        getActivity().getSupportFragmentManager().popBackStack();
    }
}
