package banrisul.ibm.com.banrisulmobileapp.api.interfaceRest;

import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by renatosilva on 22/10/16.
 */

public interface API {


    /******************************************* API  ********************************************/


    @GET("/agencias")
    void getAgenciasBarinsul(@Query("lat") String lat, @Query("long") String longi, Callback<JsonObject> callback);
}