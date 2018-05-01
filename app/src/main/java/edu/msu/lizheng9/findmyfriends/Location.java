package edu.msu.lizheng9.findmyfriends;

import android.content.SharedPreferences;
import android.location.LocationManager;

/**
 * Created by Kevin on 4/30/2018.
 */

public class Location {
    private LocationManager locationManager = null;

    private double latitude = 0;
    private double longitude = 0;
    private boolean valid = false;

    private double toLatitude = 0;
    private double toLongitude = 0;
    private String to = "";

    private SharedPreferences settings = null;
    /**
     * Set all user interface components to the current state
     */
    private void setUI() {

    }
}
