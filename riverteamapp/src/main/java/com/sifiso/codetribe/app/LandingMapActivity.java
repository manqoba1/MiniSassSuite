package com.sifiso.codetribe.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
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
import com.google.maps.android.ui.IconGenerator;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.activities.AboutActivity;
import com.sifiso.codetribe.minisasslibrary.activities.ProfileActivity;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverPartDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverPointDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.toolbox.BaseVolley;
import com.sifiso.codetribe.minisasslibrary.util.CacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.ErrorUtil;
import com.sifiso.codetribe.minisasslibrary.util.PDFUtil;
import com.sifiso.codetribe.minisasslibrary.util.SharedUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LandingMapActivity extends AppCompatActivity implements
        LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    GoogleMap googleMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest locationRequest;


    Location location;
    Context mCtx;
    List<Marker> markers = new ArrayList<Marker>();
    static final String LOG = LandingMapActivity.class.getSimpleName();
    boolean mResolvingError;
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    List<RiverDTO> riverList;
    SeekBar seekBar;
    ImageView searchIcon;
    TextView txtProgress;
    static final Locale loc = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_map);
        mCtx = LandingMapActivity.this;

        String path = getIntent().getStringExtra("path");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        setFields();
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap gm) {
                googleMap = gm;
                setGoogleMap();

            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();

        NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);

        TeamMemberDTO t = SharedUtil.getTeamMember(getApplicationContext());
        TextView title = (TextView) nav.findViewById(R.id.NAV_title);
        TextView subtitle = (TextView) nav.findViewById(R.id.NAV_subtitle);
        String name = t.getFirstName() + " " + t.getLastName();

        if (title != null) {
            String team = null;
            if (t.getTeam().getTeamName() != null) {
                team = t.getTeam().getTeamName();
            }
            title.setText(name);
            if (team == null) {
                subtitle.setVisibility(View.GONE);
            } else {
                subtitle.setText(team);
            }
        }

        Util.setCustomActionBar(getApplicationContext(),
                getSupportActionBar(), t.getFirstName() + " " + t.getLastName(), t.getTeamName(),
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_launcher), null);
    }

    private void setFields() {

        txtProgress = (TextView) findViewById(R.id.progress);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        searchIcon = (ImageView) findViewById(R.id.refresh);

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationUpdates();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtProgress.setText("" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    EvaluationSiteDTO selectedSite;
    private List<RiverPointDTO> riverPoints = new ArrayList<>();
    private List<RiverPointDTO> selectedRiverPoints = new ArrayList<>();
    private List<Marker> riverMarkers = new ArrayList<>();
    private ImageView iconDelete;


    private void setRiverPoints(RiverDTO river) {
        googleMap.clear();

        addDeviceLocationMarker();
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
        setEvaluationSiteMarkers(river);


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

    private void setEvaluationSiteMarkers(RiverDTO river) {


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

            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    Log.w(LOG, "********* onMapClick");
                    location.setLatitude(latLng.latitude);
                    location.setLongitude(latLng.longitude);

                }
            });
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15f);
                    googleMap.moveCamera(cu);
                    return true;
                }
            });

        } catch (Exception e) {
            Log.e(LOG, "", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
            getRiversAroundMe();
        }

    }
    private void addDeviceLocationMarker() {
        Log.w(LOG,"Adding device location marker to map");
        IconGenerator gen = new IconGenerator(getApplicationContext());
        gen.setColor(ContextCompat.getColor(getApplicationContext(),R.color.amber_200));
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
                getRiversAroundMe();
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
            snackbar = Snackbar.make(seekBar, "Getting device GPS location before search", Snackbar.LENGTH_INDEFINITE);
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

    boolean isBusy;
    Snackbar snackbar;

    private void getRiversAroundMe() {
        if (isBusy) {
            Log.e(LOG, "### getRiversAroundMe is BUSY!!!");
            return;
        }

        Log.d(LOG, "############### getRiversAroundMe");
        snackbar = Snackbar.make(seekBar, "Refreshing river data around you ....", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.LIST_DATA_WITH_RADIUS_RIVERS);
        w.setLatitude(location.getLatitude());
        w.setLongitude(location.getLongitude());
        w.setRadius(seekBar.getProgress());

        isBusy = true;

        //setRefreshActionButtonState(true);
        BaseVolley.getRemoteData(Statics.SERVLET_ENDPOINT, w,
                getApplicationContext(), new BaseVolley.BohaVolleyListener() {
                    @Override
                    public void onResponseReceived(ResponseDTO r) {
                        isBusy = false;
                        //setRefreshActionButtonState(false);
                        snackbar.dismiss();
                        Snackbar.make(seekBar, "Found " + r.getRiverList().size() + " rivers around you ....", Snackbar.LENGTH_LONG).show();
                        if (!ErrorUtil.checkServerError(getApplicationContext(), r)) {
                            return;
                        }
                        Log.e(LOG, "### getRiversAroundMe, found: " + r.getRiverList().size());
                        if (location != null) {
                            for (RiverDTO river : r.getRiverList()) {
                                river.calculateDistance(location.getLatitude(), location.getLongitude());
                            }
                            Collections.sort(r.getRiverList());
                        }
                        riverList = r.getRiverList();
                        if (!riverList.isEmpty())
                            setRiverPoints(riverList.get(0));
                        CacheUtil.cacheData(getApplicationContext(), r, CacheUtil.CACHE_DATA, new CacheUtil.CacheUtilListener() {
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
                    }

                    @Override
                    public void onVolleyError(VolleyError error) {
                        isBusy = false;
                    }

                    @Override
                    public void onError(final String message) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                isBusy = false;
                                snackbar.dismiss();

                                if (!message.equals(null)) {

                                    Log.e(LOG, message);
                                    Util.showErrorToast(getApplicationContext(), message);

                                }
                            }
                        });
                    }
                });


    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.action_observe:
                Intent m1 = new Intent(getApplicationContext(), MainPagerActivity.class);
                startActivity(m1);
                return true;
            case R.id.action_site_edit:
                Intent m2 = new Intent(getApplicationContext(), CreateSiteActivity.class);
                startActivity(m2);
                return true;
            case R.id.log_out:
                SharedUtil.clearTeam(getApplicationContext());
                intent = new Intent(this, SignActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.about_us:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.profile:
                intent = new Intent(this, ProfileActivity.class);
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
                    PDFUtil.downloadPDF(getApplicationContext(),
                            "http://www.minisass.org/media/filer_public/2013/06/28/1111_minisass_dichotomous_key_nov_2011.pdf", fileName,
                            new PDFUtil.PDFListener() {
                                @Override
                                public void onDownloaded(final File pdfFile) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                            startActivity(intent);
                                        }
                                    });

                                }

                                @Override
                                public void onError() {
                                    Util.showErrorToast(getApplicationContext(), "Sorry. Unable to download file");
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

        return true;
    }
}
