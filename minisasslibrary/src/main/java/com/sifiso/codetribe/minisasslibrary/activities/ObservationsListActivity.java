package com.sifiso.codetribe.minisasslibrary.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dialogs.EditEvaluationDialog;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationInsectDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.InsectDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.fragments.ObservationListFragment;
import com.sifiso.codetribe.minisasslibrary.toolbox.BaseVolley;
import com.sifiso.codetribe.minisasslibrary.util.RiverDataWorker;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.util.Date;
import java.util.List;

public class ObservationsListActivity extends AppCompatActivity implements
        ObservationListFragment.ObservationListener, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    LocationRequest locationRequest;
    ObservationListFragment observationListFragment;
    FloatingActionButton fab;
    RiverDTO river;
    EvaluationSiteDTO site;
    Snackbar snackbar;
    Context ctx;
    View handle;
    ActionBar actionBar;
    double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observations_list);
        ctx = getApplicationContext();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        actionBar = getSupportActionBar();
        actionBar.setTitle("");
        handle = findViewById(R.id.handle);
        site = (EvaluationSiteDTO) getIntent().getSerializableExtra("site");
        latitude = getIntent().getDoubleExtra("latitude",0);
        longitude = getIntent().getDoubleExtra("longitude",0);
        observationListFragment = (ObservationListFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshRiver();
            }
        });

        getCachedRiver(site.getRiverID());
    }
    Menu mMenu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.MAP_refresh) {
            refreshRiver();
        }

        return super.onOptionsItemSelected(item);
    }
    private void refreshRiver() {
        snackbar = Snackbar.make(fab, "Refreshing river data ...",
                Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
        RiverDataWorker.refreshRiver((MSApp) getApplication(),
                ctx, river.getRiverID(),
                new RiverDataWorker.RiverSearchListener() {
                    @Override
                    public void onResponse(List<RiverDTO> rivers) {
                        snackbar.dismiss();
                        if (!rivers.isEmpty()) {
                            river = rivers.get(0);
                            setFragmentRiver();
                        }
                    }

                    @Override
                    public void onError(String message) {

                    }
                });
    }

    private void getCachedRiver(Integer riverID) {
        RiverDataWorker.getRiverByID((MSApp) getApplication(), riverID, new RiverDataWorker.RiverCacheListener() {
            @Override
            public void onRiversCached() {

            }

            @Override
            public void onRiversFound(List<RiverDTO> rivers) {
                if (!rivers.isEmpty()) {
                    river = rivers.get(0);
                    setFragmentRiver();

                }
            }

            @Override
            public void onError(String message) {

            }
        });
    }

    @Override
    public void onPictureRequired(EvaluationDTO eval) {
        Util.showToast(ctx,"Observation Picture feature under construction");
    }

    private void setFragmentRiver() {
        int i = 1;
        for (EvaluationSiteDTO s : river.getEvaluationsiteList()) {
            String name = "Unnamed Site " + i;
            if (s.getSiteName() != null) {
                name = s.getSiteName();
            }
            s.setSiteName(name);
            for (EvaluationDTO m: s.getEvaluationList()) {
                m.setSiteName(name);
            }
            i++;
        }
        observationListFragment.setRiver(river, site);
        observationListFragment.setLocation(latitude,longitude);

        Util.setCustomActionBar(ctx,actionBar,river.getRiverName(),"",
                ContextCompat.getDrawable(ctx,R.drawable.ic_launcher),null);
    }
    @Override
    public void onEvaluationEdit(EvaluationDTO eval) {
        EditEvaluationDialog editEvaluationDialog = new EditEvaluationDialog();


        editEvaluationDialog.setEvaluation(eval);
        editEvaluationDialog.setListener(new EditEvaluationDialog.EditEvaluationDialogListener() {
            @Override
            public void onSaveUpdate(EvaluationDTO evaluation) {
                if (evaluation.getEvaluationID() == null) {
                    Util.showErrorToast(getApplicationContext(), "Evaluation can not be edited yet");
                    return;
                }
                editEvaluation(evaluation);
            }
        });
        editEvaluationDialog.show(getSupportFragmentManager(), "Edit Evaluation");
    }

    @Override
    public void onInsectView(List<EvaluationInsectDTO> insects) {
        Util.showPopupInsectsSelected(getApplicationContext(), this, insects, handle,
                getString(R.string.insect_selected), new Util.UtilPopupInsectListener() {
                    @Override
                    public void onInsectSelected(InsectDTO insect) {
                        Intent intent = new Intent(getApplicationContext(), InsectBrowser.class);
                        intent.putExtra("insect", insect);
                        startActivity(intent);
                        // Toast.makeText(ctx, insect.getGroupName(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void editEvaluation(EvaluationDTO dto) {
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.UPDATE_EVALUATION);
        w.setEvaluation(dto);
        Log.i(LOG, "Updated : " + new Gson().toJson(dto));
        BaseVolley.sendRequest(Statics.SERVLET_ENDPOINT, w, getApplicationContext(), new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO r) {

                Log.i(LOG, "Observation has benn Updated : ");
            }

            @Override
            public void onVolleyError(VolleyError error) {

            }

            @Override
            public void onError(String message) {

            }
        });
    }

    //Location stuff
    Location location;
    static final int ACCURACY_LIMIT = 20;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(LOG, "+++  onConnected() -  requestLocationUpdates ...");
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] strings = new String[2];
            strings[0] = android.Manifest.permission.ACCESS_FINE_LOCATION;
            strings[1] = android.Manifest.permission.ACCESS_COARSE_LOCATION;
            ActivityCompat.requestPermissions(this, strings, 121);
            return;
        }
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
        locationRequest.setFastestInterval(1000);
        if (location != null) {
            if (location.getAccuracy() > ACCURACY_LIMIT) {
                startLocationUpdates();
            }
            Log.w(LOG, "## requesting location ....lastLocation: "
                    + location.getLatitude() + " "
                    + location.getLongitude() + " acc: "
                    + location.getAccuracy());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 121:
                locationRequest = LocationRequest.create();
                locationRequest.setInterval(3000);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setFastestInterval(1000);
                startLocationUpdates();
                break;
        }
    }

    boolean mRequestingLocationUpdates;

    protected void startLocationUpdates() {
        Log.w(LOG, "###### startLocationUpdates: " + new Date().toString());
        if (mGoogleApiClient.isConnected()) {
            mRequestingLocationUpdates = true;
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, locationRequest, this);
            snackbar = Snackbar.make(handle, "Getting device GPS coordinates ...", Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
        }
    }

    protected void stopLocationUpdates() {
        Log.e(LOG, "###### stopLocationUpdates - " + new Date().toString());
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        Log.e(LOG, "####### onLocationChanged, accuracy: " + location.getAccuracy());
        this.location = location;
        if (location.getAccuracy() <= ACCURACY_LIMIT) {
            stopLocationUpdates();
            snackbar.dismiss();
            observationListFragment.setLocation(
                    location.getLatitude(),location.getLongitude());

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
//        Log.w(LOG, "################ onStart .... connecting mGoogleApiClient ");
//        mGoogleApiClient.connect();

    }

    @Override
    protected void onStop() {
        Log.e(LOG, "########## onStop");
        try {
            mGoogleApiClient.disconnect();
        } catch (Exception e) {
            Log.e(LOG, "Failed to Stop something", e);
        }
        super.onStop();
    }

    static final String LOG = ObservationsListActivity.class.getSimpleName();
}
