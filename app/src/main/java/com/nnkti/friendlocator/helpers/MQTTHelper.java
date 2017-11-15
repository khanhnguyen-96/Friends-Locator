package com.nnkti.friendlocator.helpers;

import android.content.Context;
import android.util.Log;

import com.nnkti.friendlocator.models.SimpleLocation;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by nnkti on 10/19/2017.
 */

public class MQTTHelper {
    private MqttAndroidClient mqttAndroidClient;

    private static final String serverUri = "tcp://m10.cloudmqtt.com:11778";

    public static final String CLIENT_ID = "CLIENT_ID";
    public static final String CHAT_SUB_TOPIC = "chatroom/chat";
    public static final String LOCATION_SUB_TOPIC = "chatroom/SentLocation";
    public static final String SHARED_LOCATION_SUB_TOPIC = "chatroom/SharedLocation";

    private static final String username = "phusiriw";
    private static final String password = "mhqBVcp18dsf";

    public MQTTHelper(Context context, String nickname) {

        mqttAndroidClient = new MqttAndroidClient(context, serverUri, String.valueOf(nickname.hashCode()));
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.w("mqtt", s);
            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Mqtt", mqttMessage.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
        connect();
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    private void connect() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());

        try {

            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopics();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "Failed to connect to: " + serverUri + exception.toString());
                }
            });


        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }

    private void subcribeATopic(final String topic) {
        try {
            mqttAndroidClient.subscribe(topic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w("Mqtt", "Subscribed!" + topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "Subscribed fail!");
                }
            });

        } catch (MqttException ex) {
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        }
    }

    private void subscribeToTopics() {
        subcribeATopic(CHAT_SUB_TOPIC);
        subcribeATopic(SHARED_LOCATION_SUB_TOPIC);
    }

    public void sendMessageToServer(String nickname, String message) {
        if (!message.isEmpty())
            try {
                MqttMessage messageToBeSent = new MqttMessage();
                String payload = nickname + ":" + message;
                payload = payload.replaceAll("\\s+", " ");
                messageToBeSent.setPayload(payload.getBytes());
                mqttAndroidClient.publish(CHAT_SUB_TOPIC, messageToBeSent);
            } catch (MqttException e) {
                System.err.println("Error Publishing: " + e.getMessage());
                e.printStackTrace();
            }
    }

    public void sendLocationToServer(SimpleLocation simpleLocation) {
        try {
            MqttMessage messageToBeSent = new MqttMessage();
            String payload = simpleLocation.getNickname() + " " + simpleLocation.getLatitude() + " " + simpleLocation.getLongitude();
            messageToBeSent.setPayload(payload.getBytes());
            mqttAndroidClient.publish(LOCATION_SUB_TOPIC, messageToBeSent);
        } catch (MqttException e) {
            System.err.println("Error Publishing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
