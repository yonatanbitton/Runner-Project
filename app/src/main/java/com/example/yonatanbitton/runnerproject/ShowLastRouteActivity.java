package com.example.yonatanbitton.runnerproject;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class ShowLastRouteActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<LatLng> markersArray = new ArrayList<LatLng>();
    double[] lats;
    double[] longs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_last_route);
        Bundle extras = getIntent().getExtras(); /* Get points data from the called activity*/
        if (extras != null) {
            lats = extras.getDoubleArray("lats");
            longs = extras.getDoubleArray("longs");
            //The key argument here must match that used in the other activity
        }
        for (int i=0; i<lats.length; i++){
            LatLng p = new LatLng(lats[i],longs[i]);
            markersArray.add(p);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        if (markersArray.size() == 0)
            Toast.makeText(getApplicationContext(), "No route was performed", Toast.LENGTH_LONG).show();
        else {
            LatLng initPos = markersArray.get(0);
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(initPos.latitude, initPos.longitude))
                    .title("INITPOS")
                    .anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            for (int i = 1; i < markersArray.size(); i++) {
                createMarker(markersArray.get(i).latitude, markersArray.get(i).longitude, i + 1);
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(markersArray.get(0)));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);
         }
    }
    /**
     * This function Writes the name, age, finish time, points into db file as json.
     * @param  double latitude, double longitude, int index, information needed to create the marker
     * @return Marker with the relevant information
     */
    protected Marker createMarker(double latitude, double longitude, int index) {
        String strInx = Integer.toString(index);
        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(Integer.toString(index))
                .anchor(0.5f, 0.5f)
        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.goodrunner)));
    }

}



