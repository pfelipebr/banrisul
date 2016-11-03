package banrisul.ibm.com.banrisulmobileapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.ibm.mobile.services.core.IBMBluemix;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPush;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushException;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationListener;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushResponseListener;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPSimplePushNotification;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import banrisul.ibm.com.banrisulmobileapp.utils.BlueMixCreateNotificationRouter;
import banrisul.ibm.com.banrisulmobileapp.utils.ConstantUtils;

/**
 * Created by renatosilva on 01/11/16.
 */

public class BarinsulAplication extends Application {


    public static Context mContext;

    /* Constants About IBM BLUEMIX PUSH- PUSH*/
    private static final String CLASS_NAME = BarinsulAplication.class.getSimpleName();


    private Activity mActivity;
    private MFPPush push = null;
    private final MFPPushNotificationListener notificationListener = new MFPPushNotificationListener() {
        @Override
        public void onReceive(MFPSimplePushNotification resultNotification) {

            BlueMixCreateNotificationRouter.reciverBlueMixCreateNotificationRouter(getBaseContext(), resultNotification);
        }
    };


    public BarinsulAplication() {

            /*Callbacks de todas as Activites da Aplicacao.*/
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Log.d(CLASS_NAME, "Activity created: " + activity.getLocalClassName());
                mActivity = activity;

                if (push != null) {
                    push.listen(notificationListener);
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
                mActivity = activity;
                Log.d(CLASS_NAME, "Activity started: " + activity.getLocalClassName());
            }

            @Override
            public void onActivityResumed(Activity activity) {
                mActivity = activity;

                if (push != null) {
                    push.listen(notificationListener);
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.d(CLASS_NAME, "Activity paused: " + activity.getLocalClassName());
                if (activity != null && activity.equals(mActivity)) {

                    mActivity = null;
                }
            }

            @Override
            public void onActivityDestroyed(Activity activity) {}

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

            @Override
            public void onActivityStopped(Activity activity) {}
        });
    }


    @Override
    public void onCreate() {
        super.onCreate();

        Properties props = new Properties();
        mContext = getApplicationContext();

        try {

                    AssetManager assetManager = mContext.getAssets();
                    props.load(assetManager.open(ConstantUtils.PROPS_FILE));


                    // Initialize the IBM core backend-as-a-service.
                    IBMBluemix.initialize(this, props.getProperty(ConstantUtils.APP_ID), props.getProperty(ConstantUtils.APP_SECRET), props.getProperty(ConstantUtils.APP_ROUTE));


                        BMSClient.getInstance().initialize(getApplicationContext(), BMSClient.REGION_US_SOUTH);

                        push = MFPPush.getInstance();
                        push.initialize(getApplicationContext(), props.getProperty(ConstantUtils.APP_ID), props.getProperty(ConstantUtils.CLIENTE_SECRET));
                        push.registerDevice(new MFPPushResponseListener<String>() {
                            @Override
                            public void onSuccess(String deviceId) {
                                //handle success here
                                Log.d("onSuccess", "Registered device: " + deviceId);

                                String[] pushData = deviceId.split(" ");


                                try {
                                    JSONObject jsonPush = new JSONObject(pushData[4].toString());

                                    deviceId = jsonPush.getString("deviceId");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                ///PEGANDO A KEY PARA ENVIO DE PUSH NOTIFICATION BLUEMIX//
                                ConstantUtils.DEVICE_ID_PUSH_BLUEMIX = deviceId;

                                push.subscribe("all", new MFPPushResponseListener<String>() {
                                    @Override
                                    public void onSuccess(String response) {
                                        Log.d("onSuccess", "Subiscribed to tag : " + response);
                                    }

                                    @Override
                                    public void onFailure(MFPPushException exception) {
                                        Log.d("onSuccess", "Subiscription MFPPushException device: " + exception.getMessage());
                                    }
                                });
                            }

                            @Override
                            public void onFailure(MFPPushException ex) {
                                Log.d("onFailure", "Problem: " + ex.getLocalizedMessage());
                            }
                        });

                        push.listen(notificationListener);

        } catch (FileNotFoundException e) {

             Log.e(CLASS_NAME, "The bluelist.properties file was not found.", e);
        } catch (IOException e) {

             Log.e(CLASS_NAME, "The bluelist.properties file could not be read properly.", e);
        }
    }



    //KIT KAT Fix for ClassDefNotFoundException
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}