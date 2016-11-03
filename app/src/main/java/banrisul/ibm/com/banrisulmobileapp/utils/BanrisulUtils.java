package banrisul.ibm.com.banrisulmobileapp.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.util.List;

import banrisul.ibm.com.banrisulmobileapp.R;

/**
 * Created by renatosilva on 21/10/16.
 */

public class BanrisulUtils {

    private BanrisulUtils (){

    }

    /**
     * Method for: Show a simple menssage.
     * @param title
     * @param caption
     * @param context
     * @param logo
     */
    public static void showMessage(String title, String caption, Context context, Drawable logo) {

        AlertDialog.Builder dialogo = new AlertDialog.Builder(context);

        dialogo.setTitle(title);
        dialogo.setIcon(logo);
        dialogo.setMessage(caption);
        dialogo.setNeutralButton("Ok    ", null);
        dialogo.show();
    }



    /**
     * Methodo for: Check conection with internet.
     * @param context
     * @return
     */
    public static boolean checkInternet(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {

            NetworkInfo[] info = connectivity.getAllNetworkInfo();

            if (info != null) {

                for (int i = 0; i < info.length; i++) {

                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {

                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Method for: Show a simple Progress Dialog
     * @param mContext
     * @return
     */
    public static ProgressDialog createProgressDialog(Context mContext) {

        ProgressDialog dialog = new ProgressDialog(mContext);

        try {

            if (!dialog.isShowing()) {

                dialog.show();
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.progress_dialog_custom);

            }
        } catch (Exception e) {

            e.printStackTrace();
            dialog.dismiss();
        }

        return dialog;
    }

    /**
     * Method for: Show a simple Toast message.
     * @param appCompatActivity
     * @param message
     */
    public static void makeToast(Activity appCompatActivity, String message) {

        Toast.makeText(appCompatActivity, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Method for: See if the App is renning in the system.
     * @param context
     * @return
     */
    public static Boolean appIsRunning(Context context) {

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = am.getRunningAppProcesses();
        Boolean retornoAppIsRun = false;

        for (int i = 0; i < runningAppProcessInfo.size(); i++) {

            if (runningAppProcessInfo.get(i).processName.equals(context.getApplicationInfo().taskAffinity)) {

                retornoAppIsRun = true;
            }
        }

        return retornoAppIsRun;
    }

}
