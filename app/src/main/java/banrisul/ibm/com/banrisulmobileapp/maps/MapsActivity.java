package banrisul.ibm.com.banrisulmobileapp.maps;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import banrisul.ibm.com.banrisulmobileapp.R;
import banrisul.ibm.com.banrisulmobileapp.api.controller.GetAgenciasBarinsulController;
import banrisul.ibm.com.banrisulmobileapp.database.DbBarinsul;
import banrisul.ibm.com.banrisulmobileapp.utils.BanrisulUtils;
import banrisul.ibm.com.banrisulmobileapp.utils.ConstantUtils;

import static android.R.attr.permission;
import static banrisul.ibm.com.banrisulmobileapp.R.id.map;

public class MapsActivity extends AppCompatActivity implements LocationProvider.LocationCallback{


    private static final int REQUEST_LOCATION = 0;
    private static GoogleMap mMap;
    private static LatLng mRealLatLng;
    private final Integer LOCATION = 0x1;

    private Integer havePermission = 0;
    private ProgressDialog dialog;
    private SupportMapFragment mapFragment;
    private GetAgenciasBarinsulController mGetAgenciasBarinsulController;
    private LocationProvider mLocationProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

         mMap = null;
         mLocationProvider = null;


        if(BanrisulUtils.checkInternet(getBaseContext())){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                if(havePermission.equals(0)) {

                    getMyPermission(MapsActivity.this);
                }
            } else {

                //Start Get Location
                setStartUpMap(getBaseContext());
                mLocationProvider = new LocationProvider(this, this);
            }
        }else{

            Toast.makeText(MapsActivity.this, "Por favor verifique a sua conexão de internet.", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();


        //setStartUpMap(getBaseContext());
       if(mLocationProvider != null) {
            mLocationProvider.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

         if(mLocationProvider != null) {
            mLocationProvider.disconnect();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 1) {

            if (permissions.length == 1 && permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION )) {

                if(BanrisulUtils.checkInternet(getBaseContext())){

                    //Start Get Location
                    setStartUpMap(getBaseContext());
                    mLocationProvider = new LocationProvider(this, this);
                }/*else{

                    //OffLine Flow //Disable Because API Google maps dont suport mode offline in some devices.
                    chargeMyScreenOffLine(mAppCompatActivity);
                }*/
            } else {
                // Permission was denied. Display an error message.
                Toast.makeText(this, "" + permission + " Permission was denied. Display an error message.", Toast.LENGTH_LONG).show();
            }
        }else {
            // Permission was denied. Display an error message.
            Toast.makeText(this, "" + permission + " Permission was denied. Display an error message.", Toast.LENGTH_LONG).show();
        }
    }

    private void getMyPermission(AppCompatActivity mAppCompatActivity){

        if (ContextCompat.checkSelfPermission(mAppCompatActivity.getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if(BanrisulUtils.checkInternet(getBaseContext())){

                //Start Get Location
                setStartUpMap(getBaseContext());
                mLocationProvider = new LocationProvider(this, this);
            }/*else{

                //OffLine Flow //Disable Because API Google maps dont suport mode offline in some devices.
                chargeMyScreenOffLine(mAppCompatActivity);
            }*/
        } else {

            // Show rationale and request permission.
            askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION);
        }
    }

    private void askForPermission(String permission, Integer requestCode) {


           if (ContextCompat.checkSelfPermission(MapsActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

               // Should we show an explanation?
               if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this, permission)) {

                   //This is called if user has denied the permission before
                   //In this case I am just asking the permission again
                   this.havePermission = 1;
                   ActivityCompat.requestPermissions(MapsActivity.this, new String[]{permission}, requestCode);
               } else {

                   this.havePermission = 1;
                   ActivityCompat.requestPermissions(MapsActivity.this, new String[]{permission}, requestCode);
               }
           }

    }



    @Override
    public void handleNewLocation(Location location) {

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        mRealLatLng = new LatLng(currentLatitude, currentLongitude);

        saveMyLastLocation(String.valueOf(currentLatitude),  String.valueOf(currentLongitude));


        // PROD
        getAgenciasBarinsulNearMe(String.valueOf(currentLatitude), String.valueOf(currentLongitude));

        // MOCK
        //getAgenciasBarinsulNearMe("-466510257", "-23.5803767");
    }

    private void setStartUpMap(final Context context) {

        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {

            SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);

            // Try to obtain the map from the SupportMapFragment.
            mapFrag.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    if (googleMap != null) {

                        try {

                            googleMap.getUiSettings().setAllGesturesEnabled(true);
                            mMap = googleMap;
                            mMap.setMyLocationEnabled(true);
                        }catch (SecurityException e){

                        }
                    }
                }
            });
        }
    }

    private void saveMyLastLocation(String currentLatitude, String currentLongitude){

        try {

                JSONObject mCurrentLocation = new JSONObject();
                mCurrentLocation.put("currentLatitude", currentLatitude);
                mCurrentLocation.put("currentLongitude", currentLongitude);

                JSONArray mJsonArrayCurrentLocation = new JSONArray();

                mJsonArrayCurrentLocation.put(mCurrentLocation);


                DbBarinsul.mCreateAndSaveFile(MapsActivity.this, ConstantUtils.FILE_USER_LASTLOCATION, mJsonArrayCurrentLocation.toString());

        } catch (JSONException e) {

            e.printStackTrace();
        }
    }


    private void getAgenciasBarinsulNearMe(final String latitude, final String longitude){

                Runnable newRunnable = new Runnable() {
            @Override
            public void run() {

                mGetAgenciasBarinsulController = new GetAgenciasBarinsulController();
                mGetAgenciasBarinsulController.getAgenciasBarinsul(MapsActivity.this, latitude, longitude);
            }
        };

        runOnUiThread(newRunnable);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
    }



    /**
     * API Return
     * @param mAppCompatActivity
     * @param mArrayResutServer
     * @param progressDialog
     */
    public static void getScreenData(final AppCompatActivity mAppCompatActivity, final JSONArray mArrayResutServer, final ProgressDialog progressDialog, final String mLongitude, final String mLatitude) {

        setPinsInMap(mAppCompatActivity,   mArrayResutServer,   progressDialog,   mLatitude, mLongitude);
    }


    private static void setPinsInMap(final AppCompatActivity mAppCompatActivity, final JSONArray mArrayResutServer, final ProgressDialog progressDialog,  final String mLongitude, final String mLatitude) {

        mAppCompatActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(mArrayResutServer != null){

                    try {

                        //Set the pins in Maps
                        for(int pin=0; pin< mArrayResutServer.length(); pin++) {

                            JSONArray pins = mArrayResutServer.getJSONArray(pin);
                            addMarker("Barinsul","Barinsul", String.valueOf(pins.get(0).toString()),String.valueOf( pins.get(1).toString()));
                        }

                        //Put my real location my pin. Need be the last.
                        setMyMapPin("I am here","I am here", mLongitude, mLatitude);

                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                }

                progressDialog.dismiss();

            }
        });
    }


    private static void addMarker(String title, String snippet, String pinLongitude, String pinLatitude){

        double longitude= Double.parseDouble(pinLongitude);
        double latitude= Double.parseDouble(pinLatitude);

        mMap.addMarker(new MarkerOptions().position(
                new LatLng(latitude, longitude))
                .title(title)
                .snippet(snippet));
    }



    private static void setMyMapPin(String title, String snippet, String pinLongitude, String pinLatitude){


        double longitude= Double.parseDouble(pinLongitude);
        double latitude= Double.parseDouble(pinLatitude);

        LatLng latLng = new LatLng(latitude, longitude);


        mMap.addMarker(new MarkerOptions().position(
                new LatLng(latitude, longitude))
                .title(title)
                .snippet(snippet));

        //mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());

        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to Mountain View
                .zoom(15)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(60)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder


        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }



    private void chargeMyScreenOffLine(final AppCompatActivity mAppCompatActivity){

            mAppCompatActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {

                                /* Show ProgressDialog for user */
                                ProgressDialog progressDialog = BanrisulUtils.createProgressDialog(mAppCompatActivity);
                                progressDialog.show();

                                mAppCompatActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);

                                String mResulMyLastLocation = DbBarinsul.mReadJsonData(mAppCompatActivity, ConstantUtils.FILE_USER_LASTLOCATION);

                               String mBarinsulLastLocations = DbBarinsul.mReadJsonData(mAppCompatActivity, ConstantUtils.FILE_AGENCIAS_BANRISUL);

                                if(mResulMyLastLocation != null){

                                    JSONArray mJsonArrayMyLastLocation= new JSONArray(mResulMyLastLocation);

                                            if(mBarinsulLastLocations != null){

                                                JSONArray mJsonArrayBarinsulLastLocations = new JSONArray(mBarinsulLastLocations);

                                                            if(mJsonArrayBarinsulLastLocations.length() > 0){

                                                                JSONObject mJSONObjectLastLocation = mJsonArrayMyLastLocation.getJSONObject(0);


                                                                setPinsInMap(mAppCompatActivity,   mJsonArrayBarinsulLastLocations,   progressDialog,  mJSONObjectLastLocation.getString("currentLatitude"),  mJSONObjectLastLocation.getString("currentLongitude") );
                                                            }else{

                                                                Toast.makeText(mAppCompatActivity, "Voce não estava perto de nenhuma agencia.", Toast.LENGTH_LONG).show();
                                                                progressDialog.dismiss();
                                                            }
                                            }else{

                                                Toast.makeText(mAppCompatActivity, "Voce não estava perto de nenhuma agencia.", Toast.LENGTH_LONG).show();
                                                progressDialog.dismiss();
                                            }
                                    }else{

                                        Toast.makeText(mAppCompatActivity, "Voce não tem a ultima localização em modulo OffLine.", Toast.LENGTH_LONG).show();
                                         progressDialog.dismiss();
                                    }
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                }
            });
    }






}