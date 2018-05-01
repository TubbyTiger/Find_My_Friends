package edu.msu.lizheng9.findmyfriends;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.*;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String username;
    private String device;
    private Location location_;
    private String xmlJson;

    private LocationManager locationManager = null;

    private double latitude = 0;
    private double longitude = 0;
    private boolean valid = false;

    private double toLatitude = 0;
    private double toLongitude = 0;
    private String to = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
       //  Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();


        // Get the location manager
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }

        if (b!= null){
            username = (String)b.get("username");
            device= (String)b.get("device");
        }
        final View view = this.findViewById(android.R.id.content);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Cloud cloud = new Cloud();
                final boolean success = cloud.getUsers();
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!success){
                            Toast.makeText(MapsActivity.this,
                                    R.string.get_users_fail,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                xmlJson = cloud.xmlJsonArray;
            }
        });
        t.start();
        try { t.join(3000); } catch (InterruptedException e) { e.printStackTrace(); }
    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        // Add a marker in Sydney and move the camera
        LatLng currentLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("You"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,14.0f));
        //parseJSONWithJSONObject(JSON);
        String $js = "[{\"androidid\":\"12314565455412\",\"0\":\"12314565455412\",\"username\":\"randomPos1\",\"1\":\"randomPos1\",\"longitude\":\"16.23638\",\"2\":\"16.23638\",\"latitude\":\"-16.26818\",\"3\":\"-16.26818\"},{\"androidid\":\"1681531822121\",\"0\":\"1681531822121\",\"username\":\"randomPos2\",\"1\":\"randomPos2\",\"longitude\":\"-95.91185\",\"2\":\"-95.91185\",\"latitude\":\"-2.74545\",\"3\":\"-2.74545\"},{\"androidid\":\"437fcb34a5ba7ea0\",\"0\":\"437fcb34a5ba7ea0\",\"username\":\"jdjd\",\"1\":\"jdjd\",\"longitude\":\"\",\"2\":\"\",\"latitude\":\"\",\"3\":\"\"},{\"androidid\":\"bc6107c67e5a4d7c\",\"0\":\"bc6107c67e5a4d7c\",\"username\":\"jackie\",\"1\":\"jackie\",\"longitude\":\"\",\"2\":\"\",\"latitude\":\"\",\"3\":\"\"}]";
        try {
            JSONArray obj = new JSONArray($js);
            for (int i = 0; i < obj.length(); i++) {
                JSONObject jsonObject = obj.getJSONObject(i);
                String id = jsonObject.getString("androidid");
                String name = jsonObject.getString("username");
                String longitude = jsonObject.getString("longitude");
                String latitude = jsonObject.getString("latitude");
                if(!longitude.isEmpty() && !latitude.isEmpty()){
                    double lon = Double.parseDouble(longitude);
                    double lat = Double.parseDouble(latitude);
                    LatLng cord = new LatLng(lat, lon);
                    mMap.addMarker(new MarkerOptions().position(cord).title(name));

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Called when this application becomes foreground again.
     */
    @Override
    protected void onResume() {
        super.onResume();
        registerListeners();


    }
    /**
     * Called when this application is no longer the foreground application.
     */
    @Override
    protected void onPause() {
        unregisterListeners();
        super.onPause();
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
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            Location location = locationManager.getLastKnownLocation(bestAvailable);
            onLocation(location);
        }

    }
    private void onLocation(Location location) {
        if(location == null) {
            return;
        }

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.i("location",Double.toString(latitude));
        Log.i("location",Double.toString(longitude));
        valid = true;
        setUI();
    }
    public void unregisterListeners() {
        locationManager.removeUpdates(activeListener);

    }

    /**
     * Set all user interface components to the current state
     */
    private void setUI() {

    }
    private ActiveListener activeListener = new ActiveListener();

    private class ActiveListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {

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
    };
}
