package mobile.system.geospot;

/**
 * Created by giulio on 04/06/16.
 */

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import mobile.system.geospot.prefUtil.AppPreferences;

public class GeofenceTransitionsIntentService extends IntentService {

    protected static final String TAG = "GeofenceTransitionsIS";

    public GeofenceTransitionsIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String listGeofences = intent.getStringExtra(Constants.EXTRA_PENDINTENT_RESPONSE_SERVER_LIST);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = Constants.getErrorString(this,
                    geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL || geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            for(Geofence geofence: triggeringGeofences){
                String id = geofence.getRequestId();
                sendNotification(listGeofences, id);
            }

        } else {
            // Log the error.
            Log.e(TAG, "Error in geofence transition intent service");
        }
    }


    private void sendNotification(String listGeofences, String idGeofence) {
        String title;
        String description;

        JSONObject spot = AppPreferences.getInstance(getApplicationContext()).getSingleSpot(listGeofences,idGeofence);

        try {
            title = spot.getString("name");
            description = spot.getString("description");
        } catch (JSONException e) {
            title = "Spot Not Found";
            description = "Please pray";
            e.printStackTrace();
        }
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), DetailActivity.class);
        notificationIntent.setAction(Constants.ACTION_GEOFENCE_NOTIFICATION+idGeofence);
        notificationIntent.putExtra(Constants.EXTRA_PENDINTENT_RESPONSE_SERVER_LIST, listGeofences);
        notificationIntent.putExtra(Constants.EXTRA_PENDINTENT_GEOFENCE_ID, idGeofence);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MapsActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntentWithParentStack(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.

        builder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.cart))
                .setColor(Color.RED)
                .setContentTitle(title)
                .setContentText(description)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(Integer.parseInt(idGeofence), builder.build());
    }
}