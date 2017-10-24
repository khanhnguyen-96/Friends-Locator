import random
import threading

import paho.mqtt.client as mqtt

username = "phusiriw"
password = "mhqBVcp18dsf"
clientID = "clientPython"
serverUri = "m10.cloudmqtt.com"
port = 11778
mainTopic = "chatroom/#"
chatRoomSubTopic = "chatroom/chat"


def on_sub(client, userdata, mid, granted_qos):
    print()


def on_message(client, userdata, message):
    print("Received message '" + str(message.payload) + "' on topic '"
          + message.topic + "' with QoS " + str(message.qos))


mqttc = mqtt.Client(clientID, clean_session=False)
mqttc.username_pw_set(username, password)
mqttc.connect(serverUri, port, 60)
mqttc.subscribe(topic=mainTopic)

mqttc.on_subscribe = on_sub
mqttc.message_callback_add(chatRoomSubTopic, on_message)
mqttc.loop_start()


def pub():
    mqttc.publish("chatroom/chat", payload=random.normalvariate(30, 0.5), qos=0)
    threading.Timer(10, pub).start()


pub()
