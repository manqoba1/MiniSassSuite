package com.sifiso.codetribe.minisasslibrary.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dialogs.EditEvaluationDialog;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationImageDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.interfaces.MarkerInfoWindowAdapterListener;
import com.sifiso.codetribe.minisasslibrary.toolbox.BaseVolley;
import com.sifiso.codetribe.minisasslibrary.util.Constants;
import com.sifiso.codetribe.minisasslibrary.util.ErrorUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.ToastUtil;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleMap googleMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest locationRequest;


    Location location;
    Context mCtx;
    List<Marker> markers = new ArrayList<Marker>();
    static final String LOG = MapsActivity.class.getSimpleName();
    boolean mResolvingError;
    static final long ONE_MINUTE = 1000 * 60;
    static final long FIVE_MINUTES = 1000 * 60 * 5;
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    //unique error dialog
    private static final String DIALOG_ERROR = "dialog_error";
    EvaluationImageDTO evaluationImage;
    EvaluationDTO evaluation;
    RiverDTO river;
    int index;
    TextView /*text,*/ txtCount, textMap;
    View topLayout;
    ProgressBar progressBar;
    static final Locale loc = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm");


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mCtx = MapsActivity.this;
        mMarkersHashMap = new HashMap<Marker, EvaluationDTO>();
        evaluationImage = (EvaluationImageDTO) getIntent().getSerializableExtra("evaluationImage");
        evaluation = (EvaluationDTO) getIntent().getSerializableExtra("evaluation");
        river = (RiverDTO) getIntent().getSerializableExtra("river");
        index = getIntent().getIntExtra("index", 0);
        int displayType = getIntent().getIntExtra("displayType", EVALUATION_VIEW);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        //txtCount = (TextView) findViewById(R.id.count);
        //textMap = (TextView) findViewById(R.id.textMap);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
//        text = (TextView) findViewById(R.id.textMap);

        // textMap.setText(river.getRiverName());
        //  txtCount.setText(river.getEvaluationSiteList().size() + "");

