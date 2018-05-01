package edu.msu.lizheng9.findmyfriends;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.*;
import android.location.Location;
import android.provider.Settings;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import java.util.logging.LogRecord;

import static java.security.AccessController.getContext;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

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
    private Marker currentMarker = null;
    private String selected = null;


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


    }



    private LatLng currentLocation;
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
        currentLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("You"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,14.0f));
        final Handler handler = new Handler();

        //parseJSONWithJSONObject(JSON);
        final View view = this.findViewById(android.R.id.content);
        final Runnable r = new Runnable() {
            public void run() {
                mMap.clear();
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


                Thread ta = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Cloud cloud = new Cloud();
                        final boolean success = cloud.sendLocation(device,Double.toString(longitude),Double.toString(latitude));
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                if (!success){
                                    Toast.makeText(MapsActivity.this,
                                            R.string.send_location_fail,
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                });
                ta.start();
                try { ta.join(3000); } catch (InterruptedException e) { e.printStackTrace(); }
                String $js = "";
                if(xmlJson != null){
                    $js = xmlJson;
                }
                try {

                    JSONArray obj = new JSONArray($js);
                    for (int i = 0; i < obj.length(); i++) {
                        JSONObject jsonObject = obj.getJSONObject(i);
                        String id = jsonObject.getString("androidid");
                        if (id.equals(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID) )){
                            continue;
                        }
                        String name = jsonObject.getString("username");
                        String longitude = jsonObject.getString("longitude");
                        String latitude = jsonObject.getString("latitude");
                        if(!longitude.isEmpty() && !latitude.isEmpty()){
                            double lon = Double.parseDouble(longitude);
                            double lat = Double.parseDouble(latitude);
                            LatLng cord = new LatLng(lat, lon);
                            Marker m = mMap.addMarker(new MarkerOptions().position(cord).title(name));
                            if(selected!=null && selected.equals(name)){
                                drawDist(m);
                                m.showInfoWindow();
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                registerListeners();
                final LatLng currentLocation = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(currentLocation).title("You"));
                if(currentMarker != null){
                    currentMarker.showInfoWindow();
                    drawDist(currentMarker);
                }

                Log.i("CURRENTTT", Double.toString(longitude));
                Log.i("CURRENTTT",Double.toString(latitude));
                handler.postDelayed(this, 5000);
            }
        };
        r.run();



        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                selected = marker.getTitle();
                drawDist(marker);
                return true;
            }
        });




    }

    private void drawDist(Marker marker){
        float[] results = new float[1];
        Location.distanceBetween(marker.getPosition().latitude,marker.getPosition().longitude,currentLocation.latitude,currentLocation.longitude,results);
        marker.setSnippet("Distance:" + String.valueOf(results[0])+"m");
        marker.showInfoWindow();
        LatLng currentLocation = new LatLng(latitude, longitude);
        PolylineOptions line = new PolylineOptions();
        line.add(currentLocation,marker.getPosition());
        mMap.addPolyline(line);
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
