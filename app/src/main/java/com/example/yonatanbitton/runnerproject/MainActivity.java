package com.example.yonatanbitton.runnerproject;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    public static String nameStr;
    public static String ageStr;
    public ArrayList<LatLng> points = new ArrayList<LatLng>();
    double[] lats;
    double[] longs;
    /**
     * On create: preview the layout.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    /**
     * This function is called when clicking on the next button. Toasts relevant info and activates the map activity.
     * @param  View view
     * @return void
     */
    public void nextButtonClick(View view) {
        EditText nameTxt = (EditText)findViewById(R.id.nameTxt);
        EditText ageTxt = (EditText)findViewById(R.id.ageTxt);
        nameStr = nameTxt.getText().toString();
        ageStr = ageTxt.getText().toString();
        Intent getDirectedSearchScreenIntent = new Intent(this,MapsActivity.class);
        startActivity(getDirectedSearchScreenIntent);
    }

    /**
     * This function is called when clicking on the Previous Run button. Toasts relevant info and activates the previous map activity, with the rout's markers.
     * @param  view  an absolute URL giving the base location of the image
     * @return void
     */
    public void showPreviousResult(View view){
        Toast.makeText(getApplicationContext(), "Location previous result...", Toast.LENGTH_SHORT).show();
        try {
            String path=getFilesDir()+"/db.json"; /* Working directory of the android device */
            InputStream inputstream = new FileInputStream(path); /* Construct file input-stream for the JsonReader*/
            JsonReader reader = new JsonReader(new InputStreamReader(inputstream, "UTF-8"));
            try {
                List<String> info = readMessage(reader); /* Gets the primitive information, and initializes the points arraylist */
                Toast.makeText(getApplicationContext(), "info: name: "+info.get(0)+" ,age: "+info.get(1)+" ,time: "+info.get(2), Toast.LENGTH_SHORT).show();
            } finally {
                reader.close();
            }
        } catch (IOException e) { e.printStackTrace(); };

        lats = new double[points.size()];
        longs = new double[points.size()];
        for (int i=0; i<lats.length; i++)
            lats[i]=points.get(i).latitude;
        for (int i=0; i<longs.length; i++)
            longs[i]=points.get(i).longitude;

        Intent getDirectedSearchScreenIntent = new Intent(this,ShowLastRouteActivity.class);
        getDirectedSearchScreenIntent.putExtra("lats", lats); /* Transfer points to next activity*/
        getDirectedSearchScreenIntent.putExtra("longs", longs);
        startActivity(getDirectedSearchScreenIntent); /* Activates next activity - which shows the last route on screen*/
    }

    /**
     * Function to read the message which is parsed as JSON in the internal storage.
     * @param  JsonReader reader in order to read from the internal storage of the device (path known).
     * @return List<String> of the relevant primitive information
     */
    public List<String> readMessage(JsonReader reader) throws IOException {
        List<String> result = new ArrayList<String>(); /* result accumulator */
        List<Double> lats = new ArrayList<Double>();
        List<Double> longs = new ArrayList<Double>();
        String nameStr = null, ageStr = null, timeStr = null;
        reader.beginObject(); /* Begin reading the JSON object */
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("name")) {
                nameStr = reader.nextString();
                result.add(nameStr);
            } else if (name.equals("age")) {
                ageStr = reader.nextString();
                result.add(ageStr);
            } else if (name.equals("time")) {
                timeStr = reader.nextString();
                result.add(timeStr);
            } else if (name.equals("lats") && reader.peek() != JsonToken.NULL) {
                lats = readDoublesArray(reader);
            } else if (name.equals("longs") && reader.peek() != JsonToken.NULL) {
                longs = readDoublesArray(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject(); /* Finished the JSON object reading */
        for (int i=0; i<lats.size(); i++){
            LatLng p = new LatLng(lats.get(i),longs.get(i));
            points.add(p);
        }

        return result;
    }


    /**
     * Function to read JSONArray object, and return it value as list of doubles.
     * @param  JsonReader reader in order to read from the internal storage of the device (path known).
     * @return List<String> of the longitute/latitude strings
     */
    public List<Double> readDoublesArray(JsonReader reader) throws IOException {
        List<Double> doubles = new ArrayList<Double>();
        Log.d("READ", "start function readDoubleArray");
        reader.beginArray();
        String bool="false";
        if (reader.hasNext()) bool = "true";
        Log.d("READ", "reader.hasNext(): "+ bool);
        while (reader.hasNext()) {
            Log.d("READ", "inside hasNext");
            doubles.add(reader.nextDouble());
        }
        reader.endArray();
        return doubles;
    }

    public ArrayList<LatLng> getPoints(){
        return points;
    }
}

