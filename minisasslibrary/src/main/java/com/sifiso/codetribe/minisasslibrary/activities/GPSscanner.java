package com.sifiso.codetribe.minisasslibrary.activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.Util;

public class GPSscanner extends AppCompatActivity implements LocationListener {
    static final String LOG = GPSscanner.class.getSimpleName();
    //Location mCurrentLocation;
    // GoogleApiClient mLocationClient;
    ResponseDTO response;
    Location location;
    // LocationRequest mLocationRequest;
    LocationManager locationManager;
    static final int GPS_DATA = 102;
    public boolean isGPSEnabled = false;
    public boolean isNetworkEnabled = false;
    public boolean canGetLocation = false;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 2;
    private static final long MIN_TIME_BW_UPDATES = 1;
    TextView desiredAccuracy, txtLat, txtLng, txtAccuracy;
    RelativeLayout GPS_nameLayout;

    SeekBar seekBar;
    boolean isScanning;
    EvaluationSiteDTO evaluationSite;
    ImageView imgLogo, hero;
    Context ctx;
    ObjectAnimator logoAnimator;
    long start, end;
    Chronometer chronometer;
    boolean pleaseStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_gpsscanner);
        } catch (Exception e) {

        }
        ctx = getApplicationContext();
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       /* mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationClient.connect();*/
        evaluationSite = new EvaluationSiteDTO();
        setFields();
    }

    private Location getLocation() {
        onLocationChanged(location);
        return location;
    }

    private void setFields() {

        desiredAccuracy = (TextView) findViewById(R.id.GPS_desiredAccuracy);
        txtAccuracy = (TextView) findViewById(R.id.GPS_accuracy);
        txtLat = (TextView) findViewById(R.id.GPS_latitude);
        txtLng = (TextView) findViewById(R.id.GPS_longitude);
        GPS_nameLayout = (RelativeLayout) findViewById(R.id.GPS_nameLayout);
        GPS_nameLayout.setVisibility(View.GONE);
        seekBar = (SeekBar) findViewById(R.id.GPS_seekBar);
        imgLogo = (ImageView) findViewById(R.id.GPS_imgLogo);
        hero = (ImageView) findViewById(R.id.GPS_hero);
        chronometer = (Chronometer) findViewById(R.id.GPS_chrono);


        Statics.setRobotoFontBold(ctx, txtLat);
        Statics.setRobotoFontBold(ctx, txtLng);

        imgLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(imgLogo, 100, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {

                    }
                });
            }
        });

        txtAccuracy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(txtAccuracy, 100, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {

                    }
                });

            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                desiredAccuracy.setText("" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void startScan() {
        //getGPSCoordinates();
        getLocation();
        //txtAccuracy.setText("0.00");
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        isScanning = true;

    }

    public void setLocation(Location loc) {
        if (evaluationSite == null) {
            evaluationSite = new EvaluationSiteDTO();
        }
        this.location = loc;
        txtLat.setText("" + loc.getLatitude());
        txtLng.setText("" + loc.getLongitude());
        txtAccuracy.setText("" + loc.getAccuracy());

        evaluationSite.setLatitude(location.getLatitude());
        evaluationSite.setLongitude(location.getLongitude());
        evaluationSite.setAccuracy(location.getAccuracy());

        Util.flashSeveralTimes(hero, 200, 2, null);
        if (location.getAccuracy() == seekBar.getProgress()
                || location.getAccuracy() < seekBar.getProgress()) {
            isScanning = false;
            chronometer.stop();
            //resetLogo();
            evaluationSite.setLatitude(location.getLatitude());
            evaluationSite.setLongitude(location.getLongitude());
            evaluationSite.setAccuracy(location.getAccuracy());
            Log.i(LOG,
                    "### onStart l, " + location.getAccuracy());
            GPS_nameLayout.setVisibility(View.VISIBLE);
            stopScan();
            return;
        }

    }

    @Override
    protected void onDestroy() {
        overridePendingTransition(com.sifiso.codetribe.minisasslibrary.R.anim.slide_in_left, com.sifiso.codetribe.minisasslibrary.R.anim.slide_out_right);
        //  TimerUtil.killFlashTimer();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Intent eval = new Intent(GPSscanner.this, EvaluationActivity.class);
        eval.putExtra("siteData", evaluationSite);
        setResult(GPS_DATA, eval);
        finish();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

  /*  private void stopPeriodicUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mLocationClient, this
        );
        Log.e(LOG,
                "#################### stopPeriodicUpdates - removeLocationUpdates");
    }*/

    @Override
    public void onStart() {
        super.onStart();
        Log.i(LOG,
                "### onStart, binding RequestSyncService and PhotoUploadService");

        startScan();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gpsscanner, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            Intent eval = new Intent(GPSscanner.this, EvaluationActivity.class);
            eval.putExtra("siteData", evaluationSite);
            setResult(GPS_DATA, eval);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        Log.d(LOG,
                "#################### onStop");
        stopScan();
       /* if (mLocationClient != null) {
            if (mLocationClient.isConnected()) {
                stopScan();
            }
            // After disconnect() is called, the client is considered "dead".
            mLocationClient.disconnect();
            Log.e(LOG, "### onStop - locationClient isConnected: "
                    + mLocationClient.isConnected());
        }*/


        super.onStop();
    }

    /*@Override
    public void onConnected(Bundle bundle) {
        Log.i(LOG,
                "### ---> PlayServices onConnected() - gotta start something! >>");
        startScan();
        location = LocationServices.FusedLocationApi.getLastLocation(
                mLocationClient);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDisconnected() {

    }*/

    @Override
    public void onLocationChanged(Location loc) {
       /* Log.w(LOG, "### Location changed, lat: "
                + loc.getLatitude() + " lng: "
                + loc.getLongitude()
                + " -- acc: " + loc.getAccuracy());*/
        locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        Log.v(LOG + " gps", "=" + isGPSEnabled);

        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Log.v(LOG + " network", "=" + isNetworkEnabled);

        if (isGPSEnabled == false && isNetworkEnabled == false) {
            Log.d(LOG, "is not connected");
            showSettingDialog();
        } else {
            canGetLocation = true;
            if (isNetworkEnabled) {
                location = null;
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Log.d(LOG, "Network");
                if (locationManager != null) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.
                        return;
                    }
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        setGPSLocation(location);
                    }
                }
            }

            if (isGPSEnabled) {
                location = null;
                if (location == null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d(LOG, "GPs");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            setGPSLocation(location);
                        }
                    }
                }
            }

        }
    }

    public void showSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GPSscanner.this);

        builder.setTitle("GPS settings");
        builder.setMessage("GPS is not enabled. Do you want to go to settings menu, to search for location?");
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void setGPSLocation(Location loc) {
        setLocation(loc);

        Log.w(LOG, "### Passing " + loc.getAccuracy());
        if (loc.getAccuracy() <= ACCURACY_THRESHOLD) {
            location = loc;
            Log.w(LOG, "### Passing location2");
            //gpsScannerDialog.dismiss();
            setLocation(loc);
            stopScan();


            //finish();
            Log.e(LOG, "+++ best accuracy found: " + location.getAccuracy());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void stopScan() {
        //stopPeriodicUpdates();
        // listener.onLocationConfirmed(evaluationSite);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chronometer.stop();
            }
        });

        if (locationManager != null) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(GPSscanner.this);
            isScanning = false;


        }

    }

   /* @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }*/

    static final int ACCURACY_THRESHOLD = 5;
}
