package com.nnkti.friendlocator.fragments;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.nnkti.friendlocator.R;
import com.nnkti.friendlocator.activities.MainActivity;
import com.nnkti.friendlocator.helpers.MQTTHelper;
import com.nnkti.friendlocator.helpers.SharedPreferencesHelper;

/**
 * Created by nnkti on 10/20/2017.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback {
    MQTTHelper mqttHelper;
    MapView map;
    GoogleMap currentMap;
    Location lastKnownLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    public static String LAST_LONGITUDE = "LAST_LONGITUDE";
    public static String LAST_LATITUDE = "LAST_LATITUDE";

    public static MapFragment createNewInstance(MQTTHelper mqttHelper) {
        MapFragment mapFragment = new MapFragment();
        mapFragment.mqttHelper = mqttHelper;
        return mapFragment;
    }

    private float getAverageZoomLevel() {
        return (currentMap.getMaxZoomLevel() + currentMap.getMinZoomLevel()) / 2;
    }

    public void getDeviceLocation() {
    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
        fusedLocationProviderClient = ((MainActivity) getActivity()).fusedLocationProviderClient;
        try {
            if (isLocationPermissionGranted()) {
                final Task locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this.getActivity(), new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = (Location) task.getResult();
                            if (lastKnownLocation == null) {
                                Toast.makeText(getContext(), "You may have turned off LOCATION SERVICE\nShowing your last recorded position", Toast.LENGTH_SHORT).show();
                                LatLng lastKnownLocationLatLng = new LatLng(SharedPreferencesHelper.readDoubleSharedPreferences(getActivity(), LAST_LATITUDE, 0),
                                        SharedPreferencesHelper.readDoubleSharedPreferences(getActivity(), LAST_LONGITUDE, 0));
                                Log.d("Location", "SharedPref last known LATITUDE: " + lastKnownLocationLatLng.latitude);
                                Log.d("Location", "SharedPref last known LONGITUDE: " + lastKnownLocationLatLng.longitude);
                                if ((lastKnownLocationLatLng.latitude == lastKnownLocationLatLng.longitude) && (lastKnownLocationLatLng.latitude == 0)) {
                                    Toast.makeText(getContext(), "Can't retrieve last known location", Toast.LENGTH_SHORT).show();
                                } else {
                                    currentMap.addMarker(new MarkerOptions().position(lastKnownLocationLatLng)
                                            .title(SharedPreferencesHelper.readStringSharedPreferences(getActivity(), MQTTHelper.CLIENT_ID)));
                                    currentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                            lastKnownLocationLatLng,
                                            getAverageZoomLevel()
                                    ));
                                }

                            } else {
                                LatLng lastKnownLocationLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                                Log.d("Location", "SharedPref last known LATITUDE: " + lastKnownLocationLatLng.latitude);
                                Log.d("Location", "SharedPref last known LONGITUDE: " + lastKnownLocationLatLng.longitude);

                                SharedPreferencesHelper.writeDoubleSharedPreferences(getActivity(), LAST_LATITUDE, lastKnownLocationLatLng.latitude);
                                SharedPreferencesHelper.writeDoubleSharedPreferences(getActivity(), LAST_LONGITUDE, lastKnownLocationLatLng.longitude);
                                currentMap.addMarker(new MarkerOptions().position(lastKnownLocationLatLng)
                                        .title(SharedPreferencesHelper.readStringSharedPreferences(getActivity(), MQTTHelper.CLIENT_ID)));
                                currentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        lastKnownLocationLatLng,
                                        getAverageZoomLevel()
                                ));
                            }
                        } else {
                            Log.d("Location", "Current location is null. Using defaults.");
                            Log.e("Location", "Exception: %s", task.getException());
//                            LatLng sydney = new LatLng(-33.852, 151.211);
//                            currentMap.addMarker(new MarkerOptions().position(sydney)
//                                    .title("Marker in Sydney"));
//                            currentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, currentMap.getMaxZoomLevel()));
//                            currentMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        currentMap = googleMap;
//        ((MainActivity) getActivity()).getDeviceLocation();
        getDeviceLocation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // inflate and return the layout
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        map = (MapView) v.findViewById(R.id.map_view);
        map.onCreate(savedInstanceState);
        if (!isLocationPermissionGranted()) {
            requestLocationPermission();
        }
        map.getMapAsync(this);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        map.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        map.onLowMemory();
    }

    private boolean isLocationPermissionGranted() {
        return ((MainActivity) getActivity()).mLocationPermissionGranted;
    }

    private void requestLocationPermission() {
        ((MainActivity) getActivity()).getLocationPermission();
    }
}
