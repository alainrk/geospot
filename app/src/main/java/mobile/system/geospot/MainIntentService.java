package mobile.system.geospot;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mobile.system.geospot.prefUtil.AppPreferences;
import mobile.system.geospot.reqUtil.simplyRequest;

public class MainIntentService extends IntentService
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private GoogleApiClient mGoogleApiClient;

    public MainIntentService() {
        super("MainIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (Constants.ACTION_START_MAIN_SERVICE.equals(action)){
                AppPreferences preferences = AppPreferences.getInstance(getApplicationContext());
                preferences.generateUniqueIDSession();
                preferences.setFirstRequest(true);

                buildGoogleApiClient();
                AppPreferences.getInstance(getApplicationContext()).writeOnConnectedActRecognition(1);
                mGoogleApiClient.connect();
                handleTimeoutUpdateAll();
            }
            else if (Constants.ACTION_ACTIVITY_RECOGNITION.equals(action)) {
                handleActivityRecognition();
            }
            else if (Constants.ACTION_TIMEOUT_UPDATE_ALL.equals(action)) {
                handleTimeoutUpdateAll();
            }
            else if (Constants.ACTION_ARRIVED_GEOFENCES.equals(action)) {
                buildGoogleApiClient();
                AppPreferences.getInstance(getApplicationContext()).writeOnConnectedGeoFence(1);
                mGoogleApiClient.connect();
            }
        }
    }

    private void handleActivityRecognition() {
        makeServerRequest();

        // Update view new activity recognized
        Intent intent = new Intent(Constants.BCAST_UPDATE_ACTIVITY_RECOGNITION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void handleTimeoutUpdateAll() {

        AppPreferences preferences = AppPreferences.getInstance(getApplicationContext());

        if (!nearbyToPreviousLastPosition() || preferences.isFirstRequest())
            makeServerRequest();

        if (preferences.isFirstRequest())
            preferences.setFirstRequest(false);

        // Set the alarms for next sensing of amplitude
        alarmMgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        Intent intentAlarm = new Intent(getApplicationContext(), MainIntentService.class);
        intentAlarm.setAction(Constants.ACTION_TIMEOUT_UPDATE_ALL);
        alarmIntent = PendingIntent.getService(getApplicationContext(), 0, intentAlarm, 0);

        try {
            // Remove the oldest one if exists
            alarmMgr.cancel(alarmIntent);
        } catch (Exception e) {
            Log.d("MainintentService", "Cancel pending intent error");
        }

        int typeActivity = AppPreferences.getInstance(getApplicationContext()).readLastActivityRec();

        // Set next check based on activity recognized
        int seconds = (typeActivity != -1) ? Constants.TIMEOUT_BY_ACTIVITYRECOGNITION.get(typeActivity) : Constants.DEFAULT_TIMEOUT_MAINSERVICE_UPDATE;
        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() +
                        seconds * 1000, alarmIntent);
    }

    private boolean nearbyToPreviousLastPosition() {
        AppPreferences preferences = AppPreferences.getInstance(getApplicationContext());

        Location prevloc = new Location("prev");
        prevloc.setLatitude(preferences.readLastLatitudeSent());
        prevloc.setLongitude(preferences.readLastLongitudeSent());

        Location currloc = new Location("curr");
        currloc.setLatitude(preferences.readLastLatitude());
        currloc.setLongitude(preferences.readLastLongitude());

        return (prevloc.distanceTo(currloc) < Constants.getMinRadiusByActivity(preferences.readLastActivityRec()));
    }

    private void makeServerRequest() {
        AppPreferences preferences = AppPreferences.getInstance(getApplicationContext());

        double latitude = preferences.readLastLatitude();
        double longitude = preferences.readLastLongitude();
        int dir = 0; // Not used yet
        int pattern_move = preferences.readLastActivityRec();

        String uid = preferences.readIMEIUser();

        // Make request and save data in shared preferences
        simplyRequest.makeRequest(latitude, longitude, dir, pattern_move, uid, this);
    }


    public void onResult(Status status) {
        if (status.isSuccess()) {
            Log.e("MainIntentService","Success");
        } else {
            Log.e("", "Error adding or removing activity detection: " + status.getStatusMessage());
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (!mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
            return;
        }
        AppPreferences preferences = AppPreferences.getInstance(getApplicationContext());

        int isForActivityRecogn = preferences.readOnConnectedActRecognition();
        int isForGeofences = preferences.readOnConnectedGeoFence();

        if (isForActivityRecogn == 1) {

            preferences.writeOnConnectedActRecognition(0);

            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                    mGoogleApiClient,
                    Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                    getActivityDetectionPendingIntent()
            ).setResultCallback(this);
        }

        if (isForGeofences == 1) {
            preferences.writeOnConnectedGeoFence(0);
            // Save position sent to avoid after repeat request if nearby
            preferences.saveLastPositionSent();
            populateGeofenceList();
        }
    }

    public void populateGeofenceList() {

        GeofencingRequest.Builder builder;
        ArrayList<Geofence> mGeofenceList = new ArrayList<>();

        try {
            JSONArray geofArray = AppPreferences.getInstance(getApplicationContext()).readLastSpotList();
            if (geofArray.length() == 0)
                return;

            for (int i=0; i<geofArray.length(); i++){

                JSONObject geofence = geofArray.getJSONObject(i);

                int id = geofence.getInt("id");
                LatLng target = new LatLng(geofence.getDouble("x"), geofence.getDouble("y"));
                Float radius = (float) geofence.getDouble("r");

                mGeofenceList.add(
                        new Geofence.Builder()
                        .setRequestId(String.valueOf(id))
                        .setCircularRegion(
                                target.latitude,
                                target.longitude,
                                radius
                        )
                        .setLoiteringDelay(Constants.GEOFENCE_LOITERING_IN_MILLISECONDS) // How much time after DWELLING inside zone will trigger
                        .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL|Geofence.GEOFENCE_TRANSITION_EXIT|Geofence.GEOFENCE_TRANSITION_ENTER)
                        .build()
                );
             }

        } catch (JSONException e) {
            Log.e("Mainintentservice", e.toString());
        }

        builder = new GeofencingRequest.Builder();

        builder.setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER);
        builder.addGeofences(mGeofenceList);
        GeofencingRequest geofencingRequest = builder.build();

        try {
            LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient,getGeofencePendingIntent());

            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    geofencingRequest,
                    getGeofencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException e) {
            Log.d("MainIntentService_M", e.toString());
            return;
        }

    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        intent.putExtra(Constants.EXTRA_PENDINTENT_RESPONSE_SERVER_LIST,AppPreferences.getInstance(getApplicationContext()).readLastSpotListToString());
        return PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getActivityDetectionPendingIntent() {
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        Intent intent = new Intent(this, DetectedActivitiesIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i("", "Connection suspended");
        mGoogleApiClient.connect();
    }
}
