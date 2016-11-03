package banrisul.ibm.com.banrisulmobileapp.api.client;

import android.app.Activity;
import android.content.pm.ActivityInfo;

import com.squareup.okhttp.OkHttpClient;

import banrisul.ibm.com.banrisulmobileapp.api.interfaceRest.API;
import banrisul.ibm.com.banrisulmobileapp.utils.ConstantUtils;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by renatosilva on 22/10/16.
 */

public class RestClient {


    private static API REST_CLIENT;
    private static Activity mActivity;


    static {
        setupRestClient();
    }

    private RestClient() {
    }

    public static API get(Activity activity) {
        if(mActivity != null){
            mActivity = activity;
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        }

        return REST_CLIENT;
    }

    public static void reset(){
        setupRestClient();
    }

    private static void setupRestClient() {

        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(ConstantUtils.APP_ROUTE_API)
                .setClient(new OkClient(new OkHttpClient()))
                .setLogLevel(RestAdapter.LogLevel.FULL);

        RestAdapter restAdapter = builder.build();
        REST_CLIENT = restAdapter.create(API.class);
    }
}