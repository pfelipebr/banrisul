package banrisul.ibm.com.banrisulmobileapp.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPSimplePushNotification;

import org.json.JSONException;
import org.json.JSONObject;

import banrisul.ibm.com.banrisulmobileapp.R;
import banrisul.ibm.com.banrisulmobileapp.maps.MapsActivity;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * Created by renatosilva on 02/11/16.
 */

public class BlueMixCreateNotificationRouter {


    public static void reciverBlueMixCreateNotificationRouter(Context mContext, MFPSimplePushNotification resultNotification) {

            try {

                    JSONObject mResultNotification = new JSONObject(resultNotification.getPayload().toString());
                    openAplicationScreen(mContext, mResultNotification, resultNotification.getAlert().toString());
            } catch (JSONException e) {

                e.printStackTrace();
            }
    }

    /*************************************************************** GO TO MY SCREEEN ****************************************************************************/



    private static void openAplicationScreen(Context context, JSONObject resultNotification, String mResultMessage) {

        try {


                Intent myIntent = new Intent(context, MapsActivity.class);


                    // define sound URI, the sound to be played when there's a notification
                    Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    String title = context.getString(R.string.app_name);

                    myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    PendingIntent pIntent = PendingIntent.getActivity(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
                    notificationBuilder.setContentTitle(title);
                    notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                    notificationBuilder.setVibrate(new long[]{100, 250, 100, 500});
                    notificationBuilder.setContentText(mResultMessage);
                    notificationBuilder.setTicker(mResultMessage);
                    notificationBuilder.setContentIntent(pIntent);
                    notificationBuilder.setSound(soundUri);
                    notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(mResultMessage));

                    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        //Kitkat and other old versions
                    }else{
                        //Lollipo devices or greater
                        notificationBuilder.setColor(0x0096CE);
                    }

                    /* Auto cancelar assim que clickar na notificação */
                    notificationBuilder.setAutoCancel(true);
                    notificationBuilder.setDefaults(Notification.FLAG_AUTO_CANCEL);


                    // Get an instance of the NotificationManager service
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);


                    // Build the notification and issues it with notification manager.
                    notificationManager.notify(0, notificationBuilder.build());
        }catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
