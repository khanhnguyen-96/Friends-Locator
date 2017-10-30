package com.nnkti.friendlocator.Listener;

/**
 * Created by nnkti on 10/27/2017.
 */

public class OnDataUpdate {
    public OnDataUpdate.Listener listener;
    public OnDataUpdate.MenuListener menuListener;

    public void setListener(OnDataUpdate.Listener listener) {
        this.listener = listener;
    }
    public void setGetClickedMarker(OnDataUpdate.MenuListener listener) {
        this.menuListener = listener;
    }

    public interface Listener {
        void notifyDataChanged(String type);
        void browseUserMenuClicked();
    }
    public interface MenuListener {
        void getClickedMarker(int pos);
    }
}
