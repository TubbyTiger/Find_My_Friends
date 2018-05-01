package edu.msu.lizheng9.findmyfriends;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

//    public void loadJSON(JSONArray[] dataJSON){
//        List<Integer> idsList = new ArrayList<>();
//        //location
//
//        //id
//        for(int i =0; i < dataJSON[1].length() ;++i){
//            try {
//                idsList.add(Integer.parseInt(dataJSON[1].get(i).toString()));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
//        //remove the captured pieces
//        for(int x=pieces.size()-1; x>-1; x--){
//            if(!idsList.contains(pieces.get(x).getId())){
//                pieces.remove(x);
//            }
//        }
//        for(int i=0; i<dataJSON[1].length()-1; i++) {
//            for(int j=i+1;  j<dataJSON[1].length();  j++) {
//                try {
//                    if(Integer.parseInt(dataJSON[1].get(i).toString()) == pieces.get(j).getId()) {
//                        CheckerPiece t = pieces.get(i);
//                        pieces.set(i, pieces.get(j));
//                        pieces.set(j, t);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        for(int i=0;  i<pieces.size(); i++) {
//
//
//            CheckerPiece piece = pieces.get(i);
//            try {
//                piece.moveTo(Float.parseFloat(dataJSON[0].get(i*2).toString()),Float.parseFloat(dataJSON[0].get(i*2+1).toString()));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            try {
//                piece.setQueen(Boolean.parseBoolean(dataJSON[2].get(i).toString()));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        //queens
//        view.invalidate();
//
//    }


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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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

    }
}
