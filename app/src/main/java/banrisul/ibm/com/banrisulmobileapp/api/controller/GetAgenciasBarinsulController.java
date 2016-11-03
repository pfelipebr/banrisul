package banrisul.ibm.com.banrisulmobileapp.api.controller;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import banrisul.ibm.com.banrisulmobileapp.api.client.RestClient;
import banrisul.ibm.com.banrisulmobileapp.database.DbBarinsul;
import banrisul.ibm.com.banrisulmobileapp.maps.MapsActivity;
import banrisul.ibm.com.banrisulmobileapp.utils.BanrisulUtils;
import banrisul.ibm.com.banrisulmobileapp.utils.ConstantUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by renatosilva on 22/10/16.
 */

public class GetAgenciasBarinsulController {


    private JSONArray mJSONDetail = null;
    private JSONObject detailDay = null;
    private JSONArray mJsonArrayResumoDia = null;
    private ProgressDialog progressDialog;

    public void getAgenciasBarinsul(final AppCompatActivity mAppCompatActivity, final String latitude,  final String longitude) {


     /* Show ProgressDialog for user */
        progressDialog = BanrisulUtils.createProgressDialog(mAppCompatActivity);
        progressDialog.show();


        //PROD
        RestClient.get(mAppCompatActivity).getAgenciasBarinsul( latitude, longitude , new Callback<JsonObject>() {

            @Override
            public void success(JsonObject jsonObject, Response response) {

                mAppCompatActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);

                try {


                    String myResultJsonApi = jsonObject.toString();

                    if(myResultJsonApi != null){


                            /*Pegando o objeto Json que vem da API*/
                            JSONObject resumoDia = new JSONObject(myResultJsonApi);
                            JSONArray  myObjJson  =  resumoDia.getJSONArray("rows");

                            if(myObjJson.length() > 0){

                                     mJsonArrayResumoDia = new JSONArray();

                                    for (int i = 0; i < myObjJson.length(); i++) {

                                        JSONArray mJsonObject = myObjJson.getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates");

                                        mJsonArrayResumoDia.put(mJsonObject);
                                    }

                                /*Gravando o Json no file no android*/
                                    DbBarinsul.mCreateAndSaveFile(mAppCompatActivity, ConstantUtils.FILE_AGENCIAS_BANRISUL, mJsonArrayResumoDia.toString());

                                    mAppCompatActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                                }
                    }

                } catch (JSONException e) {

                    //progressDialog.dismiss();
                    e.printStackTrace();
                } finally {
                    MapsActivity.getScreenData(mAppCompatActivity, mJsonArrayResumoDia, progressDialog,  latitude,  longitude);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                MapsActivity.getScreenData(mAppCompatActivity, mJsonArrayResumoDia, progressDialog, latitude,  longitude);
            }

        });



    }

}
