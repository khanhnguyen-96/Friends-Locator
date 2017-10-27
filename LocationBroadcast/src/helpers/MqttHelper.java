package helpers;

import models.SimpleLocation;
import org.eclipse.paho.client.mqttv3.*;

import java.util.ArrayList;

/**
 * Created by nnkti on 10/27/2017.
 */
public class MqttHelper {
    private MqttClient mqttClient;
    private IMqttMessageListener iMqttMessageListener;

    private static final String serverUri = "tcp://m10.cloudmqtt.com:11778";

    private static final String CLIENT_ID = "SERVER";
    public static final String LOCATION_SUB_TOPIC = "chatroom/SentLocation";
    private static final String SHARED_LOCATION_SUB_TOPIC = "chatroom/SharedLocation";

    private static final String username = "phusiriw";
    private static final String password = "mhqBVcp18dsf";

    public MqttHelper(MqttCallbackExtended mqttCallbackExtended, IMqttMessageListener iMqttMessageListener) throws MqttException {
        mqttClient = new MqttClient(serverUri, CLIENT_ID);
        mqttClient.setCallback(mqttCallbackExtended);
        this.iMqttMessageListener = iMqttMessageListener;
        connect();
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttClient.setCallback(callback);
    }

    private void connect() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());

        try {
            mqttClient.connect(mqttConnectOptions);
        } catch (MqttException ex) {
            ex.printStackTrace();
        }
        subscribeATopic(LOCATION_SUB_TOPIC);
    }

    private void subscribeATopic(final String topic) {
        try {
            mqttClient.subscribe(topic, iMqttMessageListener);

        } catch (MqttException ex) {
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        }
    }

    public void broadcastLocations(ArrayList<SimpleLocation> simpleLocations) {
        try {
            MqttMessage messageToBeSent = new MqttMessage();
            StringBuilder payload = new StringBuilder();
            for (SimpleLocation curr : simpleLocations) {
                payload.append(curr.getNickname())
                        .append(" ")
                        .append(curr.getLatitude())
                        .append(" ")
                        .append(curr.getLongitude())
                        .append("\n");
            }
            payload.substring(0, payload.length() - 1);
            messageToBeSent.setPayload(payload.toString().getBytes());
            System.out.println(payload);
//            TODO
            mqttClient.publish(SHARED_LOCATION_SUB_TOPIC, messageToBeSent);
        } catch (MqttException e) {
            System.err.println("Error Publishing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
