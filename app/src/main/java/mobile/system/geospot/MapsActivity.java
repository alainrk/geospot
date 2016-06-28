package mobile.system.geospot;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import mobile.system.geospot.prefUtil.AppPreferences;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    int locationUpdateCounter = 0;
    int cameraZoom = 16;

    private GoogleMap mMap;
    private ListView listView;
    private TextView textView;

    int prefActivityRec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Receiving all local broadcast
        setBroadCastReceiver();

        // Map fragment and UI main activity creation
        listView = (ListView) findViewById(R.id.listView_GS);
        textView = (TextView) findViewById(R.id.textView_GS);

        updateMovementUI();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }

        mMap.setMyLocationEnabled(true);

        setLocationService();
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    private void requestLocationPermission() {
        // Request permission for Android 6 with its dialog
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                       //Check again beacause of android studio stupidity
                        return;
                    }
                    setLocationService();
                    mMap.setMyLocationEnabled(true);
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void updateMovementUI() {
        final AppPreferences preferences = AppPreferences.getInstance(getApplicationContext());

        // User activity recognition
        prefActivityRec = preferences.readLastActivityRec();
        textView.setText("Attività: "+Constants.getActivityString(prefActivityRec));
    }

    private void updateUI() {
        int maxRaggio = 0;

        final AppPreferences preferences = AppPreferences.getInstance(getApplicationContext());

        // User position
        double latitude = preferences.readLastLatitude();
        double longitude = preferences.readLastLongitude();

        mMap.clear();

        ArrayList<PoiAdv> PoiAdvs = new ArrayList<>();
        final ArrayList<String> idGeofenceList = new ArrayList<>();

        try {
            JSONArray data = preferences.readLastSpotList();
            if (data.length() == 0)
                return;

            for (int i = 0; i < data.length(); i++) {
                JSONObject jsonSingle = data.getJSONObject(i);
                LatLng target = new LatLng(jsonSingle.getDouble("x"), jsonSingle.getDouble("y"));
                idGeofenceList.add(Integer.toString(jsonSingle.getInt("id")));

                mMap.addMarker(
                        new MarkerOptions()
                                .position(target)
                        .snippet(jsonSingle.getString("description"))
                        .title(jsonSingle.getString("name"))
                );

                int colorArea;
                if (jsonSingle.getString("type").equals("POI")){
                    colorArea=Color.argb(30, 238, 32, 32);
                } else {
                    colorArea = Color.argb(90, 77, 184, 255);
                }

                CircleOptions circleOptions = new CircleOptions()
                        .center(target)
                        .strokeWidth(7)
                        .fillColor(colorArea)
                        .strokeColor(Color.argb(70, 255, 255, 255))
                        .strokeWidth(3)
                        .radius(jsonSingle.getInt("r")); // In meters
                if(jsonSingle.getInt("r")>maxRaggio)
                    maxRaggio = jsonSingle.getInt("r");

                // Get back the mutable Circle
                mMap.addCircle(circleOptions);

                boolean poiImage = (jsonSingle.getString("type").equals("POI"));
                PoiAdvs.add(new PoiAdv(jsonSingle.getString("name"),jsonSingle.getString("description"),poiImage));

            }
        } catch (JSONException e) {

            e.printStackTrace();
        }

        // Adapter for list of spot
        AdapterListview adapterListview = new AdapterListview(getApplicationContext(),R.layout.row,PoiAdvs);

        // Cleaning list
        listView.setAdapter(null);
        listView.setAdapter(adapterListview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("position", position+"");

                Intent i = new Intent(getApplicationContext(),DetailActivity.class);
                i.putExtra(Constants.EXTRA_PENDINTENT_RESPONSE_SERVER_LIST, preferences.readLastSpotListToString());
                i.putExtra(Constants.EXTRA_PENDINTENT_GEOFENCE_ID, idGeofenceList.get(position));
                startActivity(i);
            }
        });

        LatLng target = new LatLng(latitude, longitude);

        if (maxRaggio >= 100){
            cameraZoom = 15;
        }
        if (maxRaggio >= 500){
            cameraZoom = 14;
        }

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(target, cameraZoom);
        mMap.animateCamera(cameraUpdate);
    }

    private void setBroadCastReceiver(){

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.BCAST_UPDATE_SERVER_REQUEST)) {
                    updateUI();
                }
                if (intent.getAction().equals(Constants.BCAST_UPDATE_LOCATION) && locationUpdateCounter<1){
                    Intent mainIntent = new Intent(getApplicationContext(), MainIntentService.class);
                    mainIntent.setAction(Constants.ACTION_START_MAIN_SERVICE);
                    getApplicationContext().startService(mainIntent);
                    locationUpdateCounter += 1;
                }
                if (intent.getAction().equals(Constants.BCAST_UPDATE_ACTIVITY_RECOGNITION)){
                    updateMovementUI();
                }
            }
        };

        IntentFilter actIntFilter = new IntentFilter(Constants.BCAST_UPDATE_ACTIVITY_RECOGNITION);
        IntentFilter requestServerFilter = new IntentFilter(Constants.BCAST_UPDATE_SERVER_REQUEST);
        IntentFilter locationServerFilter = new IntentFilter(Constants.BCAST_UPDATE_LOCATION);

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, actIntFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, requestServerFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, locationServerFilter);
    }

    private void setLocationService() {
        Intent locIntent = new Intent(getApplicationContext(), LocationChangeService.class);
        locIntent.setAction(Constants.ACTION_START_LOCATION_LISTENER);
        getApplicationContext().startService(locIntent);
    }

}