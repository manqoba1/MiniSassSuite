package com.sifiso.codetribe.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.ui.IconGenerator;
import com.sifiso.codetribe.minisasslibrary.activities.AboutActivity;
import com.sifiso.codetribe.minisasslibrary.activities.InsectPickerActivity;
import com.sifiso.codetribe.minisasslibrary.activities.MSApp;
import com.sifiso.codetribe.minisasslibrary.activities.ObservationsListActivity;
import com.sifiso.codetribe.minisasslibrary.activities.ProfileActivity;
import com.sifiso.codetribe.minisasslibrary.dialogs.RiverSearchDialog;
import com.sifiso.codetribe.minisasslibrary.dialogs.SiteEditorDialog;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverPartDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverPointDTO;
import com.sifiso.codetribe.minisasslibrary.util.DistanceFromRiver;
import com.sifiso.codetribe.minisasslibrary.util.IconizedMenu;
import com.sifiso.codetribe.minisasslibrary.util.PDFUtil;
import com.sifiso.codetribe.minisasslibrary.util.RiverDataWorker;
import com.sifiso.codetribe.minisasslibrary.util.SharedUtil;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainDrawerActivity extends AppCompatActivity
        implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    static final String LOG = MainDrawerActivity.class.getSimpleName();
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    GoogleApiClient mGoogleApiClient;
    LocationRequest locationRequest;
    private GoogleMap mgGoogleMap;
    EvaluationSiteDTO selectedSite;
    private List<RiverPointDTO> riverPoints = new ArrayList<>();
    private List<RiverPointDTO> selectedRiverPoints = new ArrayList<>();
    private List<Marker> riverMarkers = new ArrayList<>();
    private Marker deviceMarker;
    private Location location;
    static final int ACCURACY_LIMIT = 100;
    Context ctx;
    MSApp app;

    RiverDTO river;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);
        getSupportActionBar().setTitle("heita!");
        handle = findViewById(R.id.handle);
        ctx = getApplicationContext();
        app = (MSApp)getApplication();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        setNavigationView();
        drawerLayout.openDrawer(GravityCompat.START);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        getSupportActionBar().setTitle("");
        Util.setCustomActionBar(ctx,
                getSupportActionBar(), "MiniSASS", "Landing Page",
                ContextCompat.getDrawable(ctx, R.drawable.ic_launcher), null);
        IntentFilter m = new IntentFilter(RiverDataWorker.BROADCAST_POINTS_RETRIEVED);
        LocalBroadcastManager.getInstance(ctx).registerReceiver(new PointsBroadcastReceiver(), m);
    }

    private void setNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Log.d(LOG, "-------- nav menu clicked: " + item.getTitle());
                int id = item.getItemId();
                Intent intent;
                switch (id) {

                    case R.id.log_out:
                        SharedUtil.clearTeam(ctx);
                        intent = new Intent(ctx, SignActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                    case R.id.about_us:
                        intent = new Intent(ctx, AboutActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.profile:
                        intent = new Intent(ctx, ProfileActivity.class);
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
                            PDFUtil.downloadPDF(ctx,
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

                return true;
            }
        });
    }


    static final int NUMBER_OF_RIVER_MARKERS = 300;
    View handle;

    private void setRiverPoints() {
        Util.setCustomActionBar(ctx,
                getSupportActionBar(), "MiniSASS", river.getRiverName(),
                ContextCompat.getDrawable(ctx, R.drawable.ic_launcher), null);


        mgGoogleMap.clear();
        for (Marker m : riverMarkers) {
            m.remove();
        }

        riverMarkers = new ArrayList<>();
        riverHashMap.clear();
        for (Bag b : bags) {
            b.marker.remove();
        }
        bags = new ArrayList<>();
        selectedRiverPoints = new ArrayList<>();
        selectedSite = null;
        riverPoints.clear();

        addDeviceLocationMarker();
        setEvaluationSiteMarkers();
        if (siteHashMap.keySet().size() > 2) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : siteHashMap.keySet()) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();
            int padding = 100; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mgGoogleMap.animateCamera(cu, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {

                }

                @Override
                public void onCancel() {

                }
            });


        } else {
            if (siteHashMap.keySet().size() > 0) {
                for (Marker m : siteHashMap.keySet()) {
                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(m.getPosition(), 15);
                    mgGoogleMap.moveCamera(cu);
                    break;
                }
            }
        }

        setRiverMarkers();


    }

    private void setRiverMarkers() {
        //get points from cache and set markers on map
        drawerLayout.closeDrawer(GravityCompat.START);
        Log.d(LOG, "##### ----------- setRiverMarkers -- starting cache search for river points: " + river.getRiverName());
        RiverDataWorker.getRiverByID((MSApp) getApplication(), river.getRiverID(), new RiverDataWorker.RiverCacheListener() {
                    @Override
                    public void onRiversCached() {

                    }

                    @Override
                    public void onRiversFound(List<RiverDTO> riverList) {
                        if (!riverList.isEmpty()) {
                            river = riverList.get(0);
                            Log.e(LOG, "..................about to set river via processBounds() points for: " + river.getRiverName());
                            processBounds();
                        } else {
                            return;
                        }


                    }

                    @Override
                    public void onError(String message) {

                    }
                }

        );
    }

    private void processBounds() {


        BitmapDescriptor nearestIcon = BitmapDescriptorFactory
                .fromResource(R.drawable.dot_green);
        BitmapDescriptor icon = BitmapDescriptorFactory
                .fromResource(R.drawable.dot_red_tiny);
        VisibleRegion vr = mgGoogleMap.getProjection().getVisibleRegion();
        LatLng sw = vr.latLngBounds.southwest;
        LatLng ne = vr.latLngBounds.northeast;
        LatLngBounds latLngBounds = new LatLngBounds(sw, ne);


        riverPoints.clear();
        for (RiverPartDTO part : river.getRiverpartList()) {
            for (RiverPointDTO point : part.getRiverpointList()) {
                point.calculateDistance(location.getLatitude(), location.getLongitude());
            }
            riverPoints.addAll(part.getRiverpointList());
        }

        Collections.sort(riverPoints);
        selectedRiverPoints = riverPoints;
        Log.w(LOG, "************ selectedRiverPoints before skip algorithm: " + selectedRiverPoints.size());
        List<RiverPointDTO> xList = new ArrayList<>(selectedRiverPoints.size() / 2);
        int counter = 0;
        if (selectedRiverPoints.size() > 3000) {
            //take every eighth point
            for (RiverPointDTO p : selectedRiverPoints) {
                if (counter == 0) {
                    xList.add(p);
                    counter++;
                    continue;
                }
                int rem = counter % 15;
                if (rem == 0) {
                    xList.add(p);
                }
                counter++;
            }
        }
        if (selectedRiverPoints.size() < 3001 && selectedRiverPoints.size() > 1500) {
            //take every eighth point
            for (RiverPointDTO p : selectedRiverPoints) {
                if (counter == 0) {
                    xList.add(p);
                    counter++;
                    continue;
                }
                int rem = counter % 10;
                if (rem == 0) {
                    xList.add(p);
                }
                counter++;
            }
        }
        if (selectedRiverPoints.size() < 1501 && selectedRiverPoints.size() > 1000) {
            //take every eighth point
            for (RiverPointDTO p : selectedRiverPoints) {
                if (counter == 0) {
                    xList.add(p);
                    counter++;
                    continue;
                }
                int rem = counter % 7;
                if (rem == 0) {
                    xList.add(p);
                }
                counter++;
            }
        }
        if (selectedRiverPoints.size() < 1001 && selectedRiverPoints.size() > 300) {
            //take every eighth point
            for (RiverPointDTO p : selectedRiverPoints) {
                if (counter == 0) {
                    xList.add(p);
                    counter++;
                    continue;
                }
                int rem = counter % 4;
                if (rem == 0) {
                    xList.add(p);
                }
                counter++;
            }
        }
        if (selectedRiverPoints.size() < 301 && selectedRiverPoints.size() > 160) {
            for (RiverPointDTO p : selectedRiverPoints) {
                if (counter == 0) {
                    xList.add(p);
                    counter++;
                    continue;
                }
                int rem = counter % 2;
                if (rem == 0) {
                    xList.add(p);
                }
                counter++;
            }
        }
        if (selectedRiverPoints.size() < 160 && selectedRiverPoints.size() > 0) {
            for (RiverPointDTO p : selectedRiverPoints) {
                xList.add(p);
                counter++;
            }
        }

        selectedRiverPoints = xList;
        Log.e(LOG, "......setting selected markers: " + selectedRiverPoints.size());

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

            final Marker m = mgGoogleMap.addMarker(markerOptions);
            riverMarkers.add(m);
            riverHashMap.put(m, p);
        }


        if (selectedRiverPoints.isEmpty()) {
            Log.w(LOG, "setOnMapLoadedCallback onMapLoaded - empty riverMarkers");
            return;
        }

        if (riverMarkers.size() > 2) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : riverMarkers) {
                builder.include(marker.getPosition());
            }
            for (Marker marker : siteHashMap.keySet()) {
                builder.include(marker.getPosition());
            }
            LatLngBounds boundsx = builder.build();
            int padding = 50; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(boundsx, padding);
            mgGoogleMap.animateCamera(cu);


        } else {
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(riverMarkers.get(0).getPosition(), 15);
            mgGoogleMap.animateCamera(cu);
        }

    }

    private class PointsBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(LOG, "%%%%%%%%%%%%%%%%%%%%%% PointsBroadcastReceiver onReceive");
            setRiverMarkers();
        }
    }

    private void showNewSiteDialog(final LatLng latLng) {
        for (EvaluationSiteDTO s : river.getEvaluationsiteList()) {
            s.calculateDistance(latLng.latitude, latLng.longitude);
        }
        Collections.sort(river.getEvaluationsiteList());
        boolean foundCloseOne = false;
        for (EvaluationSiteDTO s : river.getEvaluationsiteList()) {
            if (s.getDistanceFromMe() < 100f) {
                foundCloseOne = true;
                break;
            }
        }
        if (foundCloseOne) {
            Log.e(LOG, "------------- foundCloseOne, ignoring long click");
            return;
        }
        final AlertDialog.Builder d = new AlertDialog.Builder(this);
        d.setTitle("New Observation Site")
                .setMessage("Do you want to set up an Observation site here?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedSite = new EvaluationSiteDTO();
                        selectedSite.setLatitude(latLng.latitude);
                        selectedSite.setLongitude(latLng.longitude);
                        selectedSite.setRiverID(river.getRiverID());
                        doAddSiteDialog();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void doAddSiteDialog() {
        showEditorDialog(SiteEditorDialog.ADD_SITE);
    }

    private void createSiteWhereYouAreDialog() {
        AlertDialog.Builder d = new AlertDialog.Builder(this);
        d.setTitle("New Observation Site")
                .setMessage("Do you want to set up an Observation site where you are now?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedSite = new EvaluationSiteDTO();
                        selectedSite.setLatitude(location.getLatitude());
                        selectedSite.setLongitude(location.getLongitude());
                        selectedSite.setRiverID(river.getRiverID());
                        showEditorDialog(SiteEditorDialog.ADD_SITE);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }


    private void addNewSitemarker() {
        IconGenerator gen = new IconGenerator(ctx);
        gen.setColor(ContextCompat.getColor(ctx, R.color.light_blue_300));
        Bitmap bm = gen.makeIcon("I am a NEW site!");

        BitmapDescriptor desc = BitmapDescriptorFactory.fromBitmap(bm);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(selectedSite.getLatitude(), selectedSite.getLongitude()))
                .icon(desc)
                .title("New Site")
                .snippet("");

        if (mgGoogleMap != null) {
            final Marker m = mgGoogleMap.addMarker(markerOptions);
            Bag bag = new Bag(m, null);
            bags.add(bag);
        }
    }

    private HashMap<Marker, EvaluationSiteDTO> siteHashMap = new HashMap<>();
    private HashMap<Marker, RiverPointDTO> riverHashMap = new HashMap<>();

    private void setEvaluationSiteMarkers() {
        siteHashMap.clear();
        EvaluationSiteDTO s = findSiteWhereYouAre();
        if (s != null) {
            deviceMarker.remove();
        }
        IconGenerator gen = new IconGenerator(ctx);
        for (RiverDTO river : rivers) {
            Log.d(LOG, "setEvaluationSiteMarkers, River: " + river.getRiverName() + " sites: " + river.getEvaluationsiteList().size());
        }
        if (!river.getEvaluationsiteList().isEmpty()) {
            Log.i(LOG, "################ River: " + river.getRiverName() + " sites: " + river.getEvaluationsiteList().size());
            int index = 0;
            for (final EvaluationSiteDTO es : river.getEvaluationsiteList()) {
                View v = getLayoutInflater().inflate(R.layout.map_site_icon, null);
                TextView txt = (TextView) v.findViewById(R.id.badge);
                TextView here = (TextView) v.findViewById(R.id.here);
                txt.setText("" + es.getEvaluationList().size());
                here.setVisibility(View.GONE);
                gen.setContentView(v);
                if (s != null) {
                    if (s.getEvaluationSiteID().intValue() == es.getEvaluationSiteID().intValue()) {
                        here.setVisibility(View.VISIBLE);
                        gen.setColor(ContextCompat.getColor(ctx, R.color.amber_400));
                    } else {
                        gen.setColor(ContextCompat.getColor(ctx, R.color.white));
                    }
                } else {
                    gen.setColor(ContextCompat.getColor(ctx, R.color.white));
                }
                if (es.getSiteName() != null) {
                    here.setText(es.getSiteName());
                    here.setVisibility(View.VISIBLE);
                }

                Bitmap bm = gen.makeIcon();
                BitmapDescriptor desc = BitmapDescriptorFactory
                        .fromBitmap(bm);

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(es.getLatitude(), es.getLongitude()))
                        .icon(desc);

                final Marker m = mgGoogleMap.addMarker(markerOptions);
                siteHashMap.put(m, es);
                index++;
                Log.d(LOG, "================> Next index up is: " + index + " pos: " + m.getPosition());


            }

        }
        Log.e(LOG, "+++++++++++++++ siteHashMap has: " + siteHashMap.keySet().size());

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mgGoogleMap = googleMap;
        setGoogleMap();
    }

    List<Bag> bags = new ArrayList<>();

    class Bag {
        EvaluationSiteDTO site;
        Marker marker;

        public Bag(Marker marker, EvaluationSiteDTO site) {
            this.marker = marker;
            this.site = site;
        }

    }

    private void setGoogleMap() {
        try {
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mgGoogleMap.setTrafficEnabled(true);
            mgGoogleMap.getUiSettings().setCompassEnabled(true);
            mgGoogleMap.getUiSettings().setZoomControlsEnabled(true);
            mgGoogleMap.getUiSettings().setZoomGesturesEnabled(true);

            mgGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Log.d(LOG, "############### - Marker onMarkerClick, marker: title: " + marker.getTitle()
                            + " snip: " + marker.getSnippet()
                            + " latLng: " + marker.getPosition());

                    RiverPointDTO rp = riverHashMap.get(marker);
                    if (rp != null) {
                        Log.w(LOG, "River Marker clicked - go to showNewSiteDialog");
                        showNewSiteDialog(marker.getPosition());
                        return true;
                    }
                    selectedSite = siteHashMap.get(marker);
                    if (selectedSite != null) {
                        Log.e(LOG, "Marker clicked - selected: " + selectedSite.getSiteName() + " id: " + selectedSite.getEvaluationSiteID()
                                + " observations: " + selectedSite.getEvaluationList().size());
                        showPopupMenu();
                        return true;
                    } else {
                        Log.d(LOG, "You are here Marker clicked - starting  createSiteWhereYouAreDialog");
                        createSiteWhereYouAreDialog();
                        return true;
                    }


                }
            });
            mgGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    //ensure that all markers in bounds
                    if (riverMarkers.isEmpty()) {
                        Log.w(LOG, "setOnMapLoadedCallback onMapLoaded - empty riverMarkers");
                        return;
                    }
                    if (riverMarkers.size() > 2) {
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (Marker marker : riverMarkers) {
                            builder.include(marker.getPosition());
                        }
                        LatLngBounds bounds = builder.build();
                        int padding = 100; // offset from edges of the map in pixels
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        mgGoogleMap.animateCamera(cu);
                    } else {
                        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(riverMarkers.get(0).getPosition(), 12f);
                        mgGoogleMap.moveCamera(cu);
                    }

                }
            });

            mgGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    Log.d(LOG, "mgGoogleMap onMapLongClick: " + latLng);
                    //check if near river
                    List<DistanceFromRiver> mlist = new ArrayList<>(riverPoints.size());
                    for (RiverPointDTO p : riverPoints) {
                        mlist.add(new DistanceFromRiver(p.getLatitude(), p.getLongitude(), latLng));
                    }
                    Collections.sort(mlist);

                    DistanceFromRiver dfr = mlist.get(0);
                    Log.d(LOG, "-------------------: This point is about " + df.format(dfr.getDistance()) + "  metres from a river: ");
                    if (dfr.getDistance() < 1000) {
                        showNewSiteDialog(latLng);
                    } else {
                        snackbar = Snackbar.make(handle, "This point is about " +
                                df.format(dfr.getDistance()) + " metres from a river", Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction("Cool", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        });
                        snackbar.setActionTextColor(
                                ContextCompat.getColor(ctx,
                                        R.color.light_blue_200));
                        snackbar.show();
                    }


                }
            });
