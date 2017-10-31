package com.nnkti.friendlocator.asynctask;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.nnkti.friendlocator.helpers.MQTTHelper;
import com.nnkti.friendlocator.helpers.SharedPreferencesHelper;
import com.nnkti.friendlocator.models.AsyncTaskParams;
import com.nnkti.friendlocator.models.SimpleLocation;

import static com.nnkti.friendlocator.fragments.MapFragment.LAST_LATITUDE;
import static com.nnkti.friendlocator.fragments.MapFragment.LAST_LONGITUDE;

/**
 * Created by nnkti on 10/24/2017.
 */

public class SendLocationAsyncTask extends AsyncTask<AsyncTaskParams, Integer, Void> implements OnCompleteListener {
    private AsyncTaskParams param;
    private SimpleLocation result;

    @Override
    protected Void doInBackground(final AsyncTaskParams... params) {
        param = params[0];
        while (true) {
            try {
                Thread.sleep(10000);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            try {
                final Task locationResult = param.getFusedLocationProviderClient().getLastLocation();
                locationResult.addOnCompleteListener(this);
            } catch (SecurityException e) {
                Log.e("Exception: %s", e.getMessage());
            }
        }
    }

    @Override
    public void onComplete(@NonNull Task task) {
        if (task.isSuccessful()) {
            Location lastKnownLocation = (Location) task.getResult();
            if (lastKnownLocation != null) {
                LatLng lastKnownLocationLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                Log.d("Location", "BG last known LATITUDE: " + lastKnownLocationLatLng.latitude);
                Log.d("Location", "BG last known LONGITUDE: " + lastKnownLocationLatLng.longitude);

                SharedPreferencesHelper.writeDoubleSharedPreferences(param.getFragmentActivity(), LAST_LATITUDE, lastKnownLocationLatLng.latitude);
                SharedPreferencesHelper.writeDoubleSharedPreferences(param.getFragmentActivity(), LAST_LONGITUDE, lastKnownLocationLatLng.longitude);
                result = new SimpleLocation(lastKnownLocation.getLatitude(),
                        lastKnownLocation.getLongitude(),
                        SharedPreferencesHelper.readStringSharedPreferences(param.getFragmentActivity(), MQTTHelper.CLIENT_ID)
                );
//                param.getMqttHelper().sendLocationToServer(result);
            }
        } else {
            Log.d("Location", "Current location is null.");
            Log.e("Location", "Exception: %s", task.getException());
        }
    }
}


