package mobile.system.geospot.reqUtil;

import mobile.system.geospot.Constants;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by giulio on 30/05/16.
 */

// Default interface of retrofit lib to do requests
public interface geoSpotRequest {

    // Method name and parameters getData of Web request

    // Decorator to set relative path to unify with base uri
    @GET(Constants.RELATIVE_SERVER_REQUEST_URI)
    Call<ResponseBody> getData(
            @Query("posx") double posx,
            @Query("posy") double posy,
            @Query("dir") int dir,
            @Query("pattern_move") int pattern_move,
            @Query("uid") String uid
            );

}
