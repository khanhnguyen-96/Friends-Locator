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
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.nnkti.friendlocator.Listener.OnDataUpdate;
import com.nnkti.friendlocator.R;
import com.nnkti.friendlocator.asynctask.SendLocationAsyncTask;
import com.nnkti.friendlocator.fragments.ChatRoomFragment;
import com.nnkti.friendlocator.fragments.HomeFragment;
import com.nnkti.friendlocator.fragments.MapFragment;
import com.nnkti.friendlocator.helpers.MQTTHelper;
import com.nnkti.friendlocator.helpers.SharedPreferencesHelper;
import com.nnkti.friendlocator.models.AsyncTaskParams;
import com.nnkti.friendlocator.models.ChatModel;
import com.nnkti.friendlocator.models.SimpleLocation;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MqttCallbackExtended {
    MQTTHelper mqttHelper;
    public FloatingActionButton fab;
    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigationView;
    public FusedLocationProviderClient fusedLocationProviderClient;
    public ArrayList<SimpleLocation> sharedLocations;
    public boolean mLocationPermissionGranted;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public OnDataUpdate onDataUpdate;
    public ArrayList<ChatModel> chatData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Set up toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

//        replace home fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, HomeFragment.createNewInstance(mqttHelper), "home").commit();
//        initialize drawer
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
//        Initialize floating action button (for map fragment)
        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.hide();

        chatData = new ArrayList<>();
        checkNewUser(); // start checking if this is a new user or not

//        Set up locations update listener
        onDataUpdate = new OnDataUpdate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.item_location_list) {
            onDataUpdate.listener.browseUserMenuClicked();
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        switch (topic) {
            case MQTTHelper.SHARED_LOCATION_SUB_TOPIC:
                sharedLocations = SimpleLocation.parseMessageToSimpleLocation(message.toString());
                onDataUpdate.listener.notifyDataChanged(MQTTHelper.SHARED_LOCATION_SUB_TOPIC);
            case MQTTHelper.CHAT_SUB_TOPIC:
                chatData.add(ChatModel.parseMessageToChatModel(message));
                onDataUpdate.listener.notifyDataChanged(MQTTHelper.CHAT_SUB_TOPIC);
            default:
                Log.w("Received:", "Topic:" + topic + "Message:" + message.toString());
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
                // If request is cancelled, the result array will be empty.
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