//        progressBar.setVisibility(View.GONE);
        //Statics.setRobotoFontBold(ctx, text);

        topLayout = findViewById(R.id.top);


        googleMap = mapFragment.getMap();
        if (googleMap == null) {
            Util.showToast(mCtx, getString(R.string.map_unavailable));
            // finish();
            return;
        }


        if (displayType == EVALUATION_VIEW) {
            setEvaluationMarkers();
        }
        setEvaluationMarkers();
        setGoogleMap();

    }

    static final int EVALUATION_VIEW = 12;
    static final int RIVER_VIEW = 13;

    private List<Polyline> polylines = new ArrayList<Polyline>();
    private List<LatLng> iterateList = new ArrayList<LatLng>();


    private void setEvaluationMarkers() {
        googleMap.clear();

        if (!river.getEvaluationsiteList().isEmpty()) {
            int index = 0;
            for (EvaluationSiteDTO es : river.getEvaluationsiteList()) {
                BitmapDescriptor desc = BitmapDescriptorFactory.fromResource(R.drawable.gray_crap);
                Integer conditionColor = null;
                for (EvaluationDTO eva : es.getEvaluationList()) {
                    if (eva.getConditionsID() != null) {
                        conditionColor = es.getEvaluationList().get(0).getConditionsID();
                        switch (eva.getConditionsID()) {
                            case Constants.UNMODIFIED_NATURAL_SAND:

                                desc = BitmapDescriptorFactory.fromResource(R.drawable.blue_crap);
                                break;
                            case Constants.LARGELY_NATURAL_SAND:

                                desc = BitmapDescriptorFactory.fromResource(R.drawable.green_crap);
                                break;
                            case Constants.MODERATELY_MODIFIED_SAND:

                                desc = BitmapDescriptorFactory.fromResource(R.drawable.orange_crap);
                                break;
                            case Constants.LARGELY_MODIFIED_SAND:

                                desc = BitmapDescriptorFactory.fromResource(R.drawable.red_crap);
                                break;
                            case Constants.CRITICALLY_MODIFIED_SAND:

                                desc = BitmapDescriptorFactory.fromResource(R.drawable.purple_crap);
                                break;
                            case Constants.UNMODIFIED_NATURAL_ROCK:

                                desc = BitmapDescriptorFactory.fromResource(R.drawable.blue_crap);
                                break;
                            case Constants.LARGELY_NATURAL_ROCK:

                                desc = BitmapDescriptorFactory.fromResource(R.drawable.green_crap);
                                break;
                            case Constants.MODERATELY_MODIFIED_ROCK:

                                desc = BitmapDescriptorFactory.fromResource(R.drawable.orange_crap);
                                break;
                            case Constants.LARGELY_MODIFIED_ROCK:

                                desc = BitmapDescriptorFactory.fromResource(R.drawable.red_crap);
                                break;
                            case Constants.CRITICALLY_MODIFIED_ROCK:

                                desc = BitmapDescriptorFactory.fromResource(R.drawable.purple_crap);
                                break;
                        }

                    }
                    index++;
                    Log.d(LOG, "" + index + " " + eva.getEvaluationSite());
                    MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(eva.getEvaluationSite().getLatitude(), eva.getEvaluationSite().getLongitude())).icon(desc)
                            .snippet(eva.getRemarks());
                    //markerOptions.
                    final Marker m = googleMap.addMarker(markerOptions);
                    mMarkersHashMap.put(m, eva);
                    markers.add(m);
                    googleMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter(new MarkerInfoWindowAdapterListener() {
                        @Override
                        public void onEditEvaluation(EvaluationDTO evaluation) {
                            editEvaluationDialog = new EditEvaluationDialog();
                            editEvaluationDialog.show(getSupportFragmentManager(), "Edit Evaluation");
                            editEvaluationDialog.setEvaluation(evaluation);
                            editEvaluationDialog.setListener(new EditEvaluationDialog.EditEvaluationDialogListener() {
                                @Override
                                public void onSaveUpdate(EvaluationDTO evaluation) {
                                    if (evaluation.getEvaluationID() == null) {
                                        ToastUtil.errorToast(mCtx, "Evaluation can not be edited");
                                        return;
                                    }
                                    editEvaluation(evaluation);
                                }
                            });

                        }

                        @Override
                        public void onDirection(EvaluationDTO evaluation) {
                            startDirectionsMap(evaluation.getEvaluationSite().getLatitude(), evaluation.getEvaluationSite().getLongitude());
                        }
                    }));
                }

            }
            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    //ensure that all markers in bounds
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (Marker marker : markers) {
                        builder.include(marker.getPosition());
                    }

                    LatLngBounds bounds = builder.build();
                    int padding = 10; // offset from edges of the map in pixels

                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    //   txtCount.setText("" + markers.size());
                    //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 1.0f));
                    googleMap.animateCamera(cu);
                    setTitle(river.getRiverName());
                }
            });

        }
    }

    private void editEvaluation(EvaluationDTO dto) {
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.UPDATE_EVALUATION);
        w.setEvaluation(dto);

        BaseVolley.getRemoteData(Statics.SERVLET_TEST, w, mCtx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO r) {
                if (!ErrorUtil.checkServerError(mCtx, r)) {
                    return;
                }


                //   setList();
            }

            @Override
            public void onVolleyError(VolleyError error) {

            }

            @Override
            public void onError(String message) {

            }
        });
    }

    private void setGoogleMap() {
        try {
            googleMap.setMyLocationEnabled(true);
            googleMap.setBuildingsEnabled(true);

            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    location.setLatitude(latLng.latitude);
                    location.setLongitude(latLng.longitude);
                    Log.w(LOG, "********* onMapClick");
                }
            });
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    //CameraUpdate cu = CameraUpdateFactory.newLatLng(marker.getPosition());
                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 10f);
                    googleMap.animateCamera(cu);
                    marker.showInfoWindow();

                    return true;
                }
            });

        } catch (Exception e) {
            Log.e(LOG, "", e);
        }
    }

    List<String> list;

    private void showPopup(final double lat, final double lng, String title) {
        list = new ArrayList<>();
        list.add("Directions");
        list.add("Status Report");

        Util.showPopupBasicWithHeroImage(MapsActivity.this, MapsActivity.this, list, topLayout, mCtx.getString(R.string.select_action), new Util.UtilPopupListener() {
            @Override
            public void onItemSelected(int index) {
                switch (index) {
                    case 0:
                        startDirectionsMap(lat, lng);
                        break;
                    case 1:
                        //Util.showToast(ctx, ctx.getString(R.string.under_cons));
                        break;
                    case 2:

                        break;

                }
            }
        });


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
        overridePendingTransition(com.sifiso.codetribe.minisasslibrary.R.anim.slide_in_left, com.sifiso.codetribe.minisasslibrary.R.anim.slide_out_right);
        super.onPause();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMarker();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMarker() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    static final int ACCURACY_LIMIT = 50;

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        if (location.getAccuracy() <= ACCURACY_LIMIT) {
            stopLocationUpdates();
            //getRiversAroundMe();
        }
        Log.e(LOG, "####### onLocationChanged");
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
            // mLocationClient.disconnect();
        } catch (Exception e) {
            Log.e(LOG, "Failed to Stop something", e);
        }
        super.onStop();
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

        if (location.getAccuracy() > ACCURACY_LIMIT) {
            startLocationUpdates();
        } else {
            //  getRiversAroundMe();
        }
    }

    boolean mRequestingLocationUpdates;

    protected void startLocationUpdates() {
        Log.w(LOG, "###### startLocationUpdates: " + new Date().toString());
        if (mGoogleApiClient.isConnected()) {
            mRequestingLocationUpdates = true;
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

    private HashMap<Marker, EvaluationDTO> mMarkersHashMap;
    EditEvaluationDialog editEvaluationDialog;

    public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        MarkerInfoWindowAdapterListener mListener;

        public MarkerInfoWindowAdapter(MarkerInfoWindowAdapterListener mListener) {
            this.mListener = mListener;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View v = getLayoutInflater().inflate(R.layout.evaluation_list_item, null);

            final EvaluationDTO myMarker = mMarkersHashMap.get(marker);

            TextView ELI_condition = (TextView) v.findViewById(R.id.ELI_condition);
            TextView ELI_date = (TextView) v.findViewById(R.id.ELI_date);
            TextView ELI_oxygen = (TextView) v.findViewById(R.id.ELI_oxygen);
            TextView ELI_pH = (TextView) v.findViewById(R.id.ELI_pH);
            TextView ELI_score = (TextView) v.findViewById(R.id.ELI_score);
            TextView ELI_team = (TextView) v.findViewById(R.id.ELI_team);
            TextView ELI_wc = (TextView) v.findViewById(R.id.ELI_wc);
            TextView ELI_wt = (TextView) v.findViewById(R.id.ELI_wt);
            TextView ELI_remarks = (TextView) v.findViewById(R.id.ELI_remarks);

            RelativeLayout AR_traineeLayout = (RelativeLayout) v.findViewById(R.id.AR_traineeLayout);
            RelativeLayout AR_traineeLayout2 = (RelativeLayout) v.findViewById(R.id.AR_traineeLayout2);
            RelativeLayout AR_insLayout = (RelativeLayout) v.findViewById(R.id.AR_insLayout);
            RelativeLayout AR_insLayout2 = (RelativeLayout) v.findViewById(R.id.AR_insLayout2);

            ImageView ELI_condition_image = (ImageView) v.findViewById(R.id.ELI_condition_image);
            //h.ELI_contribute = (ImageView) v.findViewById(R.id.ELI_contribute);
            final ImageView ELI_edit = (ImageView) v.findViewById(R.id.ELI_edit);
            final ImageView ELI_directions = (ImageView) v.findViewById(R.id.ELI_directions);
            TextView ELI_txtCount = (TextView) v.findViewById(R.id.ELI_txtCount);
            ELI_txtCount.setVisibility(View.GONE);
            Linkify.addLinks(ELI_remarks, Linkify.ALL);
            ELI_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.flashOnce(ELI_edit, 200, new Util.UtilAnimationListener() {
                        @Override
                        public void onAnimationEnded() {
                            mListener.onEditEvaluation(myMarker);
                        }
                    });

                }
            });
            ELI_directions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.flashOnce(ELI_directions, 200, new Util.UtilAnimationListener() {
                        @Override
                        public void onAnimationEnded() {
                            mListener.onDirection(myMarker);
                        }
                    });

                }
            });
            ELI_team.setText("Team " + myMarker.getTeamName());

            ELI_date.setText(Util.getLongDate(new Date(myMarker.getEvaluationDate())));
            ELI_score.setText((Math.round(myMarker.getScore() * 100.00) / 100.00) + "");
            if (myMarker.getOxygen() == null || myMarker.getOxygen().doubleValue() == 0.0) {
                AR_insLayout.setVisibility(View.GONE);
            } else {
                AR_insLayout.setVisibility(View.VISIBLE);
            }
            if (myMarker.getWaterClarity() == null || myMarker.getWaterClarity().doubleValue() == 0.0) {
                AR_traineeLayout.setVisibility(View.GONE);
            } else {
                AR_traineeLayout.setVisibility(View.VISIBLE);
            }
            if (myMarker.getWaterTemperature() == null || myMarker.getWaterTemperature().doubleValue() == 0.0) {
                AR_traineeLayout2.setVisibility(View.GONE);
            } else {
                AR_traineeLayout2.setVisibility(View.VISIBLE);
            }
            if (myMarker.getpH() == null || myMarker.getpH().doubleValue() == 0.0) {
                AR_insLayout2.setVisibility(View.GONE);
            } else {
                AR_insLayout2.setVisibility(View.VISIBLE);
            }
            ELI_oxygen.setText(myMarker.getOxygen() + "");
            ELI_wc.setText(myMarker.getWaterClarity() + "");
            ELI_wt.setText(myMarker.getWaterTemperature() + "");
            ELI_pH.setText(myMarker.getpH() + "");
            if (myMarker.getRemarks() == null) {
                ELI_remarks.setVisibility(View.GONE);
            } else {
                ELI_remarks.setVisibility(View.VISIBLE);
            }

            ELI_remarks.setText(myMarker.getRemarks());
            switch (myMarker.getConditionsID()) {
                case Constants.UNMODIFIED_NATURAL_SAND:
                    ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.blue));
                    ELI_score.setTextColor(mCtx.getResources().getColor(R.color.blue));
                    ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.blue_crap));
                    break;
                case Constants.LARGELY_NATURAL_SAND:
                    ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.green));
                    ELI_score.setTextColor(mCtx.getResources().getColor(R.color.green));
                    ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.green_crap));
                    break;
                case Constants.MODERATELY_MODIFIED_SAND:
                    ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.orange));
                    ELI_score.setTextColor(mCtx.getResources().getColor(R.color.orange));
                    ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.orange_crap));
                    break;
                case Constants.LARGELY_MODIFIED_SAND:
                    ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.red));
                    ELI_score.setTextColor(mCtx.getResources().getColor(R.color.red));
                    ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.red_crap));

                    break;
                case Constants.CRITICALLY_MODIFIED_SAND:
                    ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.purple));
                    ELI_score.setTextColor(mCtx.getResources().getColor(R.color.purple));
                    ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.purple_crap));
                    break;
                case Constants.UNMODIFIED_NATURAL_ROCK:
                    ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.blue));
                    ELI_score.setTextColor(mCtx.getResources().getColor(R.color.blue));
                    ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.blue_crap));
                    break;
                case Constants.LARGELY_NATURAL_ROCK:
                    ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.green));
                    ELI_score.setTextColor(mCtx.getResources().getColor(R.color.green));
                    ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.green_crap));
                    break;
                case Constants.MODERATELY_MODIFIED_ROCK:
                    ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.orange));
                    ELI_score.setTextColor(mCtx.getResources().getColor(R.color.orange));
                    ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.orange_crap));
                    break;
                case Constants.LARGELY_MODIFIED_ROCK:
                    ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.red));
                    ELI_score.setTextColor(mCtx.getResources().getColor(R.color.red));
                    ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.red_crap));
                    break;
                case Constants.CRITICALLY_MODIFIED_ROCK:
                    ELI_condition.setTextColor(mCtx.getResources().getColor(R.color.purple));
                    ELI_score.setTextColor(mCtx.getResources().getColor(R.color.purple));
                    ELI_condition_image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.purple_crap));
                    break;

            }

            ELI_condition.setText(myMarker.getConditionName());

            return v;
        }
    }
}
