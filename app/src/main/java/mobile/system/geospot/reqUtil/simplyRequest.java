package mobile.system.geospot.reqUtil;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;

import mobile.system.geospot.Constants;
import mobile.system.geospot.MainIntentService;
import mobile.system.geospot.prefUtil.AppPreferences;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by giulio on 30/05/16.
 */

/**
 * Classe singleton per effettuare la richiesta server
 */
public class simplyRequest {

    public static void makeRequest(double latitude, double longitude, int dir, int pattern_move, String uid, final Context context){

        // Retrofit object to create Base URI to attach with geoSpotRequest returned relative part
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_SERVER_REQUEST_URI)
                .build();

        geoSpotRequest locoReq = retrofit.create(geoSpotRequest.class);

        // Retrofit call with parameters that triggers callback onResponse
        Call<ResponseBody> resp = locoReq.getData(latitude, longitude, dir, pattern_move, uid);
        // Asynchronous callback for request
        resp.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                try {
                    // Take response string and put it in shared preferences
                    String response = r.body().string();
                    Log.d("response", response);

                    AppPreferences.getInstance(context).writeLastSpotList(response);

                    // Broadcast for UI update
                    Intent intent = new Intent(Constants.BCAST_UPDATE_SERVER_REQUEST);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                    // Intent for main intent service to advertise for new spots
                    Intent geoFencesIntent = new Intent(context, MainIntentService.class);
                    geoFencesIntent.setAction(Constants.ACTION_ARRIVED_GEOFENCES);
                    context.startService(geoFencesIntent);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Simply request", "Error");
            }
        });
    }
}
