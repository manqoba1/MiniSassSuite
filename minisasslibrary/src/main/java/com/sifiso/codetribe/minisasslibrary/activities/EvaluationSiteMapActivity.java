package com.sifiso.codetribe.minisasslibrary.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.maps.android.MarkerManager;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.ui.IconGenerator;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverPartDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverPointDTO;
import com.sifiso.codetribe.minisasslibrary.util.MapItem;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EvaluationSiteMapActivity extends AppCompatActivity
        implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    GoogleMap googleMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest locationRequest;


    Location location;
    Context mCtx;
    List<Marker> markers = new ArrayList<Marker>();
    static final String LOG = EvaluationSiteMapActivity.class.getSimpleName();
    boolean mResolvingError;
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    RiverDTO river;
    boolean pointsHaveBeenSet;
    View siteEditor;
    TextView txtLat, txtLng, txtCount;
    Button btnSave;
    static final Locale loc = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_map);
        mCtx = EvaluationSiteMapActivity.this;

        String path = getIntent().getStringExtra("path");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        setFields();
        setRiver(path);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap gm) {
                googleMap = gm;
                Log.e(LOG, "GoogleMap onMapReady");
                setGoogleMap();
                if (river != null) {
                    setRiverPoints();
                }
            }
        });


    }

    private void setFields() {
        siteEditor = findViewById(R.id.mainLayout);
        siteEditor.setVisibility(View.GONE);
        txtCount = (TextView) findViewById(R.id.evaluations);
        txtLat = (TextView) findViewById(R.id.latitude);
        txtLng = (TextView) findViewById(R.id.longitude);
        btnSave = (Button) findViewById(R.id.btnSave);
        iconDelete = (ImageView) findViewById(R.id.delete);
        iconDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo showDialog
                AlertDialog.Builder d = new AlertDialog.Builder(EvaluationSiteMapActivity.this);
                d.setTitle("Remove Observation Site")
                        .setMessage("Do you really want to delete this Observation site?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendSiteDeletion();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Util.collapse(siteEditor, 500, null);
                            }
                        })
                        .show();
            }
        });
    }

    private void setRiver(String path) {
        new FileTask().execute(path);
    }

    private class FileTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            try {
                String path = params[0];
                File file = new File(path);
                String json = FileUtils.readFileToString(file);
                Gson g = new Gson();
                river = g.fromJson(json, RiverDTO.class);
                Log.d(LOG, "River retrieved from file, has RiverParts: " + river.getRiverpartList().size());

            } catch (IOException e) {
                e.printStackTrace();
                return 9;
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            Log.w(LOG, "Done getting river data from file: " + river.getRiverName());
            if (result == 0) {
                getSupportActionBar().setTitle("");
                Util.setCustomActionBar(getApplicationContext(),
                        getSupportActionBar(),
                        river.getRiverName(), "River Map Details",
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_launcher), null);
                if (googleMap != null) {
                    setRiverPoints();
                }
            }
        }
    }

    EvaluationSiteDTO selectedSite;
    private List<RiverPointDTO> riverPoints = new ArrayList<>();
    private List<RiverPointDTO> selectedRiverPoints = new ArrayList<>();
    private List<Marker> riverMarkers = new ArrayList<>();
    private ImageView iconDelete;

    private ClusterManager<MapItem> clusterManager;

    private void setRiverPoints() {
        googleMap.clear();
        markers.clear();
        riverMarkers.clear();
        selectedRiverPoints.clear();
        addDeviceLocationMarker();

        MarkerManager mm = new MarkerManager(googleMap);
        clusterManager = new ClusterManager<>(getApplicationContext(), googleMap, mm);

        googleMap.setOnCameraChangeListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);


        BitmapDescriptor nearestIcon = BitmapDescriptorFactory.fromResource(R.drawable.dot_green);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.dot_red);
        if (!river.getRiverpartList().isEmpty()) {
            for (RiverPartDTO rp : river.getRiverpartList()) {
                riverPoints.addAll(rp.getRiverpointList());
            }
            Collections.sort(riverPoints);
            for (int i = 0; i < 120; i++) {
                if (i < riverPoints.size()) {
                    selectedRiverPoints.add(riverPoints.get(i));
                }


            }

            int index = 0;
            for (RiverPointDTO p : selectedRiverPoints) {
                MarkerOptions markerOptions;
                if (index == 0) {
                    markerOptions = new MarkerOptions()
                            .position(new LatLng(p.getLatitude(), p.getLongitude()))
                            .icon(nearestIcon);
                    index = 1;
                } else {
                    markerOptions = new MarkerOptions()
                            .position(new LatLng(p.getLatitude(), p.getLongitude()))
                            .icon(icon);
                }

                final Marker m = googleMap.addMarker(markerOptions);
                riverMarkers.add(m);
            }

        }
        setEvaluationSiteMarkers();

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int index = 0;
                boolean isFound = false;
                for (Marker m : markers) {
                    if (m.equals(marker)) {
                        isFound = true;
                        break;
                    }
                }
                if (isFound) {
                    selectedSite = river.getEvaluationsiteList().get(index);
                    Log.e(LOG, "Site has been selected: " + selectedSite.getRiverName());
                    txtCount.setText("" + selectedSite.getEvaluationList().size());
                    txtLng.setText("" + selectedSite.getLatitude());
                    txtLat.setText("" + selectedSite.getLongitude());
                    Util.expand(siteEditor, 1000, null);
                    btnSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendSiteConfirmation();
                        }
                    });
                } else {
                    isFound = false;
                    for (Marker m : riverMarkers) {
                        if (m.equals(marker)) {
                            isFound = true;
                            break;
                        }
                    }
                    if (isFound) {
                        showNewSiteDialog();
                    }
                }
                return true;
            }
        });
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                //googleMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
                //ensure that all markers in bounds
                if (riverMarkers.size() > 2) {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (Marker marker : riverMarkers) {
                        builder.include(marker.getPosition());
                    }
                    LatLngBounds bounds = builder.build();
                    int padding = 100; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    googleMap.animateCamera(cu);
                } else {
                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(riverMarkers.get(0).getPosition(), 15);
                    googleMap.moveCamera(cu);
                }
            }
        });
    }

    private void showNewSiteDialog() {
        AlertDialog.Builder d = new AlertDialog.Builder(this);
        d.setTitle("New Observation Site")
                .setMessage("Do you want to set up an Observation site here?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendNewSite();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void sendNewSite() {
        Log.w(LOG, "Sending new site data to server");
    }

    private void setEvaluationSiteMarkers() {


        if (!river.getEvaluationsiteList().isEmpty()) {
            BitmapDescriptor desc = BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher);
            for (EvaluationSiteDTO es : river.getEvaluationsiteList()) {

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(es.getLatitude(), es.getLongitude()))
                        .icon(desc)
                        .title(es.getRiverName())
                        .snippet("");

                final Marker m = googleMap.addMarker(markerOptions);
                markers.add(m);

            }

        }
        pointsHaveBeenSet = true;


    }

    private void setGoogleMap() {
        try {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.setBuildingsEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);

            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    Log.w(LOG, "********* onMapClick");
                    location.setLatitude(latLng.latitude);
                    location.setLongitude(latLng.longitude);

                }
            });
