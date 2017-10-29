package com.nnkti.friendlocator.models;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by nnkti on 10/29/2017.
 */

public class ChatModel {
    private String nickname;
    private String message;

    public ChatModel(String nickname, String message) {
        this.nickname = nickname;
        this.message = message;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static ChatModel parseMessageToChatModel(MqttMessage message) {
        int divider = message.toString().indexOf(':');
        String nickname = message.toString().substring(0, divider);
        String parsedMessage = message.toString().substring(divider + 1, message.toString().length());
        return new ChatModel(nickname, parsedMessage);
    }
}
