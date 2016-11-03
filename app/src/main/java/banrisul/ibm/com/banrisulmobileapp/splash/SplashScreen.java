package banrisul.ibm.com.banrisulmobileapp.splash;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import banrisul.ibm.com.banrisulmobileapp.R;
import banrisul.ibm.com.banrisulmobileapp.base.BaseActivity;
import banrisul.ibm.com.banrisulmobileapp.maps.MapsActivity;

/**
 * Created by renatosilva on 7/5/16.
 */
public class SplashScreen extends BaseActivity  {

    private int _splashTime = 3000;
    private Thread splashTread;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splashscreen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // thread for displaying the SplashScreen
        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(_splashTime);
                    }
                } catch (InterruptedException e) {

                } finally {

                    loadSystem();
                }
            }
        };
        splashTread.start();
    }



    private void loadSystem() {

        Intent mSplashScreen = new Intent(SplashScreen.this, MapsActivity.class);
        startActivity(mSplashScreen);
        this.finish();
    }
}

