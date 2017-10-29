package com.nnkti.friendlocator.Listener;

/**
 * Created by nnkti on 10/27/2017.
 */

public class OnLocationsUpdateListener {
    public OnLocationsUpdateListener.Listener listener;

    public void setListener(OnLocationsUpdateListener.Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void notifyLocationsChanges();
    }
}
