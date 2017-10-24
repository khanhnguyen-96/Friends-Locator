package com.nnkti.friendlocator.models;

import com.nnkti.friendlocator.helpers.SharedPreferencesHelper;

/**
 * Created by nnkti on 10/24/2017.
 */

public class SimpleLocation {
    private double latitude;
    private double longitude;
    private String nickname;

    public SimpleLocation(double latitude, double longitude, String nickname) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.nickname = nickname;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
