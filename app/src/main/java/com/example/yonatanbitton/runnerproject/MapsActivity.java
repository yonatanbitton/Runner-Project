package com.example.yonatanbitton.runnerproject;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.FileOutputStream;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    final private String TAG = "MapsJon";
    private GoogleMap mMap;
    private ArrayList<LatLng> points = new ArrayList<>();
    final ExecutorService executor = Executors.newFixedThreadPool(2);
    private int maxNumOfPoints=1000;
    LocationManager locationManager; // needs it to access my loc
    long count=0;
    LatLng currentPoint;
    LatLng initPos=null;
    int currentIndex = 1;
    boolean finishFlag=false;

    /**
     * On create: preview the layout, instanciate the location manager, check permission, and if one of the network providers is enabled - connect and mark the current location.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(getApplicationContext(), "Please wait for GPS connection", Toast.LENGTH_LONG).show();
        Log.d(TAG, "on create started");
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager() /* Obtain the SupportMapFragment and get notified when the map is ready to be used.*/
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE); /* Accessing current location */

        /* Check at the Manifast that the ACCESS_FINE_LOCATION is requested */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){ /* Check if the network provider is enabled.*/
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() { /* Params - provider, min time, min dist, location listener */
                @Override
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude(); /* Get the updated latitude and longitude */
                    LatLng latLng = new LatLng(latitude, longitude); /* Instantiate LatLng instance */
                    currentPoint = latLng; /* Update current point */

                    Geocoder geocoder = new Geocoder(getApplicationContext()); /* Instantiate Geocoder class - which converts the longitute and latitude to meaning full addres  */
                    try {
                        List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1); /* Last param - max number of addresses to return */
                        String str = addressList.get(0).getLocality(); /* Beersheva */
                        str += ",";
                        str += addressList.get(0).getCountryName(); /* Israel */
                        str += " is the current location.";
                        mMap.addMarker(new MarkerOptions().position(latLng).title(str)); /* Add the marker with the relevant data */
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng)); /* Move the camera to the new location */
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null); /* Zoom */
                        if (initPos==null) {
                            initPos = latLng;
                            points.add(initPos); /* Adding initPos to points*/
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {}

                @Override
                public void onProviderEnabled(String s) {}

                @Override
                public void onProviderDisabled(String s) {}
            });

        }
        else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){ /* else, Check if the GPS provider is enabled.*/
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() { /* Params - provider, min time, min dist, location listener */
                @Override
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude(); /* Get the updated latitude and longitude */
                    LatLng latLng = new LatLng(latitude, longitude); /* Instantiate LatLng instance */
                    currentPoint = latLng; /* Update current point */
                    Geocoder geocoder = new Geocoder(getApplicationContext()); /* Instantiate Geocoder class - which converts the longitute and latitude to meaning full addres  */
                    try {
                        List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1); /* Last param - max number of addresses to return */
                        String str = addressList.get(0).getLocality(); /* Beersheva */
                        str += ",";
                        str += addressList.get(0).getCountryName(); /* Israel */
                        str += " is the current location.";
                        mMap.addMarker(new MarkerOptions().position(latLng).title(str)); /* Add the marker with the relevant data */
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng)); /* Move the camera to the new location */
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null); /* Zoom */
                        if (initPos==null) {
                            initPos = latLng;
                            points.add(initPos); /* Adding initPos to points*/
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {}

                @Override
                public void onProviderEnabled(String s) {}

                @Override
                public void onProviderDisabled(String s) {}
            });
        }

        Toast.makeText(getApplicationContext(), "Enter route, click start button when ready.", Toast.LENGTH_LONG).show();
        Log.d(TAG, "on create done");
    }

    @Override
    protected void onStop() {
        super.onStop();
        executor.shutdown(); /* Shutdown the threads that may be still running */ 
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
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                if (points.size()<maxNumOfPoints){
                    Log.d(TAG, "adding point to map");
                    points.add(latLng); /* Adding the marker to the markers arrayList */
                    MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.goodrunner)); /* Creating a marker */
                    markerOptions.position(latLng); /* Setting the position for the marker */
                    markerOptions.title(latLng.latitude + " : " + latLng.longitude); /* Setting the title for the marker. This will be displayed on taping the marker */
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng)); /* Animating to the touched position */
                    mMap.addMarker(markerOptions); /* Placing a marker on the touched position */
                }
            }
        });
    }

    /**
     * This function will be called when clicking on the "Map type" button, and change the map type from road map to sattelite.
     * @param  View view
     * @return void
     */
    public void changeMapType(View view) {
        if (mMap.getMapType()==GoogleMap.MAP_TYPE_NORMAL)
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        else
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    /**
     * This function will be called when clicking on the "start" button. It will make the unrelevant buttons invisible, and start the clock.
     * @param  View view
     * @return void
     */
    public void finishedRoute_Start(View v) {
        //startFlag = true;
        if (points.size() == 1) {
            Toast.makeText(getApplicationContext(), "Needs minimum 1 point", Toast.LENGTH_SHORT).show();
        } else {
            maxNumOfPoints = points.size();
            Log.d(TAG, "clicked view: " + v.getId());
            Button button = (Button) v;
            button.setVisibility(View.INVISIBLE);
            button.setVisibility(View.GONE);
            Toast toast = Toast.makeText(getApplicationContext(), "The clock has started, run!", Toast.LENGTH_SHORT);
            toast.show();
            final TextView time = (TextView) findViewById(R.id.time);
            final TextView distanceToNextPoint = (TextView) findViewById(R.id.dist);
            final TextView distTxt = (TextView) findViewById(R.id.distText);

            time.setVisibility(View.VISIBLE);
            distanceToNextPoint.setVisibility(View.VISIBLE);
            distTxt.setVisibility(View.VISIBLE);

            Runnable timeUpdate = new Runnable() {
                @Override
                public void run() {
                    try {
                        while (!Thread.currentThread().isInterrupted() && finishFlag == false) {
                            Thread.sleep(1000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("CALCULATION", "startedRunning");
                                    count++;
                                    String convertedTime = getFormattedTimeString(count);
                                    time.setText(convertedTime);
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };

            Runnable distanceUpdate = new Runnable() {
                @Override
                public void run() {
                    try {
                        while (!Thread.currentThread().isInterrupted() && finishFlag == false) {
                            Thread.sleep(1000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    handleNextPoint(distanceToNextPoint, distTxt);
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };

            executor.execute(timeUpdate);
            executor.execute(distanceUpdate);

        }
    }

    public void handleNextPoint(TextView distanceToNextPoint, TextView distTxt){
        LatLng nextPoint = points.get(currentIndex); /* Get the next checkpoint */
        int nextDistance = (int)getDistanceFrom(nextPoint.latitude, nextPoint.longitude); /* Caclculate the distance to it from the current place */
        String nextDistStr = Integer.toString(nextDistance);
        distanceToNextPoint.setText(nextDistStr); /* Show it on the textview */

        if (nextDistance < 30 && finishFlag == false) { /* If the nextDistance<30 - Next checkpoint was achieved. */
            Toast.makeText(getApplicationContext(), "CHECKPOINT REACHED!", Toast.LENGTH_SHORT).show();
            if (currentIndex < points.size() - 1) currentIndex++; /* Advance if possible  */

            else if (currentIndex == points.size() - 1 && finishFlag == false) { /* Finish operations*/
                finishFlag = true;
                distanceToNextPoint.setVisibility(View.INVISIBLE);
                distTxt.setVisibility(View.INVISIBLE);
                String convertedFinishTime = getFormattedTimeString(count + 1);
                Toast.makeText(getApplicationContext(), "Congratulations! Your time is: " + convertedFinishTime, Toast.LENGTH_LONG).show();
                writeResultToFile(convertedFinishTime);
                executor.shutdown(); /* Shutdown the threads in case of finishing the route successfully */ 
            }
        }
    }

    /**
     * This function Writes the name, age, finish time, points into db file as json.
     * @param  String convertedFinishTime - the finish time to record.
     * @return void
     */
    public void writeResultToFile(String convertedFinishTime){
        JSONObject result = new JSONObject();
        try {
            result.put("name", MainActivity.nameStr);
            result.put("age", MainActivity.ageStr);
            result.put("time", convertedFinishTime);
            Double[] lats = new Double[points.size()];
            Double[] longs = new Double[points.size()];
            for (int i=0; i<points.size(); i++)
                 lats[i]=points.get(i).latitude;
            for (int i=0; i<points.size(); i++)
                longs[i]=points.get(i).longitude;
            JSONArray latsJson = new JSONArray(Arrays.asList(lats));
            JSONArray longsJson = new JSONArray(Arrays.asList(longs));
            result.put("lats",latsJson);
            result.put("longs",longsJson);

            String parsed = result.toString();
            FileOutputStream streamer = openFileOutput("db.json", MODE_PRIVATE);
            streamer.write(parsed.getBytes());
            streamer.close();
            Toast.makeText(getApplicationContext(), "Saved record at internal storage :)", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error on saving result", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    /**
     * Calculates the distance in meters between current point location, and a given point (by lat and long)
     */
    public double getDistanceFrom(double lat2, double lng2) {
        double lat1 = currentPoint.latitude;
        double lng1 = currentPoint.longitude;
        int r = 6371; // average radius of the earth in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = r * c;
        return d*1000;
    }

    /**
     * Traslated seconds to actual time at string format
     */
    public String getFormattedTimeString(long timeInSeconds) {
        String timeStr = new String();
        long sec_term = 1;
        long min_term = 60 * sec_term;
        long hour_term = 60 * min_term;
        long result = Math.abs(timeInSeconds);
        int hour = (int) (result / hour_term);
        result = result % hour_term;
        int min = (int) (result / min_term);
        result = result % min_term;
        int sec = (int) (result / sec_term);
        if (timeInSeconds < 0)
            timeStr = "-";
        if (hour > 0)
            timeStr += hour + "h ";
        if (min > 0)
            timeStr += min + "m ";
        if (sec > 0)
            timeStr += sec + "s ";
        return timeStr;
    }
}

