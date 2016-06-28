package mobile.system.geospot;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import mobile.system.geospot.prefUtil.AppPreferences;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private TextView textViewTitle;
    private TextView textViewDescription;
    private TextView textViewType;
    private ImageView imageViewLogo;

    private String title;
    private String description;
    private String type;
    private int r;
    private LatLng target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Like in main activity set ui and map
        textViewTitle = (TextView) findViewById(R.id.textView_title);
        textViewDescription = (TextView) findViewById(R.id.textView_description);
        textViewType = (TextView) findViewById(R.id.textView_type);
        imageViewLogo = (ImageView) findViewById(R.id.logo);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.setMyLocationEnabled(true);

        Intent i = getIntent();
        String listGeofences = i.getStringExtra(Constants.EXTRA_PENDINTENT_RESPONSE_SERVER_LIST);
        String idGeofence = i.getStringExtra(Constants.EXTRA_PENDINTENT_GEOFENCE_ID);

        JSONObject jsonSingle = AppPreferences.getInstance(getApplicationContext()).getSingleSpot(listGeofences,idGeofence);

        try {
            target = new LatLng(jsonSingle.getDouble("x"), jsonSingle.getDouble("y"));
            title = jsonSingle.getString("name");
            description = jsonSingle.getString("description");
            type = jsonSingle.getString("type");
            r = jsonSingle.getInt("r");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mMap.addMarker(
                new MarkerOptions()
                        .position(target)
                        .snippet(description)
                        .title(title)
        );

        int colorArea;
        if (type.equals("POI")) {
            colorArea = Color.argb(30, 238, 32, 32);
            imageViewLogo.setImageResource(R.drawable.poi);
            textViewType.setText("Luogo di interesse");
        } else {
            colorArea = Color.argb(90, 77, 184, 255);
            imageViewLogo.setImageResource(R.drawable.cart);
            textViewType.setText("Shopping");
        }

        CircleOptions circleOptions = new CircleOptions()
                .center(target)
                .strokeWidth(7)
                .fillColor(colorArea)
                .strokeColor(Color.argb(70, 255, 255, 255))
                .strokeWidth(3)
                .radius(r); // In meters

        // Get back the mutable Circle
        mMap.addCircle(circleOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(target, Constants.CAMERA_ZOOM);
        mMap.animateCamera(cameraUpdate);
        textViewTitle.setText(title);
        textViewDescription.setText(description);

    }
}

