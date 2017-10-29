package com.nnkti.friendlocator.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nnkti.friendlocator.R;
import com.nnkti.friendlocator.activities.MainActivity;
import com.nnkti.friendlocator.adapter.ChatRecyclerViewAdapter;
import com.nnkti.friendlocator.helpers.MQTTHelper;
import com.nnkti.friendlocator.helpers.SharedPreferencesHelper;
import com.nnkti.friendlocator.models.ChatModel;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;

/**
 * Created by nnkti on 10/20/2017.
 */

public class ChatRoomFragment extends Fragment implements View.OnClickListener, MqttCallbackExtended {
    View view;
    RecyclerView rvChatData;
    ChatRecyclerViewAdapter chatRecyclerViewAdapter;
    RecyclerView.LayoutManager layoutManager;
    EditText etInputMessage;
    ImageButton btnSend;
    MQTTHelper mqttHelper;
    ArrayList<ChatModel> chatData;

    public static ChatRoomFragment createNewInstance(MQTTHelper mqttHelper) {
        ChatRoomFragment chatRoomFragment = new ChatRoomFragment();
        chatRoomFragment.mqttHelper = mqttHelper;
        return chatRoomFragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mqttHelper.setCallback(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat_room, container, false);
//        scrollView = (ScrollView) view.findViewById(R.id.scroll_view_data_received);
//        tvReceivedMessages = (TextView) view.findViewById(R.id.tv_data_received);
        chatData = new ArrayList<>();

        rvChatData = (RecyclerView) view.findViewById(R.id.rv_chat_data);
        layoutManager = new LinearLayoutManager(this.getContext());
        rvChatData.setLayoutManager(layoutManager);
        chatRecyclerViewAdapter = new ChatRecyclerViewAdapter(chatData,
                SharedPreferencesHelper.readStringSharedPreferences(getActivity(), MQTTHelper.CLIENT_ID),
                getContext());
        rvChatData.setAdapter(chatRecyclerViewAdapter);

        etInputMessage = (EditText) view.findViewById(R.id.et_input_message);
        btnSend = (ImageButton) view.findViewById(R.id.btn_send);
        btnSend.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == view.findViewById(R.id.btn_send)) {
            String tempMsg = etInputMessage.getText().toString();
            String nickname = SharedPreferencesHelper.readStringSharedPreferences(getActivity(), MQTTHelper.CLIENT_ID);
            mqttHelper.sendMessageToServer(nickname, tempMsg);
            etInputMessage.setText("");
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
        if (topic.equals(MQTTHelper.CHAT_SUB_TOPIC)) {
            chatData.add(ChatModel.parseMessageToChatModel(message));
            chatRecyclerViewAdapter.notifyDataSetChanged();
            layoutManager.smoothScrollToPosition(rvChatData, null, chatData.size() - 1);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
