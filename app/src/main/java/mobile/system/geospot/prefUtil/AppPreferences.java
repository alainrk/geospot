package mobile.system.geospot.prefUtil;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mobile.system.geospot.Constants;

/**
 * Created by giulio on 18/06/16.
 */
public class AppPreferences {

    private static AppPreferences sInstance;
    private static Context sContext;
    private SharedPreferences mPreferences;

    private AppPreferences(Context context) {
        sContext = context;
        initPreferences();
    }

    public static synchronized AppPreferences getInstance(Context context) {
        if(sInstance == null) {
            sInstance = new AppPreferences(context.getApplicationContext());
        }
        return sInstance;
    }

    private void initPreferences() {
        mPreferences = sContext.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE);
    }

    public JSONArray readLastSpotList() throws JSONException {
        String prefResponse = mPreferences.getString(Constants.PREF_RESPONSE, "");
        return new JSONArray(prefResponse);
    }

    public String readLastSpotListToString(){
        return mPreferences.getString(Constants.PREF_RESPONSE, "");
    }

    public void writeLastSpotList(String list){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(Constants.PREF_RESPONSE, list);
        editor.commit();
    }

    public JSONObject getSingleSpot(String prefResponse, String idString) {

        int id = Integer.parseInt(idString);

        if (!prefResponse.equals("")) {
            JSONArray data = null;
            try {
                data = new JSONArray(prefResponse);
                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonSingle = data.getJSONObject(i);
                    if (jsonSingle.getInt("id") == id) {
                        return jsonSingle;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void writeLastActivityRec(int maxAct, int maxConf){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(Constants.PREF_ACTIVITY_TYPE, maxAct);
        editor.putInt(Constants.PREF_ACTIVITY_CONFIDENCE, maxConf);
        editor.commit();
    }

    public int readLastActivityRec(){
        return mPreferences.getInt(Constants.PREF_ACTIVITY_TYPE, -1);
    }

    public void writeLastPosition(double lat, double lon){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(Constants.PREF_LATITUDE, String.valueOf(lat));
        editor.putString(Constants.PREF_LONGITUDE,String.valueOf(lon));
        editor.commit();
    }

    public void saveLastPositionSent(){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(Constants.PREF_LATITUDE_SENT, String.valueOf(mPreferences.getString(Constants.PREF_LATITUDE, "-1")));
        editor.putString(Constants.PREF_LONGITUDE_SENT,String.valueOf(mPreferences.getString(Constants.PREF_LONGITUDE, "-1")));
        editor.commit();
    }

    public double readLastLatitudeSent(){
        return Double.parseDouble(mPreferences.getString(Constants.PREF_LATITUDE_SENT, "-1"));
    }

    public double readLastLongitudeSent(){
        return Double.parseDouble(mPreferences.getString(Constants.PREF_LONGITUDE_SENT, "-1"));
    }

    public double readLastLatitude(){
        return Double.parseDouble(mPreferences.getString(Constants.PREF_LATITUDE, "-1"));
    }

    public double readLastLongitude(){
        return Double.parseDouble(mPreferences.getString(Constants.PREF_LONGITUDE, "-1"));
    }

    public void writeOnConnectedActRecognition(int val) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(Constants.PREF_ONCONNECTED_ACTRECOGNITION, val);
        editor.commit();
    }

    public int readOnConnectedActRecognition(){
        return mPreferences.getInt(Constants.PREF_ONCONNECTED_ACTRECOGNITION, 0);
    }

    public void writeOnConnectedGeoFence(int val) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(Constants.PREF_ONCONNECTED_GEOFENCES, val);
        editor.commit();
    }

    public int readOnConnectedGeoFence(){
        return mPreferences.getInt(Constants.PREF_ONCONNECTED_GEOFENCES, 0);
    }

    public void generateUniqueIDSession(){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(Constants.PREF_IMEI_USER, Double.toString(Math.random()));
        editor.commit();
    }

    public String readIMEIUser(){
        return mPreferences.getString(Constants.PREF_IMEI_USER,"USER_UNKNOWN");
    }

    public void setFirstRequest(boolean on) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(Constants.FIRST_REQUEST, on);
        editor.commit();
    }

    public boolean isFirstRequest() {
        return mPreferences.getBoolean(Constants.FIRST_REQUEST, true);
    }

    public void clear() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
