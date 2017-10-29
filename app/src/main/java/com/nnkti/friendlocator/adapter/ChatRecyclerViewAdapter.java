package com.nnkti.friendlocator.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nnkti.friendlocator.R;
import com.nnkti.friendlocator.helpers.MQTTHelper;
import com.nnkti.friendlocator.helpers.SharedPreferencesHelper;
import com.nnkti.friendlocator.models.ChatModel;

import java.util.ArrayList;

/**
 * Created by nnkti on 10/29/2017.
 */

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ChatRecyclerViewHolder> {
    private Context context;
    private ArrayList<ChatModel> chatModels;
    private String thisUser;

    public ChatRecyclerViewAdapter(ArrayList<ChatModel> chatModels, String thisUser, Context context) {
        this.chatModels = chatModels;
        this.thisUser = thisUser;
        this.context = context;
    }

    @Override
    public ChatRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_model_view_holder, parent, false);
        return new ChatRecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ChatRecyclerViewHolder holder, int position) {
        ChatModel chatModel = chatModels.get(position);
        holder.nickname.setText(chatModel.getNickname());
        holder.message.setText(chatModel.getMessage());
        if (!chatModel.getNickname().equals(thisUser)){
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.cardview_dark_background));
        }
    }

    @Override
    public int getItemCount() {
        return chatModels.size();
    }

    class ChatRecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView nickname;
        private TextView message;
        private CardView cardView;

        public ChatRecyclerViewHolder(View itemView) {
            super(itemView);
            nickname = (TextView) itemView.findViewById(R.id.tv_chat_nickname);
            message = (TextView) itemView.findViewById(R.id.tv_chat_message);
            cardView = (CardView) itemView.findViewById(R.id.cardview);
        }

    }
}
