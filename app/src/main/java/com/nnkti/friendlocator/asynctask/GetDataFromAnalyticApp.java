package com.nnkti.friendlocator.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by nnkti on 11/15/2017.
 */

public class GetDataFromAnalyticApp extends AsyncTask<String, Integer, String> {

    @Override
    protected String doInBackground(String... params) {
        Socket kkSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        Log.e("CCC", params[0]);
        try {
            kkSocket = new Socket(params[0], 1234);
            Log.d("GetDataFromAnalyticApp", (InetAddress.getLocalHost().getHostName()));
            out = new PrintWriter(kkSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
        } catch (
                UnknownHostException e)

        {
            e.printStackTrace();
        } catch (
                IOException e)

        {
            e.printStackTrace();
        }

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String fromServer;
        StringBuilder result = new StringBuilder();

        if (out != null) {
            out.println("FRIEND_LOCATION");
        }

        try {
            Log.e("OUTPUT", "");
            if (in != null) {
                while ((fromServer = in.readLine()) != null) {
                    switch (fromServer) {
                        case "FRIEND_LOCATION_RESPONSE":
                            Log.d(("Receiving message: "), "");
                            break;
                        case "FRIEND_LOCATION_DONE":
                            Log.d("Done", "");
                            break;
                        default:
                            Log.d("OUTPUT", fromServer);
                            result.append(fromServer);
                            result.append("\n");
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e("OUTPUT", result.toString());
        if (out != null) {
            out.close();
        }
        try {
            if (in != null) {
                in.close();
            }
            stdIn.close();
            if (kkSocket != null) {
                kkSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
