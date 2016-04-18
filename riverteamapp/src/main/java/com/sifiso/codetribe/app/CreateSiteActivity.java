package com.sifiso.codetribe.app;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
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
import com.sifiso.codetribe.minisasslibrary.activities.AboutActivity;
import com.sifiso.codetribe.minisasslibrary.activities.InsectPickerActivity;
import com.sifiso.codetribe.minisasslibrary.activities.ProfileActivity;
import com.sifiso.codetribe.minisasslibrary.dialogs.RiverSearchDialog;
import com.sifiso.codetribe.minisasslibrary.dialogs.SiteEditorDialog;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverPartDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverPointDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.toolbox.BaseVolley;
import com.sifiso.codetribe.minisasslibrary.util.CacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.DistanceFromRiver;
import com.sifiso.codetribe.minisasslibrary.util.IconizedMenu;
import com.sifiso.codetribe.minisasslibrary.util.PDFUtil;
import com.sifiso.codetribe.minisasslibrary.util.SharedUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class CreateSiteActivity extends AppCompatActivity
        implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mgGoogleMap;
    EvaluationSiteDTO selectedSite;
    private List<RiverPointDTO> riverPoints = new ArrayList<>();
    private List<RiverPointDTO> selectedRiverPoints = new ArrayList<>();
    private List<Marker> riverMarkers = new ArrayList<>();
    private ImageView iconDelete;

    static final String LOG = CreateSiteActivity.class.getSimpleName();
    RiverDTO river;
    boolean pointsHaveBeenSet;

    static final Locale loc = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm", loc);
    GoogleApiClient mGoogleApiClient;
    LocationRequest locationRequest;
    static final int ACCURACY_LIMIT = 10;
    SeekBar seekBar;
    ImageView iconRefresh;
    Location location;
    EditText eSiteName;
    RadioButton radioRocky, radioSandy;
    TextView txtLat, txtLng, txtCount;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_site);
        handle = findViewById(R.id.handle);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        setFields();
        mapFragment.getMapAsync(this);
        getSupportActionBar().setTitle("");
        Util.setCustomActionBar(getApplicationContext(),
                getSupportActionBar(), "MiniSASS", "Landing Page",
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_launcher), null);
    }

    static final int NUMBER_OF_RIVER_MARKERS = 250;
    View handle;

    private void setRiverPoints() {
        mgGoogleMap.clear();
        for (Marker m : riverMarkers) {
            m.remove();
        }
        riverMarkers = new ArrayList<>();
        for (Bag b : bags) {
            b.marker.remove();
        }
        bags = new ArrayList<>();
        selectedRiverPoints = new ArrayList<>();
        selectedSite = null;
        riverPoints.clear();

        addDeviceLocationMarker();

        BitmapDescriptor nearestIcon = BitmapDescriptorFactory
                .fromResource(R.drawable.dot_green);
        BitmapDescriptor icon = BitmapDescriptorFactory
                .fromResource(R.drawable.dot_red);
        if (!river.getRiverpartList().isEmpty()) {
            for (RiverPartDTO rp : river.getRiverpartList()) {
                riverPoints.addAll(rp.getRiverpointList());
            }
            Collections.sort(riverPoints);
            for (int i = 0; i < NUMBER_OF_RIVER_MARKERS; i++) {
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

                final Marker m = mgGoogleMap.addMarker(markerOptions);
                riverMarkers.add(m);
            }

        }
        setEvaluationSiteMarkers();
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
            mgGoogleMap.animateCamera(cu, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    CameraUpdate cu2 = CameraUpdateFactory.newLatLngZoom(latLng, 13);
                                    mgGoogleMap.animateCamera(cu2);
                                }
                            });
                        }
                    }, 5000);

                }

                @Override
                public void onCancel() {

                }
            });


        } else {
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(riverMarkers.get(0).getPosition(), 15);
            mgGoogleMap.moveCamera(cu);
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
        IconGenerator gen = new IconGenerator(getApplicationContext());
        gen.setColor(ContextCompat.getColor(getApplicationContext(), R.color.light_blue_300));
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

    HashMap<Marker, EvaluationSiteDTO> hashMap = new HashMap<>();

    private void setEvaluationSiteMarkers() {
        hashMap.clear();
        EvaluationSiteDTO s = findSiteWhereYouAre();
        if (s != null) {
            deviceMarker.remove();
        }
        IconGenerator gen = new IconGenerator(getApplicationContext());
        for (RiverDTO river: rivers) {
            Log.d(LOG, "setEvaluationSiteMarkers, River: " + river.getRiverName() + " sites: " + river.getEvaluationsiteList().size());
        }
        if (!river.getEvaluationsiteList().isEmpty()) {
            Log.i(LOG,"################ River: " + river.getRiverName() + " sites: " + river.getEvaluationsiteList().size());
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
                        gen.setColor(ContextCompat.getColor(getApplicationContext(), R.color.amber_400));
                    } else {
                        gen.setColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                    }
                } else {
                    gen.setColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
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
                        .title("" + es.getEvaluationSiteID())
                        .snippet("" + es.getEvaluationSiteID())
                        .icon(desc);

                final Marker m = mgGoogleMap.addMarker(markerOptions);
                hashMap.put(m, es);
                index++;
                Log.d(LOG,"================> Next index up is: " + index + " pos: " + m.getPosition());


            }

        }
        Log.e(LOG, "+++++++++++++++ hashMap has: " + hashMap.keySet().size());
        pointsHaveBeenSet = true;

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mgGoogleMap = googleMap;
        setGoogleMap();
    }

    List<Bag> bags = new ArrayList<>();

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
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mgGoogleMap.setMyLocationEnabled(true);
            mgGoogleMap.setBuildingsEnabled(true);
            mgGoogleMap.getUiSettings().setCompassEnabled(true);
            mgGoogleMap.getUiSettings().setZoomControlsEnabled(true);
            mgGoogleMap.getUiSettings().setZoomGesturesEnabled(true);

            mgGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Log.d(LOG, "Marker onMarkerClick, marker: title: " + marker.getTitle()
                            + " snip: " + marker.getSnippet()
                            + " latLng: " + marker.getPosition());
                    if (marker.getTitle() == null || marker.getTitle().isEmpty()) {
                        Log.e(LOG, "+++++++++++> marker has no title, WTF?");
                        //check if within or very near a site
                        for (EvaluationSiteDTO s : river.getEvaluationsiteList()) {
                            s.calculateDistance(marker.getPosition().latitude, marker.getPosition().longitude);
                        }
                        Collections.sort(river.getEvaluationsiteList());
                        if (!river.getEvaluationsiteList().isEmpty()) {
                            EvaluationSiteDTO nearest = river.getEvaluationsiteList().get(0);
                            if (nearest.getDistanceFromMe() < 500) {
                                selectedSite = nearest;
                                showPopupMenu();
                                return true;
                            }
                        }
                        return false;
                    }

                    selectedSite = hashMap.get(marker);

                    if (selectedSite == null) { // "you are here clicked..."
                        Log.e(LOG, "------------------- selectedSite is null. Site not found in hash");
                        selectedSite = findSiteWhereYouAre();
                        if (selectedSite == null) {
                            createSiteWhereYouAreDialog();
                            return true;
                        } else {
                            showPopupMenu();
                            return true;
                        }
                    }

                    Log.d(LOG, "Marker clicked - selected: " + selectedSite.getSiteName() + " id: " + selectedSite.getEvaluationSiteID()
                            + " observations: " + selectedSite.getEvaluationList().size());
                    showPopupMenu();
                    return true;
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
                        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(riverMarkers.get(0).getPosition(), 15);
                        mgGoogleMap.moveCamera(cu);
                    }

                }
            });

            mgGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    Log.d(LOG, "mgGoogleMap onMapLongClick: " + latLng);
                    //check if near river
                    List<DistanceFromRiver> mlist = new ArrayList<>(riverMarkers.size());
                    for (RiverPointDTO p : riverPoints) {
                        mlist.add(new DistanceFromRiver(p.getLatitude(), p.getLongitude(), latLng));
                    }
                    Collections.sort(mlist);
                    DistanceFromRiver dfr = mlist.get(0);
                    if (dfr.getDistance() < 500) {
                        showNewSiteDialog(latLng);
                    } else {
                        Log.d(LOG, "You are more than 500 metres from a river");
                    }


                }
            });
            mgGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    Log.d(LOG, "****************** MAP clicked: " + latLng);
                    //check if within or very near a site
                    for (EvaluationSiteDTO s : river.getEvaluationsiteList()) {
                        s.calculateDistance(latLng.latitude, latLng.longitude);
                    }
                    Collections.sort(river.getEvaluationsiteList());
                    if (!river.getEvaluationsiteList().isEmpty()) {
                        EvaluationSiteDTO nearest = river.getEvaluationsiteList().get(0);
                        if (nearest.getDistanceFromMe() < 500) {
                            selectedSite = nearest;
                            showPopupMenu();
                        }
                    }
                }
            });

        } catch (Exception e) {
            Log.e(LOG, "", e);
        }
    }


    static final DecimalFormat df = new DecimalFormat("###,###,###,###");
    private void showPopupMenu() {
        Log.w(LOG, "showPopupMenu selectedSite id: " + selectedSite.getEvaluationSiteID()
                + " obs: " + selectedSite.getEvaluationList().size());
        Util.setCustomActionBar(getApplicationContext(),
                getSupportActionBar(), selectedSite.getRiverName(), selectedSite.getSiteName(),
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_launcher), null);
        IconizedMenu popup = new IconizedMenu(this, handle);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_on_map, popup.getMenu());
        selectedSite.calculateDistance(location.getLatitude(), location.getLongitude());
        if (selectedSite.getDistanceFromMe() > 200) {
            Log.e(LOG, "Cannot do observation -------------------------------------: "
                    + selectedSite.getDistanceFromMe());
            Menu m = popup.getMenu();
            m.getItem(0).setVisible(false);
        }

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
                        Intent m = new Intent(getApplicationContext(), InsectPickerActivity.class);
                        m.putExtra("site", selectedSite);
                        startActivity(m);
                        break;
                    case R.id.ic_action_list:
                        Intent m2 = new Intent(getApplicationContext(), MainPagerActivity.class);
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

                        break;

                }
                return true;
            }


        });
        popup.show();

    }

    private void showEditorDialog(int type) {
        SiteEditorDialog d = new SiteEditorDialog();
        d.setContext(getApplicationContext());
        d.setType(type);
        if (type == SiteEditorDialog.ADD_SITE) {
            selectedSite.setRiverID(river.getRiverID());

        }
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
            public void onSiteRemoved() {
                getRiversAroundMe();
            }

            @Override
            public void onError(String message) {

            }
        });
        d.show(getSupportFragmentManager(), "EDITOR_DIALOG");
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
        //todo start search dialog
        if (riverSearchDialog == null) {
            riverSearchDialog = new RiverSearchDialog();
        } else {
            riverSearchDialog.show(getSupportFragmentManager(), "DIALOG");
            return;
        }
        riverSearchDialog.setLatitude(location.getLatitude());
        riverSearchDialog.setLongitude(location.getLongitude());
        riverSearchDialog.setRadius(15);
        if (rivers != null) {
            riverSearchDialog.setRivers(rivers);
        }
        riverSearchDialog.setContext(getApplicationContext());
        riverSearchDialog.setListener(new RiverSearchDialog.RiverSearchListener() {
            @Override
            public void onSearchComplete(List<RiverDTO> list) {
                if (!list.isEmpty()) {
                    rivers = list;
                    river = rivers.get(0);
                    setRiverPoints();
                }
            }

            @Override
            public void onError(String message) {

            }

            @Override
            public void onRiverSelected(RiverDTO r) {
                river = r;
                setRiverPoints();
            }

            @Override
            public void onEvaluationSiteSelected(EvaluationSiteDTO site) {

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

    Marker deviceMarker;

    private void addDeviceLocationMarker() {
        IconGenerator gen = new IconGenerator(getApplicationContext());
        gen.setColor(ContextCompat.getColor(getApplicationContext(), R.color.cyan_100));
        Bitmap bm = gen.makeIcon("You are here");
        BitmapDescriptor desc = BitmapDescriptorFactory.fromBitmap(bm);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .icon(desc)
                .title("You are here")
                .snippet("You are here");

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


    private void setFields() {
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
    static final int RADIUS = 20;

    private void getRiversAroundMe() {
        if (isBusy) {
            Log.e(LOG, "### getRiversAroundMe is BUSY!!!");
            return;
        }

        Log.d(LOG, "############### getRiversAroundMe");
        snackbar = Snackbar.make(handle, "Refreshing river data around you ....", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.LIST_DATA_WITH_RADIUS_RIVERS);
        w.setLatitude(location.getLatitude());
        w.setLongitude(location.getLongitude());
        w.setRadius(RADIUS);

        isBusy = true;
        BaseVolley.getRemoteData(Statics.SERVLET_ENDPOINT, w, getApplicationContext(), new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO r) {
                isBusy = false;
                snackbar.dismiss();
                Snackbar.make(handle, "Found " + r.getRiverList().size() + " rivers around you ....", Snackbar.LENGTH_LONG).show();

                Log.e(LOG, "### getRiversAroundMe, found: " + r.getRiverList().size());
                for (RiverDTO river : r.getRiverList()) {
                    river.calculateDistance(location.getLatitude(), location.getLongitude());
                }
                Collections.sort(r.getRiverList());
                rivers = r.getRiverList();
                if (!rivers.isEmpty()) {
                    river = rivers.get(0);
                    setRiverPoints();
                } else {
                    Util.showErrorToast(getApplicationContext(), "No rivers found");
                    return;
                }
                for (RiverDTO river: rivers) {
                    Log.e(LOG, "getRiversAroundMe: River sites: " + river.getRiverName() + " sites: " + river.getEvaluationsiteList().size());
                }

                CacheUtil.cacheData(getApplicationContext(), r,
                        CacheUtil.CACHE_DATA, new CacheUtil.CacheUtilListener() {
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

            }

            @Override
            public void onError(final String message) {
                Util.showErrorToast(getApplicationContext(), message);
            }
        });


    }

}
