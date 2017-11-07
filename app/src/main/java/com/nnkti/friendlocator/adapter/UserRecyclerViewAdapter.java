package com.nnkti.friendlocator.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.nnkti.friendlocator.R;
import com.nnkti.friendlocator.models.ChatModel;
import com.nnkti.friendlocator.models.SimpleLocation;

import java.util.ArrayList;

/**
 * Created by nnkti on 10/29/2017.
 */

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.UserRecyclerViewHolder> {
    private Context context;
    private ArrayList<SimpleLocation> locations;
    private String thisUser;
    private RecyclerViewClickListener recyclerViewClickListener;

    public UserRecyclerViewAdapter(ArrayList<SimpleLocation> locations, String thisUser, Context context, RecyclerViewClickListener recyclerViewClickListener) {
        this.locations = locations;
        this.thisUser = thisUser;
        this.context = context;
        this.recyclerViewClickListener = recyclerViewClickListener;
    }

    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }

    @Override
    public UserRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_view_holder, parent, false);
        return new UserRecyclerViewHolder(v, recyclerViewClickListener);
    }

    @Override
    public void onBindViewHolder(UserRecyclerViewHolder holder, int position) {
        SimpleLocation thisLocation = locations.get(position);

        if (thisLocation.getNickname().equals(thisUser)) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
            String temp = thisUser + " - ME";
            holder.nickname.setText(temp);
        } else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.cardview_dark_background));
            holder.nickname.setText(thisLocation.getNickname());
        }
    }

    @Override
    public int getItemCount() {
        return (locations != null) ? (locations.size()) : 0;
    }

    class UserRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView nickname;
        private CardView cardView;
        private LinearLayout linearLayout;
        private RecyclerViewClickListener recyclerViewClickListener;

        public UserRecyclerViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            nickname = (TextView) itemView.findViewById(R.id.tv_item_user);
            cardView = (CardView) itemView.findViewById(R.id.cv_user);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.ll_chat_view_holder);

            linearLayout.setOnClickListener(this);
            recyclerViewClickListener = listener;
        }

        @Override
        public void onClick(View v) {
            recyclerViewClickListener.onClick(v, getAdapterPosition());
        }
    }
}
