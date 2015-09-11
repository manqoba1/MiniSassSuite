package com.sifiso.codetribe.riverteamapp;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.sifiso.codetribe.minisasslibrary.activities.MapsActivity;
import com.sifiso.codetribe.minisasslibrary.activities.ProfileActivity;
import com.sifiso.codetribe.minisasslibrary.activities.SettingsActivity;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverPartDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverPointDTO;
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
import com.sifiso.codetribe.minisasslibrary.util.SharedUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.TimerUtil;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainPagerActivity extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, CreateEvaluationListener {
    static final int NUM_ITEMS = 2;
    static final String LOG = MainPagerActivity.class.getSimpleName();
    Context ctx;
    ProgressBar progressBar;
    RiverListFragment riverListFragment;
    Menu mMenu;
    private ResponseDTO response;
    private TextView RL_add;
    TeamMemberDTO teamMember;
    GoogleApiClient mGoogleApiClient;
    LocationRequest locationRequest;
    Location location;
    boolean mResolvingError;
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx = getApplicationContext();

        riverListFragment = (RiverListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        setField();
        if (savedInstanceState != null) {
            response = (ResponseDTO) savedInstanceState.getSerializable("response");
            evaluationSiteList = (List<EvaluationSiteDTO>) savedInstanceState.getSerializable("evaluationSite");
            index = savedInstanceState.getInt("index");

            if (response != null) {
                buildPages();
            }
        } else {
            getCachedRiverData();
        }


    }

    public void showSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainPagerActivity.this);

        builder.setTitle("GPS settings");
        builder.setMessage("GPS is not enabled. Please enable it to continue.");
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
                finish();
                dialog.cancel();
            }
        });
        builder.show();
    }

    int index;

    private void setField() {
        RL_add = (TextView) findViewById(R.id.RL_add);

        RL_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(RL_add, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        Intent intent = new Intent(MainPagerActivity.this, EvaluationActivity.class);
                        startActivity(intent);
                    }
                });

            }
        });
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }

    @Override
    protected void onSaveInstanceState(Bundle data) {
        if (response != null) {
            data.putSerializable("response", response);
            data.putSerializable("evaluationSite", (java.io.Serializable) evaluationSiteList);
            data.putInt("index", index);
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
        if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
        }
        TimerUtil.killFlashTimer();

        Intent intent = new Intent(MainPagerActivity.this, RequestSyncService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Log.d(LOG,
                "#################### onStart");
        setActionBar();
        super.onStart();
    }

    private void setActionBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Util.setCustomActionBar(ctx, getSupportActionBar(), teamMember.getFirstName() + " " + teamMember.getLastName(), teamMember.getTeamName(), teamMember.getTeamMemberImage(), new Util.ActinBarListener() {
                    @Override
                    public void onEvokeProfile() {
                        Intent pro = new Intent(MainPagerActivity.this, ProfileActivity.class);
                        startActivity(pro);
                    }
                });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;

        if (response == null) {
            getCachedRiverData();
        } /*else {
            getRefreshCachedData();
            progressBar.setVisibility(View.GONE);
        }*/
        // startActivity(new Intent(MainPagerActivity.this, SplashActivity.class));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;
        //noinspection SimplifiableIfStatement
        switch (id) {
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
            case R.id.setting:
                intent = new Intent(MainPagerActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
       /* switch (id) {
            case R.id.add_member:
                break;
            case R.id.log_out:
                break;
            default:

        };*/


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        overridePendingTransition(com.sifiso.codetribe.minisasslibrary.R.anim.slide_in_left, com.sifiso.codetribe.minisasslibrary.R.anim.slide_out_right);

        super.onPause();
    }

    @Override
    protected void onResume() {
        overridePendingTransition(com.sifiso.codetribe.minisasslibrary.R.anim.slide_out_left, com.sifiso.codetribe.minisasslibrary.R.anim.slide_in_right);

        super.onResume();
    }


    private List<EvaluationSiteDTO> evaluationSiteList;

    @Override
    public void onRefreshEvaluation(List<EvaluationSiteDTO> siteList, int index, String riverName) {
        Log.d(LOG, "onclick" + siteList.toString());
        if (siteList.size() == 0) {
            Util.showToast(ctx, "Unfortunately there are no evaluations made yet on " + riverName + " river");
            return;
        }
        this.index = index;
        evaluationSiteList = siteList;
        Intent intent = new Intent(MainPagerActivity.this, EvaluationListActivity.class);
        intent.putExtra("evaluationSite", (java.io.Serializable) siteList);
        intent.putExtra("response", response);
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
    public void onCreateEvaluation(RiverDTO river) {
        // ToastUtil.toast(ctx, river.getRiverName());
        Intent createEva = new Intent(MainPagerActivity.this, EvaluationActivity.class);
        createEva.putExtra("riverCreate", river);
        createEva.putExtra("response", response);
        createEva.putExtra("statusCode", CREATE_EVALUATION);
        startActivity(createEva);
    }

    @Override
    public void onDirection(Double latitude, Double longitude) {
        Log.i(LOG, "startDirectionsMap ..........");
        String url = "http://maps.google.com/maps?saddr="
                + location.getLatitude() + "," + location.getLongitude()
                + "&daddr=" + latitude + "," + longitude + "&mode=driving";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    @Override
    public void onPullRefresh() {
        getCachedRiverData();
    }

    @Override
    public void onNewEvaluation() {
        Intent intent = new Intent(MainPagerActivity.this, EvaluationActivity.class);
        startActivity(intent);
    }

    static final int CREATE_EVALUATION = 108;

    boolean mRequestingLocationUpdates;

    protected void startLocationUpdates() {
        Log.w(LOG, "###### startLocationUpdates: " + new Date().toString());
        if (mGoogleApiClient.isConnected()) {
            mRequestingLocationUpdates = true;
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, locationRequest, this);
        }
    }

    static final int ACCURACY_LIMIT = 50;

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        setLoc(location);
        if (location.getAccuracy() <= ACCURACY_LIMIT) {
            setLoc(location);
            stopLocationUpdates();
            // getCachedRiverData();
            // getRiversAroundMe();
        }
        Log.e(LOG, "####### onLocationChanged");
    }


    void setLoc(Location loc) {
        this.location = loc;
        // Log.v(LOG + " network",location.getLongitude()+ " = " + isNetworkEnabled);
        // getCachedRiverData();
    }

    protected void stopLocationUpdates() {
        Log.e(LOG, "###### stopLocationUpdates - " + new Date().toString());
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.i(LOG, "+++  onConnected() -  requestLocationUpdates ...");
        location = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (location != null) {
            Log.w(LOG, "## requesting location ....lastLocation: "
                    + location.getLatitude() + " "
                    + location.getLongitude() + " acc: "
                    + location.getAccuracy());
        }

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(2000);
        if (location == null) {
            startLocationUpdates();
            return;
        }
        if (location.getAccuracy() > ACCURACY_LIMIT) {
            startLocationUpdates();
        } else {
            // getCachedRiverData();
            // getRiversAroundMe();
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

    /* Creates a dialog for an error message */
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

    private void calculateDistancesForSite() {
        if (location != null) {
            List<EvaluationSiteDTO> riverPoints = new ArrayList<>();
            Location spot = new Location(LocationManager.GPS_PROVIDER);

            for (EvaluationSiteDTO w : response.getEvaluationSiteList()) {
                spot.setLatitude(w.getLatitude());
                spot.setLongitude(w.getLongitude());
                w.setDistanceFromMe(location.distanceTo(spot));
            }
            Collections.sort(response.getEvaluationSiteList());
        }
    }

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
            public void onFileDataDeserialized(final ResponseDTO respond) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(LOG, new Gson().toJson(respond));
                        progressBar.setVisibility(View.GONE);
                        response = respond;
                        if (respond != null) {
                            buildPages();
                        }
                        if (w.isWifiConnected()) {
                            getRiversAroundMe();
                            // getData();
                        } else if (w.isMobileConnected()) {
                            getRiversAroundMe();
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
                        if (w.isWifiConnected() || w.isMobileConnected()) {
                            getRiversAroundMe();
                        }
                    }
                });

            }
        });


    }

    boolean isLocatorOn;

    private void getRiversAroundMe() {
        if (location == null) {
            Toast.makeText(ctx, "Busy...getting rivers ...t", Toast.LENGTH_SHORT).show();
            if (!isLocatorOn) {
                showSettingDialog();
                isLocatorOn = true;
            }
            // getRiversAroundMe();
            //riverListFragment.refreshListStop();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    riverListFragment.refreshListStop();
                }
            }, 5000);
            return;
        }
        if (isBusy) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    riverListFragment.refreshListStop();
                }
            }, 5000);

            // riverListFragment.refreshListStop();
            return;
        }

