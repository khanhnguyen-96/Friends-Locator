import helpers.MqttHelper;
import models.SimpleLocation;
import org.eclipse.paho.client.mqttv3.*;

import java.util.ArrayList;

/**
 * Created by nnkti on 10/27/2017.
 */
public class Broadcaster implements MqttCallbackExtended, IMqttMessageListener, Runnable {
    private MqttHelper mqttHelper;
    ArrayList<SimpleLocation> locations;

    private void init() {
        locations = new ArrayList<>();
        try {
            mqttHelper = new MqttHelper(this, this);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        if (mqttHelper != null) {
            mqttHelper.setCallback(this);
        }
    }

    public static void main(String[] args) {
        Broadcaster broadcaster = new Broadcaster();
        broadcaster.init();
        Thread background = new Thread(broadcaster);
        background.start();
    }

    @Override
    public void connectComplete(boolean b, String s) {
        System.out.println("Connected - " + s);
    }

    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        if (topic.equals(MqttHelper.LOCATION_SUB_TOPIC)) {
            checkArrivedMessage(mqttMessage.toString());
        }
    }

    private void checkArrivedMessage(String arrived) {
        SimpleLocation locationToCheck = SimpleLocation.getSimpleLocationFromLine(arrived);
        if (locations.isEmpty()) {
            locations.add(locationToCheck);
        } else {
            int pos = existedNickname(locationToCheck.getNickname());
            if (pos == -1) {
//                add new user
                locations.add(locationToCheck);
            } else {
//                Update user location
                locations.get(pos).setLatitude(locationToCheck.getLatitude());
                locations.get(pos).setLongitude(locationToCheck.getLongitude());
            }
        }
    }

    private int existedNickname(String nickname) {
//        case -1: user not exists
//        other cases from (0 -> n): user position in locations array
        for (SimpleLocation curr : locations) {
            if (curr.getNickname().equals(nickname))
                return locations.indexOf(curr);
        }
        return -1;
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    @Override
    public void run() {
        // sending locations
        if (!locations.isEmpty()) {
            mqttHelper.broadcastLocations(locations);
        }
        System.out.println("-----Suspending Background thead");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Stop suspending Background thead");
        run();
    }
}
