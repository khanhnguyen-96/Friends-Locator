package com.nnkti.friendlocator.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.nnkti.friendlocator.R;
import com.nnkti.friendlocator.asynctask.SendLocationAsyncTask;
import com.nnkti.friendlocator.fragments.ChatRoomFragment;
import com.nnkti.friendlocator.fragments.HomeFragment;
import com.nnkti.friendlocator.fragments.MapFragment;
import com.nnkti.friendlocator.helpers.MQTTHelper;
import com.nnkti.friendlocator.helpers.SharedPreferencesHelper;
import com.nnkti.friendlocator.models.AsyncTaskParams;
import com.nnkti.friendlocator.models.SimpleLocation;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MqttCallbackExtended {
    MQTTHelper mqttHelper;
    public FloatingActionButton fab;
    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigationView;
    public FusedLocationProviderClient fusedLocationProviderClient;
    public boolean mLocationPermissionGranted;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Home");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, HomeFragment.createNewInstance(mqttHelper), "home").commit();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.hide();
        checkNewUser();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void enable2MenuItems(boolean enable) {
        navigationView.getMenu().getItem(1).setEnabled(enable);
        navigationView.getMenu().getItem(2).setEnabled(enable);
    }

    private void checkNewUser() {
        String nickname = SharedPreferencesHelper.readStringSharedPreferences(this, MQTTHelper.CLIENT_ID);
        if (nickname.equals("")) {
            enable2MenuItems(false);
        } else {
            getLocationPermission();
            startMqttAndGetCurrentLocation(nickname);
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        final int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        // delay 300ms before changing fragment
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (id == R.id.nav_home) {
                    toolbar.setTitle("Home");
                    fab.hide();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, HomeFragment.createNewInstance(mqttHelper), "home").commit();
                } else if (id == R.id.nav_chat_room) {
                    toolbar.setTitle("Chat Room");
                    fab.hide();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, ChatRoomFragment.createNewInstance(mqttHelper), "chat_room").commit();
                } else if (id == R.id.nav_map) {
                    toolbar.setTitle("Friends Locator");
                    fab.show();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, MapFragment.createNewInstance(mqttHelper), "map").commit();
                }
                hideSoftKeyboard();
            }
        }, 300);
        return true;
    }

    public void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            enable2MenuItems(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void startMqttAndGetCurrentLocation(String nickname) {
//            If user has already created a nickname
        mqttHelper = new MQTTHelper(getApplicationContext(), nickname);
        mqttHelper.setCallback(this);

        fusedLocationProviderClient = new FusedLocationProviderClient(this);
        if (mLocationPermissionGranted) {
            AsyncTaskParams params = new AsyncTaskParams(fusedLocationProviderClient, this, mqttHelper);
            SendLocationAsyncTask sendLocationAsyncTask = new SendLocationAsyncTask();
            sendLocationAsyncTask.execute(params);
        }
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {

    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.w("Received:", "Topic:" + topic + "Message:" + message.toString());
//                receivedMessages += mqttMessage.toString() + "\n";
//                dataReceived.setText(receivedMessages);
//                TODO
        if (topic.equals(MQTTHelper.SHARED_LOCATION_SUB_TOPIC)) {
//            SimpleLocation.parseMessageToSimpleLocation(message.toString());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    enable2MenuItems(true);
                    startMqttAndGetCurrentLocation(SharedPreferencesHelper.readStringSharedPreferences(this, MQTTHelper.CLIENT_ID));
                } else {
                    this.finish();
                    System.exit(0);
                }
            }
        }
    }
}