//            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                @Override
//                public boolean onMarkerClick(Marker marker) {
//                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15f);
//                    googleMap.moveCamera(cu);
//                    return true;
//                }
//            });

        } catch (Exception e) {
            Log.e(LOG, "", e);
        }
    }

    private void sendSiteConfirmation() {
        Log.e(LOG, "############### sendSiteConfirmation");
        Util.collapse(siteEditor, 500, null);
    }

    private void sendSiteDeletion() {
        Log.w(LOG, "############### sendSiteDeletion");
        Util.collapse(siteEditor, 500, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // setUpMapIfNeeded();
    }

    private void startDirectionsMap(double lat, double lng) {
        Log.i(LOG, "startDirectionsMap ..........");
        String url = "http://maps.google.com/maps?saddr="
                + location.getLatitude() + "," + location.getLongitude()
                + "&daddr=" + lat + "," + lng + "&mode=driving";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    static final int ACCURACY_LIMIT = 50;

    @Override
    public void onLocationChanged(Location location) {
        Log.e(LOG, "####### onLocationChanged, accuracy: " + location.getAccuracy());
        this.location = location;
        if (location.getAccuracy() <= ACCURACY_LIMIT) {
            stopLocationUpdates();

            setRiverPoints();
        }

    }
    private void addDeviceLocationMarker() {
        Log.w(LOG,"Adding device location marker to map");
        IconGenerator gen = new IconGenerator(getApplicationContext());
        gen.setColor(ContextCompat.getColor(getApplicationContext(),R.color.green_300));
        Bitmap bm = gen.makeIcon("You are here");
        BitmapDescriptor desc = BitmapDescriptorFactory.fromBitmap(bm);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .icon(desc)
                .title("You are here")
                .snippet("");

        if (googleMap != null) {
            final Marker m = googleMap.addMarker(markerOptions);
            markers.add(m);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(LOG, "################ onStart .... connect API and location clients ");
        mGoogleApiClient.connect();
        if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
            Log.e(LOG, "################ onStart .... connect API and location clients {0} " + mGoogleApiClient.isConnecting());
        }

    }

    @Override
    protected void onStop() {
        try {
            mGoogleApiClient.disconnect();
        } catch (Exception e) {
            Log.e(LOG, "Failed to Stop something", e);
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(LOG, "+++  onConnected() -  requestLocationUpdates ...");
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            } else {
                addDeviceLocationMarker();

            }
            Log.w(LOG, "## requesting location ....lastLocation: "
                    + location.getLatitude() + " "
                    + location.getLongitude() + " acc: "
                    + location.getAccuracy());
        }
    }

    boolean mRequestingLocationUpdates;

    protected void startLocationUpdates() {
        Log.w(LOG, "###### startLocationUpdates: " + new Date().toString());
        if (mGoogleApiClient.isConnected()) {
            mRequestingLocationUpdates = true;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, locationRequest, this);
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

            mResolvingError = true;
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    private void makeBubbles() {

    }
}
