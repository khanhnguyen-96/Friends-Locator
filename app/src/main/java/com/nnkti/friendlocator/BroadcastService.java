package com.nnkti.friendlocator;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.nnkti.friendlocator.helpers.MQTTHelper;
import com.nnkti.friendlocator.helpers.SharedPreferencesHelper;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

import static com.nnkti.friendlocator.fragments.MapFragment.LAST_LATITUDE;
import static com.nnkti.friendlocator.fragments.MapFragment.LAST_LONGITUDE;

/**
 * Created by Aki on 11/1/2017.
 */

public class BroadcastService extends Service {
    static String UDP_BROADCAST = "UDPBroadcast";
    //Boolean shouldListenForUDPBroadcast = false;
    DatagramSocket socket;
    SharedPreferences pre;

    private void listenAndWaitAndThrowIntent(InetAddress broadcastIP, Integer port) throws Exception {
//        byte[] recvBuf = new byte[15000];

        if (socket == null || socket.isClosed()) {
            socket = new DatagramSocket(port, broadcastIP);
            socket.setBroadcast(true);
        }

        while (true) {
            TimeUnit.SECONDS.sleep(10);
            StringBuilder sb = new StringBuilder("COORDINATE ");
            sb.append(pre.getString(MQTTHelper.CLIENT_ID, "A"));
            sb.append(" ");
            sb.append(Double.longBitsToDouble(pre.getLong(LAST_LATITUDE, 0)));
            sb.append(" ");
            sb.append(Double.longBitsToDouble(pre.getLong(LAST_LONGITUDE, 0)));

            byte[] temp = sb.toString().getBytes();
            socket.send(new DatagramPacket(temp, temp.length, broadcastIP, 8888));

//                broadcastIntent(senderIP, sb.toString());
            Log.e("UDP", "sent: " + sb.toString());
        }
    }

    private void broadcastIntent(String senderIP, String message) {
        Intent intent = new Intent(BroadcastService.UDP_BROADCAST);
        intent.putExtra("sender", senderIP);
        intent.putExtra("message", message);
        sendBroadcast(intent);
    }

    Thread UDPBroadcastThread;

    void startListenForUDPBroadcast() {
        UDPBroadcastThread = new Thread(new Runnable() {
            public void run() {
                try {
                    DatagramPacket receivePacket = null;
                    socket = new DatagramSocket();
                    byte[] sendData = "FRIEND_LOCATOR_REQUEST".getBytes();
                    Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
                    while (interfaces.hasMoreElements()) {
                        NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();
                        if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                            continue;
                        }

                        for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                            InetAddress broadcast = interfaceAddress.getBroadcast();
                            if (broadcast == null) {
                                continue;
                            }
                            try {
                                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8888);
                                socket.send(sendPacket);

                                byte[] recvBuf = new byte[15000];
                                receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                                socket.receive(receivePacket);
                                String message = new String(receivePacket.getData()).trim();
//                                Log.e("UDP", "Got UDB broadcast from " + receivePacket.getAddress().getHostAddress() + ", message: " + message);

                                if (message.equals("FRIEND_LOCATOR_RESPONSE")) {
                                    listenAndWaitAndThrowIntent(receivePacket.getAddress(), 8888);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    while (shouldRestartSocketListen) {
                        if (receivePacket != null)
                            listenAndWaitAndThrowIntent(receivePacket.getAddress(), 8888);
                    }

                    //if (!shouldListenForUDPBroadcast) throw new ThreadDeath();
                } catch (Exception e) {
                    Log.i("UDP", "no longer listening for UDP broadcasts cause of error " + e.getMessage());
                }
            }
        });
        UDPBroadcastThread.start();
    }

    private Boolean shouldRestartSocketListen = true;

    void stopListen() {
        shouldRestartSocketListen = false;
        socket.close();
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
        stopListen();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        pre = getApplicationContext().getSharedPreferences("MySP", MODE_PRIVATE);
        shouldRestartSocketListen = true;
        startListenForUDPBroadcast();
        Log.i("UDP", "Service started");
        return START_STICKY;

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
