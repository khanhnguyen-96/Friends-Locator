package com.nnkti.friendlocator.asynctask;

import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.nnkti.friendlocator.fragments.HomeFragment;
import com.nnkti.friendlocator.helpers.MQTTHelper;
import com.nnkti.friendlocator.helpers.SharedPreferencesHelper;
import com.nnkti.friendlocator.models.AsyncTaskParams;
import com.nnkti.friendlocator.models.RequestLocationsAsyncTaskParams;
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

public class SendRequestLocationsAsyncTask extends AsyncTask<RequestLocationsAsyncTaskParams, Integer, Void> {
    private static final String FRIENDSLOCATION = "FRIENDSLOCATION";

    @Override
    protected Void doInBackground(final RequestLocationsAsyncTaskParams... params) {
        try {

            Log.d("LocationAsync", "doInBackground: " + params[0].getIpAnalyzer());
            Socket theSocket = new Socket(
                    params[0].getIpAnalyzer(),
                    1234);

            Log.d("LocationAsync", "doInBackground: 2" );
            PrintWriter out = new PrintWriter(theSocket.getOutputStream(), true);
            BufferedReader fromServer =
                    new BufferedReader(new InputStreamReader(theSocket.getInputStream()));
            out.println(FRIENDSLOCATION);
            String received = "";
            while (received.isEmpty()) {
                if (fromServer.readLine() != null) {
                    received = fromServer.readLine();
                }
                Log.d("LocationAsync", "doInBackground: " + received);
            }

            Log.d("LocationAsync", "doInBackground: " + received);
            if (fromServer.readLine() != null) {
                String locations = fromServer.readLine();
                params[0].getOnDataUpdate().locationListener.notifyNewLocationsArrived(locations);
                theSocket.close();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}


