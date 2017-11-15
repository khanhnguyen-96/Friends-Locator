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
import com.nnkti.friendlocator.activities.MainActivity;
import com.nnkti.friendlocator.fragments.HomeFragment;
import com.nnkti.friendlocator.helpers.MQTTHelper;
import com.nnkti.friendlocator.helpers.SharedPreferencesHelper;
import com.nnkti.friendlocator.models.AsyncTaskParams;
import com.nnkti.friendlocator.models.SimpleLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import static com.nnkti.friendlocator.fragments.MapFragment.LAST_LATITUDE;
import static com.nnkti.friendlocator.fragments.MapFragment.LAST_LONGITUDE;

/**
 * Created by nnkti on 10/24/2017.
 */

public class SendLocationAsyncTask extends AsyncTask<AsyncTaskParams, Integer, Void> implements OnCompleteListener {
    private AsyncTaskParams param;
    private SimpleLocation result;
    private static final String FRIENDSLOCATION = "FRIENDSLOCATION";

    @Override
    protected Void doInBackground(final AsyncTaskParams... params) {
        param = params[0];
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
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

    private void sendLocationRequestToServer() {
        String ipAnalyzer = SharedPreferencesHelper.readStringSharedPreferences(param.getFragmentActivity(), HomeFragment.IP_ANALYZER);
        if (!ipAnalyzer.isEmpty())
            try {
                Socket theSocket = new Socket(
                        ipAnalyzer,
                        6789);
                PrintWriter out = new PrintWriter(theSocket.getOutputStream(), true);
                BufferedReader fromServer =
                        new BufferedReader(new InputStreamReader(theSocket.getInputStream()));
                out.println(FRIENDSLOCATION);

                String locations = fromServer.readLine();
                param.getOnDataUpdate().locationListener.notifyNewLocationsArrived(locations);
                theSocket.close();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
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
                sendLocationRequestToServer();
            }
        } else {
            Log.d("Location", "Current location is null.");
            Log.e("Location", "Exception: %s", task.getException());
        }
    }
}