//TODO remember to change back to location getLatitude and longitude
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.LIST_DATA_WITH_RADIUS_RIVERS);
        /*w.setLatitude(-26.30566667);
        w.setLongitude(28.01558333);
        w.setRadius(5);*/
        w.setLatitude(location.getLatitude());
        w.setLongitude(location.getLongitude());
        w.setRadius(5);
        isBusy = true;
        //riverListFragment.refreshListStart();
        if (riverListFragment != null) {
            riverListFragment.refreshListStart();
        }
        //progressBar.setVisibility(View.VISIBLE);
        BaseVolley.getRemoteData(Statics.SERVLET_ENDPOINT, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO r) {
                isBusy = false;


                Log.e(LOG, "## getStarterData responded...statusCode: " + r.getStatusCode());
                if (!ErrorUtil.checkServerError(ctx, r)) {
                    return;
                }

                CacheUtil.cacheData(ctx, r, CacheUtil.CACHE_DATA, new CacheUtil.CacheUtilListener() {
                    @Override
                    public void onFileDataDeserialized(final ResponseDTO resp) {

                    }

                    @Override
                    public void onDataCached(ResponseDTO r) {
                                    /*Intent intent = new Intent(getApplicationContext(), RequestSyncService.class);
                                    startService(intent);*/
                        // getData();
                        response = r;
                        buildPages();
                        if (riverListFragment != null) {
                            riverListFragment.refreshListStop();
                        }
                        try {
                            if (SharedUtil.getRiverLoadedFlag(ctx) == 1) {
                                SharedUtil.setRiverLoadedFlag(ctx, 0);
                                getData();
                            } else {
                                progressBar.setVisibility(View.GONE);
                                if (riverListFragment != null) {
                                    riverListFragment.refreshListStop();
                                }
                            }
                        } catch (Exception e) {

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

        BaseVolley.getRemoteData(Statics.SERVLET_ENDPOINT, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(final ResponseDTO r) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
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

                            }

                            @Override
                            public void onError() {

                            }
                        });
                            /*Intent intent = new Intent(getApplicationContext(), RequestSyncService.class);
                            startService(intent);*/
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
                        //riverListFragment.refreshListStop();
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
        if (location != null) {
            List<RiverPointDTO> riverPoints = new ArrayList<>();
            Location spot = new Location(LocationManager.GPS_PROVIDER);

            for (RiverDTO w : response.getRiverList()) {
                for (RiverPartDTO x : w.getRiverpartList()) {
                    for (RiverPointDTO y : x.getRiverpointList()) {
                        spot.setLatitude(y.getLatitude());
                        spot.setLongitude(y.getLongitude());
                        y.setDistanceFromMe(location.distanceTo(spot));
                    }
                    Collections.sort(x.getRiverpointList());
                    x.setNearestLatitude(x.getRiverpointList().get(0).getLatitude());
                    x.setNearestLongitude(x.getRiverpointList().get(0).getLongitude());
                    x.setDistanceFromMe(x.getRiverpointList().get(0).getDistanceFromMe());
                }
                Collections.sort(w.getRiverpartList());
                w.setNearestLatitude(w.getRiverpartList().get(0).getNearestLatitude());
                w.setNearestLongitude(w.getRiverpartList().get(0).getNearestLongitude());
                w.setDistanceFromMe(w.getRiverpartList().get(0).getDistanceFromMe());


            }
            Collections.sort(response.getRiverList());
        }
    }

    @Override
    public void onBackPressed() {
        if (riverListFragment != null) {
            if (riverListFragment.refreshLayout.isRefreshing()) {
                riverListFragment.refreshListStop();
            } else {
                super.onBackPressed();
            }
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
            WebCheckResult wcr = WebCheck.checkNetworkAvailability(ctx, true);
            if (wcr.isWifiConnected() || wcr.isMobileConnected()) {
                mService.startSyncCachedRequests(new RequestSyncService.RequestSyncListener() {
                    @Override
                    public void onTasksSynced(int goodResponses, int badResponses) {
                        Log.w(LOG, "@@ cached requests done, good: " + goodResponses + " bad: " + badResponses);
                        getRefreshCachedData();
                        if (goodResponses > 0) {
                            getRiversAroundMe();
                        }
                    }

                    @Override
                    public void onError(String message) {

                    }
                });


            }
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
        Log.d(LOG,
                "#################### onStop");


        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        Log.w(LOG, "############## onStop stopping google service clients");
        try {
            mGoogleApiClient.disconnect();
        } catch (Exception e) {
            Log.e(LOG, "Failed to Stop something", e);
        }

        super.onStop();
    }
}
