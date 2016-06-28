package mobile.system.geospot;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;


import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

import mobile.system.geospot.prefUtil.AppPreferences;

public class DetectedActivitiesIntentService extends IntentService {

    protected static final String TAG = "Activity DETECTED: ";

    public DetectedActivitiesIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int maxConf = -1;
        int maxAct = -1;

        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level, which is an int in [0-100]
        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

        for (DetectedActivity da: detectedActivities) {
            if (Constants.isAMonitoredActivity(da.getType()) && maxConf < da.getConfidence()) {
                maxConf = da.getConfidence();
                maxAct = da.getType();
            }

            if (!Constants.isAMonitoredActivity(da.getType())) {
                Log.i("TIPO NON IN LISTA: ", Constants.getActivityString(da.getType()));
            }
        }

        if (maxAct != -1) {
            Log.i(TAG, Constants.getActivityString(maxAct) + " " + maxConf + "%" );

            if (maxAct == AppPreferences.getInstance(getApplicationContext()).readLastActivityRec())
                return;

            AppPreferences.getInstance(getApplicationContext()).writeLastActivityRec(maxAct,maxConf);

            Intent mainintent = new Intent(getApplicationContext(), MainIntentService.class);
            mainintent.setAction(Constants.ACTION_ACTIVITY_RECOGNITION);
            getApplicationContext().startService(mainintent);
        }

    }

}