//

        } catch (Exception e) {
            Log.e(LOG, "", e);
        }
    }


    static final DecimalFormat df = new DecimalFormat("###,###,###,###");

    private void showPopupMenu() {
        Log.w(LOG, "showPopupMenu selectedSite id: " + selectedSite.getEvaluationSiteID()
                + " obs: " + selectedSite.getEvaluationList().size());

        String siteName = "Site Unnamed";
        if (selectedSite.getSiteName() != null) {
            siteName = selectedSite.getSiteName();
        }
        Util.setCustomActionBar(ctx,
                getSupportActionBar(),river.getRiverName(),
                siteName, ContextCompat.getDrawable(
                        ctx,R.drawable.ic_launcher),null);

        IconizedMenu popup = new IconizedMenu(this, handle);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_on_map, popup.getMenu());

        popup.setOnMenuItemClickListener(new IconizedMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.ic_action_observe:
                        if (selectedSite == null) {
                            throw new RuntimeException("SelectedSite is fucking null, why?");
                        }
                        Log.e(LOG, "..............starting InsectPickerActivity, selectedSite "
                                + selectedSite.getEvaluationSiteID() + " obs: "
                                + selectedSite.getEvaluationList().size());
                        Intent m = new Intent(ctx, InsectPickerActivity.class);
                        m.putExtra("site", selectedSite);
                        startActivity(m);
                        break;
                    case R.id.ic_action_list:
                        Intent m2 = new Intent(ctx, ObservationsListActivity.class);
                        m2.putExtra("site",selectedSite);
                        startActivity(m2);
                        break;
                    case R.id.ic_action_edit:
                        showEditorDialog(SiteEditorDialog.UPDATE_SITE);
                        break;
                    case R.id.ic_action_directions:
                        getDirections(selectedSite.getLatitude(), selectedSite.getLongitude());
                        break;
                    case R.id.ic_action_remove:
                        showEditorDialog(SiteEditorDialog.DELETE_SITE);
                        break;
                    default:
                        throw new RuntimeException("Don't fuck with this, do stuff!");


                }
                return true;
            }


        });
        popup.show();

    }

    private void showEditorDialog(int type) {
        SiteEditorDialog d = new SiteEditorDialog();
        d.setContext(ctx);
        d.setType(type);
        d.setSite(selectedSite);
        d.setRiver(river);
        d.setListener(new SiteEditorDialog.SiteEditorListener() {
            @Override
            public void onSiteAdded(EvaluationSiteDTO site) {
                selectedSite = site;
                addNewSitemarker();
                getRiversAroundMe();

            }

            @Override
            public void onSiteUpdated() {
                getRiversAroundMe();
            }

            @Override
            public void onSiteRemoved(EvaluationSiteDTO site) {
                Log.e(LOG, "******** site removed, refreshing data: "
                        + site.getSiteName());
                refreshRiver(site);
            }

            @Override
            public void onError(String message) {
                snackbar = Snackbar.make(handle, message, Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
                snackbar.setActionTextColor(ContextCompat.getColor(ctx, R.color.amber_600));
                snackbar.show();

            }
        });
        d.show(getSupportFragmentManager(), "EDITOR_DIALOG");
    }

    private void refreshRiver(EvaluationSiteDTO site) {
        RiverDataWorker.refreshRiver((MSApp) getApplication(),
                ctx, site.getRiverID(),
                new RiverDataWorker.RiverSearchListener() {
            @Override
            public void onResponse(List<RiverDTO> rivers) {
                if (!rivers.isEmpty()) {
                    river = rivers.get(0);
                    setRiverPoints();
                }
            }

            @Override
            public void onError(String message) {

            }
        });
    }
    private EvaluationSiteDTO findSiteWhereYouAre() {
        if (river.getEvaluationsiteList().isEmpty())
            return null;
        for (EvaluationSiteDTO es : river.getEvaluationsiteList()) {
            es.calculateDistance(location.getLatitude(), location.getLongitude());
        }
        Collections.sort(river.getEvaluationsiteList());
        EvaluationSiteDTO es = river.getEvaluationsiteList().get(0);
        Location loc1 = new Location(LocationManager.GPS_PROVIDER);
        loc1.setLatitude(es.getLatitude());
        loc1.setLongitude(es.getLongitude());

        Location loc2 = new Location(LocationManager.GPS_PROVIDER);
        loc2.setLatitude(location.getLatitude());
        loc2.setLongitude(location.getLongitude());

        float distanceFromMe = loc1.distanceTo(loc2);
        if (distanceFromMe < 30) {
            return river.getEvaluationsiteList().get(0);
        } else {
            return null;
        }

    }

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
            } else {
                addDeviceLocationMarker();
                getRiversAroundMe();
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

    Snackbar snackbar;
    List<RiverDTO> rivers;

    @Override
    public void onLocationChanged(Location location) {

        Log.e(LOG, "####### onLocationChanged, accuracy: " + location.getAccuracy());
        this.location = location;
        if (location.getAccuracy() <= ACCURACY_LIMIT) {
            stopLocationUpdates();
            snackbar.dismiss();
            addDeviceLocationMarker();
            getRiversAroundMe();

        }
    }

    RiverSearchDialog riverSearchDialog;

    private void startSearch() {
        if (riverSearchDialog == null) {
            riverSearchDialog = new RiverSearchDialog();
        } else {
            riverSearchDialog.setFirsTime(true);
            riverSearchDialog.show(getSupportFragmentManager(), "RIVERDIALOG");
            return;
        }
        riverSearchDialog.setLatitude(location.getLatitude());
        riverSearchDialog.setLongitude(location.getLongitude());
        riverSearchDialog.setRadius(15);
        if (rivers != null) {
            riverSearchDialog.setApp((MSApp) getApplication());
            riverSearchDialog.setRivers(rivers, false);
        }
        riverSearchDialog.setContext(ctx);
        riverSearchDialog.setAllowEnterTransitionOverlap(true);

        riverSearchDialog.setApp((MSApp) getApplication());
        riverSearchDialog.setRivers(rivers, false);
        riverSearchDialog.setCancelable(false);
        riverSearchDialog.setFirsTime(true);
        riverSearchDialog.setListener(new RiverSearchDialog.RiverSearchListener() {
            @Override
            public void onSearchComplete(List<RiverDTO> list) {
                riverSearchDialog.dismiss();
                if (!list.isEmpty()) {
                    rivers = list;
                }
            }

            @Override
            public void onError(String message) {

            }

            @Override
            public void onRiverSelected(RiverDTO r) {
                Log.d(LOG, "-------------------------------- River selected: "
                        + r.getRiverName() + " sites: " + r.getEvaluationsiteList().size());
                RiverDataWorker.getRiverByID((MSApp) getApplication(), r.getRiverID(), new RiverDataWorker.RiverCacheListener() {
                    @Override
                    public void onRiversCached() {

                    }

                    @Override
                    public void onRiversFound(List<RiverDTO> rivers) {
                        if (!rivers.isEmpty()) {
                            river = rivers.get(0);
                            for (EvaluationSiteDTO s : river.getEvaluationsiteList()) {
                                Log.d(LOG, river.getRiverName() + " -- Site: " + s.getSiteName() + " lat: "
                                        + s.getLatitude() + " lng: " + s.getLongitude());
                            }
                            setRiverPoints();
                        }

                    }

                    @Override
                    public void onError(String message) {

                    }
                });

            }

            @Override
            public void onEvaluationSiteSelected(EvaluationSiteDTO site) {
                Log.d(LOG, "-------------------------------- Site selected: " + site.getSiteName());
            }

            @Override
            public void onDirections(RiverDTO river) {

                List<DistanceFromRiver> mlist = new ArrayList<>(riverMarkers.size());
                for (RiverPointDTO p : riverPoints) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mlist.add(new DistanceFromRiver(p.getLatitude(), p.getLongitude(), latLng));
                }
                Collections.sort(mlist);
                DistanceFromRiver dfr = mlist.get(0);
                getDirections(dfr.getLatitude(), dfr.getLongitude());


            }

            @Override
            public void onDirections(EvaluationSiteDTO site) {
                getDirections(site.getLatitude(), site.getLongitude());
            }
        });

        riverSearchDialog.show(getSupportFragmentManager(), "RIVERDIALOG");

    }

    private void getDirections(double latitude, double longitude) {
        Log.i(LOG, "startDirectionsMap ..........");
        String url = "http://maps.google.com/maps?saddr="
                + location.getLatitude() + "," + location.getLongitude()
                + "&daddr=" + latitude + "," + longitude + "&mode=driving";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }


    private void addDeviceLocationMarker() {
        if (location == null) return;
        IconGenerator gen = new IconGenerator(ctx);
        View v = getLayoutInflater().inflate(R.layout.you_are_here, null);
        gen.setContentView(v);
        gen.setColor(ContextCompat.getColor(ctx, R.color.amber_200));
        Bitmap bm = gen.makeIcon();
        BitmapDescriptor desc = BitmapDescriptorFactory.fromBitmap(bm);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .icon(desc);

        if (mgGoogleMap != null) {
            deviceMarker = mgGoogleMap.addMarker(markerOptions);
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w(LOG, "################ onStart .... connecting mGoogleApiClient ");
        mGoogleApiClient.connect();

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


    Menu mMenu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_site, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.ic_action_refresh) {
            startSearch();
        }

        return super.onOptionsItemSelected(item);
    }

    boolean isBusy;
    static final int RADIUS = 10;

    
    private void getRiversAroundMe() {
        Log.d(LOG, "############### getRiversAroundMe");
        snackbar = Snackbar.make(handle, "Refreshing river data around you ...." +
                "may take a minute or two, depending on network", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();

        RiverDataWorker.getRiversAroundMe((MSApp) getApplication(), ctx,
                location.getLatitude(), location.getLongitude(), RADIUS,
                false, new RiverDataWorker.RiverSearchListener() {
                    @Override
                    public void onResponse(List<RiverDTO> riverList) {
                        isBusy = false;
                        snackbar.dismiss();
                        SharedUtil.saveRiverRefreshTime(ctx);
                        Snackbar.make(handle, "Found " + riverList.size()
                                + " riverList around you ....",
                                Snackbar.LENGTH_LONG).show();

                        Log.e(LOG, "### getRiversAroundMe, found: " + riverList.size());
                        rivers = riverList;

                        if (!riverList.isEmpty()) {
                            river = riverList.get(0);
                            setRiverPoints();
                        } else {
                            Util.showErrorToast(ctx, "No riverList found");
                            return;
                        }
                    }

                    @Override
                    public void onError(String message) {
                        snackbar.dismiss();
                        snackbar = Snackbar.make(handle, message, Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        });
                        snackbar.setActionTextColor(ContextCompat.getColor(ctx, R.color.amber_600));
                        snackbar.show();

                    }
                });

    }

    boolean cacheHasRivers;


    boolean backPressed = false;
    @Override
    public void onBackPressed() {
        if (!backPressed) {
            backPressed = true;
            Snackbar.make(handle,"Press back again to exit", Snackbar.LENGTH_SHORT).show();
            return;
        }
        finish();
    }

    //location gps etc .......
    private boolean checkSettings() {
        //
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean gpsEnabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkEnabled = service.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Log.i(LOG, "GPS enabled: " + gpsEnabled + " networkEnabled: " + networkEnabled);
        if (!gpsEnabled && !networkEnabled && !isLocationEnabled()) {
            showSettingDialog();
        } else {
            return true;
        }
        return true;
    }

    public void showSettingDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

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
            } else {
                return true;
            }

        }

        return false;
    }

}
