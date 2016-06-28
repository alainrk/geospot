package mobile.system.geospot;

import android.content.Context;
import android.util.SparseIntArray;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.GeofenceStatusCodes;

/**
 * Created by giulio on 31/05/16.
 */
public final class Constants {

    public static final String BASE_SERVER_REQUEST_URI = "http://512b.it";
    public static final String RELATIVE_SERVER_REQUEST_URI = "/geospot/around.php";

    public static final String PREF_FILE = "GEOSPOTPREF";
    public static final String PREF_RESPONSE = "RESPONSE";
    public static final long DETECTION_INTERVAL_IN_MILLISECONDS = 0;

    public static final String PREF_ACTIVITY_TYPE = "PREF_ACTIVITY_TYPE";
    public static final String PREF_ACTIVITY_CONFIDENCE = "PREF_ACTIVITY_CONFIDENCE";
    public static final String PREF_LATITUDE = "PREF_LATITUDE";
    public static final String PREF_LONGITUDE = "PREF_LONGITUDE";

    public static final String PREF_LATITUDE_SENT = "PREF_LATITUDE_SENT";
    public static final String PREF_LONGITUDE_SENT = "PREF_LONGITUDE_SENT";

    public static final String BCAST_UPDATE_ACTIVITY_RECOGNITION = "BCAST_UPDATE_ACTIVITY_RECOGNITION";
    public static final String BCAST_UPDATE_SERVER_REQUEST = "BCAST_UPDATE_SERVER_REQUEST";
    public static final String BCAST_UPDATE_LOCATION = "BCAST_UPDATE_LOCATION";

    // Internally mainIntenctService to understand what to do in onConnected
    public static final String PREF_ONCONNECTED_ACTRECOGNITION = "PREF_ONCONNECTED_ACTRECOGNITION";
    public static final String PREF_ONCONNECTED_GEOFENCES = "PREF_ONCONNECTED_GEOFENCES";
    public static final String PREF_IMEI_USER = "PREF_IMEI_USER";

    public static final String ACTION_ACTIVITY_RECOGNITION = "ACTION_ACTIVITY_RECOGNITION";
    public static final String ACTION_TIMEOUT_UPDATE_ALL = "ACTION_TIMEOUT_UPDATE_ALL";
    public static final String ACTION_START_LOCATION_LISTENER = "ACTION_START_LOCATION_LISTENER";
    public static final String ACTION_START_MAIN_SERVICE = "ACTION_START_MAIN_SERVICE";

    public static final String ACTION_ARRIVED_GEOFENCES = "ACTION_ARRIVED_GEOFENCES";
    public static final String ACTION_GEOFENCE_NOTIFICATION = "ACTION_GEOFENCE_NOTIFICATION" ;

    public static final String EXTRA_PENDINTENT_GEOFENCE_ID = "EXTRA_PENDINTENT_GEOFENCE_ID";
    public static final String EXTRA_PENDINTENT_RESPONSE_SERVER_LIST = "EXTRA_PENDINTENT_RESPONSE_SERVER_LIST";


    public static final String FIRST_REQUEST = "FIRST_REQUEST";

    /**
     * List of DetectedActivity types that we monitor in this sample.
     */
    protected static final int[] MONITORED_ACTIVITIES = {
            DetectedActivity.STILL,
            DetectedActivity.WALKING,
            DetectedActivity.ON_FOOT,
            DetectedActivity.ON_BICYCLE,
            DetectedActivity.IN_VEHICLE,
            DetectedActivity.RUNNING
    };

    // Main Activity wake up based on activity alarm
    public static final SparseIntArray TIMEOUT_BY_ACTIVITYRECOGNITION = new SparseIntArray() {{
        put(DetectedActivity.STILL, 1800);
        put(DetectedActivity.WALKING, 60);
        put(DetectedActivity.ON_FOOT, 60);
        put(DetectedActivity.ON_BICYCLE, 30);
        put(DetectedActivity.IN_VEHICLE, 15);
        put(DetectedActivity.RUNNING, 45);
    }};

    // Get MIN radius for new update based on activity
    public static float getMinRadiusByActivity (int activityType) {
        switch(activityType) {
            case DetectedActivity.IN_VEHICLE:
                return 500;
            case DetectedActivity.ON_BICYCLE:
                return 250;
            case DetectedActivity.ON_FOOT:
                return 100;
            case DetectedActivity.RUNNING:
                return 100;
            case DetectedActivity.STILL:
                return 50;
            case DetectedActivity.WALKING:
                return 100;
            default:
                return 100;
        }
    }

    public static boolean isAMonitoredActivity(int a) {
        for(int i: MONITORED_ACTIVITIES){
            if(i == a)
                return true;
        }
        return false;
    }

    /**
     * Returns a human readable String corresponding to a detected activity type.
     */
    public static String getActivityString(int detectedActivityType) {
        switch(detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return "VEHICLE";
            case DetectedActivity.ON_BICYCLE:
                return "BICYCLE";
            case DetectedActivity.ON_FOOT:
                return "FOOT";
            case DetectedActivity.RUNNING:
                return "RUNNING";
            case DetectedActivity.STILL:
                return "STILL";
            case DetectedActivity.TILTING:
                return "TILTING";
            case DetectedActivity.UNKNOWN:
                return "UNKNOWN";
            case DetectedActivity.WALKING:
                return "WALKING";
            default:
                return "NOT RECOGNIZED";
        }
    }
    public static String getErrorString(Context context, int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GEOFENCE NOT AVAILABLE";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "TOO MANY GEOFENCE";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "TOO MANY PENDING INTENT";
            default:
                return "UNKNOWN ERROR";
        }
    }


    final static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 90;

    final static int CAMERA_ZOOM = 15;

    final static int INTERVAL_LOCATION_UPDATE = 20000;
    final static int DEFAULT_TIMEOUT_MAINSERVICE_UPDATE = 20000;

    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 1;
    public static final int GEOFENCE_LOITERING_IN_MILLISECONDS = 3000;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
}
