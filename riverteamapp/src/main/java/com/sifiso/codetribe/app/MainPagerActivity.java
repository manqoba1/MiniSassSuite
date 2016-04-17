package com.sifiso.codetribe.app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.activities.AboutActivity;
import com.sifiso.codetribe.minisasslibrary.activities.EvaluationActivity;
import com.sifiso.codetribe.minisasslibrary.activities.EvaluationSiteMapActivity;
import com.sifiso.codetribe.minisasslibrary.activities.MapsActivity;
import com.sifiso.codetribe.minisasslibrary.activities.ProfileActivity;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.fragments.RiverListFragment;
import com.sifiso.codetribe.minisasslibrary.services.CreateEvaluationListener;
import com.sifiso.codetribe.minisasslibrary.services.RequestSyncService;
import com.sifiso.codetribe.minisasslibrary.toolbox.BaseVolley;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheck;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheckResult;
import com.sifiso.codetribe.minisasslibrary.util.CacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.ErrorUtil;
import com.sifiso.codetribe.minisasslibrary.util.PDFUtil;
import com.sifiso.codetribe.minisasslibrary.util.SharedUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.TimerUtil;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class MainPagerActivity extends AppCompatActivity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        CreateEvaluationListener {
    static final int NUM_ITEMS = 2;
    static final String LOG = MainPagerActivity.class.getSimpleName();
    Context ctx;
    ProgressBar progressBar;
    RiverListFragment riverListFragment;
    private ResponseDTO response;
    private FloatingActionButton RL_add;
    TeamMemberDTO teamMember;
    GoogleApiClient mGoogleApiClient;
    LocationRequest locationRequest;
    Location location;
    boolean mResolvingError, mDoNotStart;
    private static final int REQUEST_RESOLVE_ERROR = 1001;

    private static final String DIALOG_ERROR = "dialog_error";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx = getApplicationContext();
        Log.i(LOG, "##### onCreate");
        riverListFragment = (RiverListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        setFields();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if (savedInstanceState != null) {
            Log.w(LOG, "savedInstanceState is not null - getting location");
            location = new Location("");
            location.setLatitude(savedInstanceState.getDouble("latitude"));
            location.setLongitude(savedInstanceState.getDouble("longitude"));
            stopLocationUpdates();
        }
        teamMember = SharedUtil.getTeamMember(ctx);
        Util.setCustomActionBar(ctx, getSupportActionBar(),
                teamMember.getFirstName() + " " + teamMember.getLastName(),
                teamMember.getTeam().getTeamName(),
                ContextCompat.getDrawable(ctx, R.drawable.ic_launcher), new Util.ActinBarListener() {
                    @Override
                    public void onEvokeProfile() {
                        Intent i = new Intent(MainPagerActivity.this, ProfileActivity.class);
                        startActivity(i);
                    }
                });

        getCachedRiverData();

        IntentFilter m = new IntentFilter(RequestSyncService.BROADCAST_REQUESTS_UPLOADED);
        LocalBroadcastManager.getInstance(ctx).registerReceiver(new BroadcastRequestsReceiver(), m);
    }

    private boolean checkSettings() {

        if (WebCheck.checkNetworkAvailability(ctx).isNetworkUnavailable()) {
            getCachedRiverData();
            return false;
        }
        //
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean gpsEnabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkEnabled = service.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Log.i(LOG, "GPS enabled: " + gpsEnabled + " networkEnabled: " + networkEnabled);

        if (!isLocationEnabled()) {
            Log.e(LOG, "extra check - isLocationEnabled: " + false);
        }
        if (!gpsEnabled && !networkEnabled && !isLocationEnabled()) {
            showSettingDialog();
        } else {
            return true;
        }
        return true;
    }

    public void showSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainPagerActivity.this);

        builder.setTitle("Location Setting");
        builder.setMessage("The app needs Location setting to be turned on so that it can start the river search." +
                "\n\nDo you want to turn it on?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent("com.google.android.gms.location.settings.GOOGLE_LOCATION_SETTINGS");
                startActivity(intent);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    public boolean isLocationEnabled() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                int locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
                return locationMode != Settings.Secure.LOCATION_MODE_OFF;

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

        } else {
            String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if (locationProviders == null || locationProviders.equalsIgnoreCase("null") || locationProviders.isEmpty()) {
                return false;
            }

        }

        return false;
    }

    int index;

    private void setFields() {
        RL_add = (FloatingActionButton) findViewById(R.id.fab);

        RL_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(RL_add, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        if (checkSettings()) {
                            refreshRivers = true;
                            startLocationUpdates();
                        }
                    }
                });

            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txtRadius = (TextView) findViewById(R.id.SI_radius);
        seekBar = (SeekBar) findViewById(R.id.SI_seekBar);

        seekBar.setMax(40);
        seekBar.setProgress(10);
        progressBar.setVisibility(View.GONE);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                txtRadius.setText("" + seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle data) {
        Log.e(LOG, "### onSaveInstanceState");
        if (response != null) {
            data.putSerializable("response", response);
            data.putSerializable("evaluationSite", (java.io.Serializable) evaluationSiteList);
            data.putInt("index", index);
            if (location != null) {
                data.putDouble("latitude", location.getLatitude());
                data.putDouble("longitude", location.getLongitude());
            }
            super.onSaveInstanceState(data);
        }
    }

    private void buildPages() {
        calculateDistances();
        if (riverListFragment != null) {
            riverListFragment.setResponse(response, index);
        }

    }


    @Override
    protected void onStart() {
        teamMember = SharedUtil.getTeamMember(ctx);
        Log.e(LOG, "################ onStart .... connect API and location clients ");
        mGoogleApiClient.connect();
        TimerUtil.killFlashTimer();

        Intent intent = new Intent(MainPagerActivity.this, RequestSyncService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;
        checkSettings();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.action_refresh:
                if (checkSettings()) {
                    refreshRivers = true;
                    startLocationUpdates();
                }
                return true;
            case R.id.log_out:
                SharedUtil.clearTeam(ctx);
                intent = new Intent(MainPagerActivity.this, SignActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.about_us:
                intent = new Intent(MainPagerActivity.this, AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.profile:
                intent = new Intent(MainPagerActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            case R.id.dicotomas:
                try {
                    String fileName = "dicotomas.pdf";
                    File dir = Environment.getExternalStorageDirectory();
                    if (!dir.exists()) {
                        dir = Environment.getDataDirectory();
                    }
                    File file = new File(dir, fileName);
                    if (file.exists()) {
                        Log.i(LOG, "## pdf from disk: " + file.getAbsolutePath() + " length: " + file.length());
                        Intent w = new Intent(Intent.ACTION_VIEW);
                        w.setDataAndType(Uri.fromFile(file), "application/pdf");
                        w.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(w);
                        return true;
                    }
                    setRefreshActionButtonState(true);
                    PDFUtil.downloadPDF(ctx,
                            "http://www.minisass.org/media/filer_public/2013/06/28/1111_minisass_dichotomous_key_nov_2011.pdf", fileName,
                            new PDFUtil.PDFListener() {
                                @Override
                                public void onDownloaded(final File pdfFile) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            setRefreshActionButtonState(false);
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                            startActivity(intent);
                                        }
                                    });

                                }

                                @Override
                                public void onError() {
                                    setRefreshActionButtonState(false);
                                    Util.showErrorToast(ctx, "Sorry. Unable to download file");
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;
            case R.id.how_to:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.minisass.mobi/how_to.php"));
                startActivity(intent);
                break;
            case R.id.minisass_org:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.minisass.org"));
                startActivity(intent);
                break;
            case R.id.youtube:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/channel/UCub24hwrLi52WR9C24uTbaQ"));
                startActivity(intent);
                break;
            case R.id.facebook:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.facebook.com/Minisass-Mini-Stream-Assessment-Scoring-System-544121315643167/timeline/"));
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause() {
        overridePendingTransition(com.sifiso.codetribe.minisasslibrary.R.anim.slide_in_left, com.sifiso.codetribe.minisasslibrary.R.anim.slide_out_right);

        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.i(LOG, "#### onResume");
        super.onResume();
    }


    private List<EvaluationSiteDTO> evaluationSiteList;

    @Override
    public void onListEvaluationSites(List<EvaluationSiteDTO> siteList, int index) {
        if (siteList.size() == 0) {
            Util.showToast(ctx, "Unfortunately there are no evaluations made yet on the river");
            return;
        }
        this.index = index;
        evaluationSiteList = siteList;
        Intent intent = new Intent(this, EvaluationListActivity.class);
        intent.putExtra("evaluationSite", (java.io.Serializable) siteList);
        startActivity(intent);

    }


    @Override
    public void onRefreshMap(RiverDTO river, int result) {
        if (river.getEvaluationsiteList().size() == 0) {
            Util.showToast(ctx, "Unfortunately there are no evaluations to display on " + river.getRiverName().trim() + " river");
            return;
        }
        Intent intent = new Intent(MainPagerActivity.this, MapsActivity.class);
        intent.putExtra("river", river);
        intent.putExtra("displayType", result);
        startActivity(intent);
    }


    @Override
    public void onCreateEvaluation(final RiverDTO river) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                String j = gson.toJson(river);
                try {
                    File dir = Environment.getExternalStorageDirectory();
                    File file = new File(dir, System.currentTimeMillis() + ".data");
                    FileUtils.writeStringToFile(file, j);
                    Intent m = new Intent(getApplicationContext(), EvaluationActivity.class);
                    m.putExtra("riverCreate", file.getAbsolutePath());
                    m.putExtra("statusCode", EvaluationActivity.CREATE_EVALUATION);
                    startActivity(m);
                } catch (IOException e) {
                    Log.e(LOG, "Failed to write river file", e);
                }

            }
        });


    }

    @Override
    public void onDirection(Double latitude, Double longitude) {
        String url = "http://maps.google.com/maps?saddr="
                + location.getLatitude() + "," + location.getLongitude()
                + "&daddr=" + latitude + "," + longitude + "&mode=driving";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    @Override
    public void onSitesMapRequested(RiverDTO river) {
        Gson g = new Gson();
        String json = g.toJson(river);
        File dir = Environment.getExternalStorageDirectory();
        File file = new File(dir, "river_"+river.getRiverID()+".json");
        try {
            FileUtils.writeStringToFile(file,json);
            Intent m = new Intent(ctx, EvaluationSiteMapActivity.class);
            m.putExtra("path",file.getAbsolutePath());
            startActivityForResult(m,EVAL_SITES_MAP);
        } catch (IOException e) {
            Util.showErrorToast(ctx,"Unable to get data for map");
        }
    }

   static final int EVAL_SITES_MAP = 182;
    @Override
    public void onNewEvaluation() {

        Intent intent = new Intent(MainPagerActivity.this, EvaluationActivity.class);
        startActivity(intent);
    }

    static final int CREATE_EVALUATION = 108;

    boolean mRequestingLocationUpdates;
    Snackbar snackbar;

    protected void startLocationUpdates() {
        Log.w(LOG, "## startLocationUpdates ....");
        snackbar = Snackbar.make(RL_add, "Starting to locate GPS coordinates ...", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
        if (mGoogleApiClient.isConnected()) {
            mRequestingLocationUpdates = true;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                 return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, locationRequest, this);
        }
    }

    static final int ACCURACY_LIMIT = 150;
    boolean refreshRivers;

    @Override
    public void onLocationChanged(Location location) {
        Log.i(LOG, "####### onLocationChanged " + location.getAccuracy());

        if (location.getAccuracy() <= ACCURACY_LIMIT) {
            this.location = location;
            stopLocationUpdates();
            if (refreshRivers) {
                refreshRivers = false;
                getRiversAroundMe();
            } else {
                snackbar.dismiss();
            }
        }

    }


    protected void stopLocationUpdates() {
        Log.e(LOG, "###### stopLocationUpdates - " + new Date().toString());
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    static final int REQUEST_LOCATION = 6, REQUEST_WRITE_EXTERNAL = 7;
    @Override
    public void onConnected(Bundle bundle) {
        Log.e(LOG, "########### onConnected .... check LOCATION permission");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);
            } else {
                location = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                setLocationRequest();
            }
        } else {
            setLocationRequest();
        }
        Log.e(LOG, "########### onConnected .... check LOCATION permission");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_EXTERNAL);
            }
        }
    }

    private void setLocationRequest() {
        Log.w(LOG, "## requesting location updates ....");
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(1000);
        startLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e(LOG, "Permission has been granted for LOCATION");
                setLocationRequest();
            } else {
                finish();
            }
        }
        if (requestCode == REQUEST_WRITE_EXTERNAL) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e(LOG, "Permission has been granted for WRITE_EXTERNAL_STORAGE");
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }

    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(), "errordialog");
    }


    boolean isBusy;


    private void getRefreshCachedData() {
        CacheUtil.getCachedData(ctx, CacheUtil.CACHE_DATA, new CacheUtil.CacheUtilListener() {
            @Override
            public void onFileDataDeserialized(final ResponseDTO r) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (r != null) {
                            response = r;
                            buildPages();
                        }

                    }
                });

            }

            @Override
            public void onDataCached(ResponseDTO response) {

            }

            @Override
            public void onError() {

            }
        });
    }

    private void getCachedRiverData() {
        final WebCheckResult w = WebCheck.checkNetworkAvailability(ctx);
        CacheUtil.getCachedData(ctx, CacheUtil.CACHE_DATA, new CacheUtil.CacheUtilListener() {
            @Override
            public void onFileDataDeserialized(final ResponseDTO r) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (r == null || r.getRiverList().isEmpty()) {
                            refreshRivers = true;
                            startLocationUpdates();
                            return;
                        }
                        response = r;
                        if (response.getRiverList() != null || !response.getRiverList().isEmpty()) {
                            buildPages();
                        } else {
                            refreshRivers = true;
                            startLocationUpdates();
                        }

                    }
                });


            }

            @Override
            public void onDataCached(ResponseDTO r) {

            }

            @Override
            public void onError() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        setRefreshActionButtonState(false);
                        if (w.isWifiConnected() || w.isMobileConnected()) {
                            startLocationUpdates();
                        }
                    }
                });

            }
        });


    }

    TextView txtRadius;
    SeekBar seekBar;

    private void getRiversAroundMe() {
        if (isBusy) {
            Log.e(LOG, "### getRiversAroundMe is BUSY!!!");
            return;
        }

        Log.d(LOG, "############### getRiversAroundMe");
        snackbar = Snackbar.make(RL_add,"Refreshing river data around you ....",Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.LIST_DATA_WITH_RADIUS_RIVERS);
        w.setLatitude(location.getLatitude());
        w.setLongitude(location.getLongitude());
        w.setRadius(seekBar.getProgress());

        isBusy = true;
        if (riverListFragment != null) {
            riverListFragment.refreshListStart();
        }
        isBusy = true;
        setRefreshActionButtonState(true);
        BaseVolley.getRemoteData(Statics.SERVLET_ENDPOINT, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO r) {
                isBusy = false;
                setRefreshActionButtonState(false);
                snackbar.dismiss();
                Snackbar.make(RL_add,"Found "+r.getRiverList().size() +" rivers around you ....",Snackbar.LENGTH_LONG).show();
                if (!ErrorUtil.checkServerError(ctx, r)) {
                    return;
                }
                Log.e(LOG, "### getRiversAroundMe, found: " + r.getRiverList().size());
                if (location != null) {
                    for (RiverDTO river : r.getRiverList()) {
                        river.calculateDistance(location.getLatitude(), location.getLongitude());
                    }
                    Collections.sort(r.getRiverList());
                }
                CacheUtil.cacheData(ctx, r, CacheUtil.CACHE_DATA, new CacheUtil.CacheUtilListener() {
                    @Override
                    public void onFileDataDeserialized(final ResponseDTO resp) {

                    }

                    @Override
                    public void onDataCached(ResponseDTO r) {
                        response = r;
                        buildPages();
                        if (riverListFragment != null) {
                            riverListFragment.setResponse(r);
                            riverListFragment.refreshListStop();
                        }
                    }

                    @Override
                    public void onError() {
                    }
                });
            }

            @Override
            public void onVolleyError(VolleyError error) {
                isBusy = false;
                //riverListFragment.refreshListStop();
                progressBar.setVisibility(View.GONE);
                if (riverListFragment != null) {
                    riverListFragment.refreshListStop();
                }
                Toast.makeText(ctx, "Problem: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isBusy = false;
                        snackbar.dismiss();
                        setRefreshActionButtonState(false);
                        if (riverListFragment != null) {
                            riverListFragment.refreshListStop();
                        }
                        progressBar.setVisibility(View.GONE);
                        if (!message.equals(null)) {

                            Log.e(LOG, message);
                            Util.showErrorToast(ctx, message);

                        }
                    }
                });
            }
        });


    }

    public void getData() {
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.GET_DATA);

        setRefreshActionButtonState(true);
        BaseVolley.getRemoteData(Statics.SERVLET_ENDPOINT, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(final ResponseDTO r) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        setRefreshActionButtonState(false);
                        if (riverListFragment != null) {
                            riverListFragment.refreshListStop();
                        }
                        Log.e(LOG, "## getStarterData responded...statusCode: " + r.getStatusCode());
                        if (!ErrorUtil.checkServerError(ctx, r)) {
                            return;
                        }

                        CacheUtil.cacheData(ctx, r, CacheUtil.CACHE_RIVER_DATA, new CacheUtil.CacheUtilListener() {
                            @Override
                            public void onFileDataDeserialized(final ResponseDTO resp) {

                            }

                            @Override
                            public void onDataCached(ResponseDTO r) {
                                SharedUtil.setRiverLoadedFlag(ctx, 0);
                            }

                            @Override
                            public void onError() {

                            }
                        });

                    }
                });
            }

            @Override
            public void onVolleyError(VolleyError error) {
                isBusy = false;
                if (riverListFragment != null) {
                    riverListFragment.refreshListStop();
                }
                Toast.makeText(ctx, "Problem: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isBusy = false;
                        setRefreshActionButtonState(false);
                        if (riverListFragment != null) {
                            riverListFragment.refreshListStop();
                        }
                        progressBar.setVisibility(View.GONE);
                        if (!message.equals(null)) {

                            Log.e(LOG, message);
                            Util.showErrorToast(ctx, message);

                        }
                    }
                });
            }
        });


    }

    private void calculateDistances() {
//        if (location != null) {
//            List<RiverPointDTO> riverPoints = new ArrayList<>();
//            Location spot = new Location(LocationManager.GPS_PROVIDER);
//
//            for (RiverDTO w : response.getRiverList()) {
//                for (RiverPartDTO x : w.getRiverpartList()) {
//                    for (RiverPointDTO y : x.getRiverpointList()) {
//                        spot.setLatitude(y.getLatitude());
//                        spot.setLongitude(y.getLongitude());
//                        y.setDistanceFromMe(location.distanceTo(spot));
//                    }
//                    Collections.sort(x.getRiverpointList());
//                    x.setNearestLatitude(x.getRiverpointList().get(0).getLatitude());
//                    x.setNearestLongitude(x.getRiverpointList().get(0).getLongitude());
//                    x.setDistanceFromMe(x.getRiverpointList().get(0).getDistanceFromMe());
//                }
//                Collections.sort(w.getRiverpartList());
//                w.setNearestLatitude(w.getRiverpartList().get(0).getNearestLatitude());
//                w.setNearestLongitude(w.getRiverpartList().get(0).getNearestLongitude());
//                w.setDistanceFromMe(w.getRiverpartList().get(0).getDistanceFromMe());
//
//
//            }
//            Collections.sort(response.getRiverList());
//        }
    }

    boolean hasBeenPressed;
    @Override
    public void onBackPressed() {
        if (hasBeenPressed) {
            finish();

        } else {
            hasBeenPressed = true;
            Snackbar.make(progressBar,"Please press Back again to exit", Snackbar.LENGTH_LONG).show();
        }

    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.w(LOG, "## RequestSyncService ServiceConnection: onServiceConnected");
            RequestSyncService.LocalBinder binder = (RequestSyncService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.startSyncCachedRequests(new RequestSyncService.RequestSyncListener() {
                @Override
                public void onTasksSynced(int goodResponses, int badResponses) {
                    Log.w(LOG,"------ onTasksSynced goodResponses: " + goodResponses + " bad: " + badResponses);
                }

                @Override
                public void onError(String message) {

                }
            });

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.w(LOG, "## RequestSyncService onServiceDisconnected");
            mBound = false;
        }
    };
    boolean mBound;
    RequestSyncService mService;

    @Override
    protected void onStop() {
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        try {
            mGoogleApiClient.disconnect();
        } catch (Exception e) {
            Log.e(LOG, "Failed to Stop something", e);
        }

        super.onStop();
    }

    Menu mMenu;

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (mMenu != null) {
            final MenuItem refreshItem = mMenu.findItem(R.id.action_refresh);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(R.layout.action_bar_progess);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }
    private class BroadcastRequestsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(LOG,"BroadcastRequestsReceiver onReceive: Requests have been uploaded");
            getRiversAroundMe();
        }
    }
}
