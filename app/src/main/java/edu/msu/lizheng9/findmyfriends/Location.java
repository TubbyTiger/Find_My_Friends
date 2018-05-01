package edu.msu.lizheng9.findmyfriends;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

/**
 * Created by Kevin on 4/30/2018.
 */

public class Location {
    public LocationManager locationManager = null;
    public Context context;
    private double latitude = 0;
    private double longitude = 0;
    private boolean valid = false;

    private double toLatitude = 0;
    private double toLongitude = 0;
    private String to = "";

    private SharedPreferences settings = null;
    private ActiveListener activeListener = new ActiveListener();

    /**
     * Set all user interface components to the current state
     */
    private class ActiveListener implements LocationListener {


        @Override
        public void onLocationChanged(android.location.Location location) {

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }

    ;

    public void setUI() {

    }

    public void registerListeners() {
        unregisterListeners();
        // Create a Criteria object
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(false);

        String bestAvailable = locationManager.getBestProvider(criteria, true);
        if (bestAvailable != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(bestAvailable, 500, 1, activeListener);
        }

    }

    public void unregisterListeners() {
        locationManager.removeUpdates(activeListener);

    }

}
