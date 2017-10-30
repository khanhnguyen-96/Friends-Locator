package com.nnkti.friendlocator.Listener;

/**
 * Created by nnkti on 10/27/2017.
 */

public class OnDataUpdate {
    public OnDataUpdate.Listener listener;

    public void setListener(OnDataUpdate.Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void notifyDataChanged(String type);
    }
}
