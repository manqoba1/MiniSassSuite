package com.sifiso.codetribe.minisasslibrary.activities;

import android.Manifest;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.adapters.InsectSelectionAdapter;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.InsectImageDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.services.RequestCache;
import com.sifiso.codetribe.minisasslibrary.services.RequestSyncService;
import com.sifiso.codetribe.minisasslibrary.toolbox.BaseVolley;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheck;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheckResult;
import com.sifiso.codetribe.minisasslibrary.util.CacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.Constants;
import com.sifiso.codetribe.minisasslibrary.util.DividerItemDecoration;
import com.sifiso.codetribe.minisasslibrary.util.RequestCacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.SharedUtil;
import com.sifiso.codetribe.minisasslibrary.util.SpacesItemDecoration;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InsectPickerActivity extends AppCompatActivity implements
        LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    static final String LOG = InsectPickerActivity.class.getSimpleName();
    GoogleApiClient mGoogleApiClient;
    LocationRequest locationRequest;
    Location location;
    Context ctx;
    double total = 0.0;
    private InsectSelectionAdapter adapter;
    private RecyclerView recyclerView;

    private List<InsectImageDTO> mRawImages;
    private List<InsectImageDTO> mSelectedImages;
    private TextView txtTotal, txtNumber, txtAverage, txtScoreStatus;
    private ImageView scoreIcon;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insect_picker);
        ctx = getApplicationContext();
        evaluationSite = (EvaluationSiteDTO)getIntent().getSerializableExtra("site");
        Log.w(LOG,"evaluationSite, id: " + evaluationSite.getEvaluationSiteID()
        + "observations: " + evaluationSite.getEvaluationList().size());
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        getSupportActionBar().setTitle("");
        setFields();
        getCachedData();

        Util.setCustomActionBar(ctx,getSupportActionBar(),
                "Macro-inveterbrates", evaluationSite.getSiteName()
                        + ", " + evaluationSite.getRiverName(),
                ContextCompat.getDrawable(ctx,R.drawable.ic_launcher),null);
    }

    private void getCachedData() {
        CacheUtil.getCachedData(ctx, CacheUtil.CACHE_DATA, new CacheUtil.CacheUtilListener() {
            @Override
            public void onFileDataDeserialized(ResponseDTO r) {
                mRawImages = r.getInsectimageDTOList();
                setList();
            }

            @Override
            public void onDataCached(ResponseDTO response) {

            }

            @Override
            public void onError() {
                Log.e(LOG, "Failed to get cached data");
            }
        });
    }
    private void setList() {

        adapter = new InsectSelectionAdapter(ctx,
                mRawImages,
                new InsectSelectionAdapter.InsectPopupAdapterListener() {
            @Override
            public void onInsectSelected(InsectImageDTO insect, int index) {
                setCheckedInsects();
            }

            @Override
            public void onViewMore(InsectImageDTO insect, int index) {
                intent = new Intent(InsectPickerActivity.this, ViewMoreImagesActivity.class);
                intent.putExtra("insetImageList", (java.io.Serializable) mRawImages);
                intent.putExtra("insect", insect);
                startActivity(intent);
            }
        });
        LinearLayoutManager llm = new LinearLayoutManager(ctx,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(llm);
        recyclerView.addItemDecoration(new SpacesItemDecoration(4));
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        switch (reqCode) {
        }
    }

    Snackbar snackbar;
    private void sendEvaluation() {

        WebCheckResult wcr = WebCheck.checkNetworkAvailability(ctx);

        if (txtScoreStatus.getText().toString().isEmpty()) {
            return;
        }
        EvaluationDTO m = new EvaluationDTO();
        m.setEvaluationSiteID(evaluationSite.getEvaluationSiteID());
        m.setTeamMemberID(SharedUtil.getTeamMember(ctx).getTeamMemberID());
        m.setEvaluationDate(new Date().getTime());
        m.setScore(Double.parseDouble(txtAverage.getText().toString()));
        m.setLatitude(location.getLatitude());
        m.setLongitude(location.getLongitude());

        for (InsectImageDTO im: mSelectedImages) {
            im.setDateRegistered(null);
            im.setInsect(null);
            im.setInsectimagelistList(null);
        }
        m.setInsectImages(mSelectedImages);
        m.setConditionsID(statusCondition);

        snackbar = Snackbar.make(recyclerView,"Sending evaluation",Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
        RequestDTO w = new RequestDTO(RequestDTO.ADD_EVALUATION);
        w.setEvaluation(m);
        if (!wcr.isMobileConnected() && !wcr.isWifiConnected()) {
            addRequestToCache(w);
            snackbar.dismiss();
            return;
        }

        BaseVolley.getRemoteData(Statics.SERVLET_ENDPOINT, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO response) {
                snackbar.dismiss();
                Snackbar.make(recyclerView,
                        "Observation has been sent to server",Snackbar.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onVolleyError(VolleyError error) {
                snackbar.dismiss();
                Snackbar.make(recyclerView,error.getMessage(),Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onError(String message) {
                snackbar.dismiss();
                Snackbar.make(recyclerView,message,Snackbar.LENGTH_LONG).show();
            }
        });
    }
    private void addRequestToCache(final RequestDTO request) {
        RequestCacheUtil.addRequest(ctx, request, new CacheUtil.CacheRequestListener() {
            @Override
            public void onDataCached() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.w(LOG, "+++++++++++++ request cached, starting bound service");
                        Intent m = new Intent(ctx,RequestSyncService.class);
                        m.putExtra("request",request);
                        ctx.startService(m);
                        Snackbar.make(recyclerView,
                                "Observation data has been saved",Snackbar.LENGTH_LONG).show();
                        finish();
                    }
                });


            }

            @Override
            public void onRequestCacheReturned(RequestCache cache) {

            }

            @Override
            public void onError() {
            }
        });
    }

    private void setCheckedInsects() {
        mSelectedImages = new ArrayList<>();

        for (InsectImageDTO m: mRawImages) {
            if (m.isSelected()) {
                mSelectedImages.add(m);
                total += m.getSensitivityScore();
            }
        }
        txtAverage.setText(calculateAverage() + "");
        txtNumber.setText("" + mSelectedImages.size());
        calculateStatusScore();
        txtTotal.setText(calculateScore() + "");

        Log.e(LOG, "############# selected insects: " + mSelectedImages.size() + " ");

    }

    EvaluationSiteDTO evaluationSite;
    Integer statusCondition;
    private void calculateStatusScore() {
        String status = "";
        statusCondition = 0;
        double average = 0.0, total = 0.0;
        if (mSelectedImages == null || mSelectedImages.isEmpty()) {
            average = 0.0;
        } else {
            for (InsectImageDTO ii : mSelectedImages) {
                total = total + ii.getInsect().getSensitivityScore();
            }
            average = total / mSelectedImages.size();
        }
        average = Math.round(average * 100.0) / 100.0;
        Log.d(LOG, average + "");
        if (evaluationSite.getCategoryID() == 8) { //Sandy Type
            if (average > 6.9) {
                status = "Unmodified(NATURAL condition)";
                statusCondition = Constants.UNMODIFIED_NATURAL_SAND;
                txtScoreStatus.setTextColor(ContextCompat.getColor(ctx,R.color.blue_900));
                txtAverage.setTextColor(ContextCompat.getColor(ctx,R.color.blue_900));
                scoreIcon.setImageDrawable(ContextCompat.getDrawable(ctx,R.drawable.blue_crap));
                //scoreIcon.setColorFilter(ContextCompat.getColor(ctx,R.color.purple), PorterDuff.Mode.MULTIPLY);
            } else if (average > 5.8 && average < 6.9) {
                status = "Largely natural/few modifications(GOOD condition)";
                statusCondition = Constants.LARGELY_NATURAL_SAND;
                txtScoreStatus.setTextColor(ContextCompat.getColor(ctx,R.color.green_700));
                txtAverage.setTextColor(ContextCompat.getColor(ctx,R.color.green_700));
                scoreIcon.setImageDrawable(ContextCompat.getDrawable(ctx,R.drawable.green_crap));
                // scoreIcon.setColorFilter(ContextCompat.getColor(ctx,R.color.green), PorterDuff.Mode.MULTIPLY);
            } else if (average > 4.9 && average < 5.8) {
                status = "Moderately modified(FAIR condition)";
                statusCondition = Constants.MODERATELY_MODIFIED_SAND;
                txtScoreStatus.setTextColor(ContextCompat.getColor(ctx,R.color.orange_400));
                txtAverage.setTextColor(ContextCompat.getColor(ctx,R.color.orange_400));
                scoreIcon.setImageDrawable(ContextCompat.getDrawable(ctx,R.drawable.orange_crap));
                // scoreIcon.setColorFilter(ContextCompat.getColor(ctx,R.color.yellow_dark), PorterDuff.Mode.MULTIPLY);
            } else if (average > 4.3 && average < 4.9) {
                status = "Largely modified(POOR condition)";
                statusCondition = Constants.LARGELY_MODIFIED_SAND;
                txtScoreStatus.setTextColor(ContextCompat.getColor(ctx,R.color.red_900));
                txtAverage.setTextColor(ContextCompat.getColor(ctx,R.color.red_900));
                scoreIcon.setImageDrawable(ContextCompat.getDrawable(ctx,R.drawable.red_crap));
                // scoreIcon.setColorFilter(ContextCompat.getColor(ctx,R.color.orange), PorterDuff.Mode.MULTIPLY);
            } else if (average < 4.3) {
                status = "Seriously/critically modified";
                statusCondition = Constants.CRITICALLY_MODIFIED_SAND;
                txtScoreStatus.setTextColor(ContextCompat.getColor(ctx,R.color.purple_800));
                txtAverage.setTextColor(ContextCompat.getColor(ctx,R.color.purple_800));
                scoreIcon.setImageDrawable(ContextCompat.getDrawable(ctx,R.drawable.purple_crap));
                //scoreIcon.setColorFilter(ContextCompat.getColor(ctx,R.color.red), PorterDuff.Mode.MULTIPLY);
            }
        } else if (evaluationSite.getCategoryID() == 9) { //"Rocky Type")) {
            if (average > 7.9) {
                status = "Unmodified(NATURAL condition)";
                statusCondition = Constants.UNMODIFIED_NATURAL_ROCK;
                txtScoreStatus.setTextColor(ContextCompat.getColor(ctx,R.color.blue_900));
                txtAverage.setTextColor(ContextCompat.getColor(ctx,R.color.blue_900));
                scoreIcon.setImageDrawable(ContextCompat.getDrawable(ctx,R.drawable.blue_crap));
                //scoreIcon.setColorFilter(ContextCompat.getColor(ctx,R.color.purple), PorterDuff.Mode.MULTIPLY);
            } else if (average > 6.8 && average < 7.9) {
                status = "Largely natural/few modifications(GOOD condition)";
                statusCondition = Constants.LARGELY_NATURAL_ROCK;
                txtScoreStatus.setTextColor(ContextCompat.getColor(ctx,R.color.green_700));
                txtAverage.setTextColor(ContextCompat.getColor(ctx,R.color.green_700));
                scoreIcon.setImageDrawable(ContextCompat.getDrawable(ctx,R.drawable.green_crap));
                // scoreIcon.setColorFilter(ContextCompat.getColor(ctx,R.color.green), PorterDuff.Mode.MULTIPLY);
            } else if (average > 6.1 && average < 6.8) {
                status = "Moderately modified(FAIR condition)";
                statusCondition = Constants.MODERATELY_MODIFIED_ROCK;
                txtScoreStatus.setTextColor(ContextCompat.getColor(ctx,R.color.orange_400));
                txtAverage.setTextColor(ContextCompat.getColor(ctx,R.color.orange_400));
                scoreIcon.setImageDrawable(ContextCompat.getDrawable(ctx,R.drawable.orange_crap));
                // scoreIcon.setColorFilter(ContextCompat.getColor(ctx,R.color.yellow_dark), PorterDuff.Mode.MULTIPLY);
            } else if (average > 5.1 && average < 6.1) {
                status = "Largely modified(POOR condition)";
                statusCondition = Constants.LARGELY_MODIFIED_ROCK;
                txtScoreStatus.setTextColor(ContextCompat.getColor(ctx,R.color.red_900));
                txtAverage.setTextColor(ContextCompat.getColor(ctx,R.color.red_900));
                scoreIcon.setImageDrawable(ContextCompat.getDrawable(ctx,R.drawable.red_crap));
                // scoreIcon.setColorFilter(ContextCompat.getColor(ctx,R.color.orange), PorterDuff.Mode.MULTIPLY);
            } else if (average < 5.1) {
                status = "Seriously/critically modified";
                statusCondition = Constants.CRITICALLY_MODIFIED_ROCK;
                txtScoreStatus.setTextColor(ContextCompat.getColor(ctx,R.color.purple_800));
                txtAverage.setTextColor(ContextCompat.getColor(ctx,R.color.purple_800));
                scoreIcon.setImageDrawable(ContextCompat.getDrawable(ctx,R.drawable.purple_crap));
                //scoreIcon.setColorFilter(ContextCompat.getColor(ctx,R.color.red), PorterDuff.Mode.MULTIPLY);
            }
        }
        txtScoreStatus.setText(status);
        
    }

    private double calculateScore() {
        double score = 0.0;

        if (mSelectedImages.isEmpty()) {
            return 0.0;
        } else {
            for (InsectImageDTO ii : mSelectedImages) {
                score += ii.getInsect().getSensitivityScore();
            }
        }
        return score;
    }

    private double calculateAverage() {
        double average = 0.0, total = 0.0;
        if (mSelectedImages == null || mSelectedImages.isEmpty()) {
            return 0.0;
        }
        for (InsectImageDTO ii : mSelectedImages) {
            total += ii.getInsect().getSensitivityScore();
        }
        average = total / mSelectedImages.size();
        average = Math.round(average * 100.0) / 100.0;
        return average;
    }

    private Intent intent;
    private static final int INSECT_DATA = 103;


    @Override
    public void onBackPressed() {
        if (mSelectedImages != null && !mSelectedImages.isEmpty()) {
            intent = new Intent();
            intent.putExtra("overallInsect", (java.io.Serializable) mRawImages);
            intent.putExtra("selectedInsects", (java.io.Serializable) mSelectedImages);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    private void setFields() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        txtTotal = (TextView) findViewById(R.id.TV_total_score);
        txtNumber = (TextView) findViewById(R.id.TV_average_score);
        txtAverage = (TextView) findViewById(R.id.TV_avg_score);
        txtScoreStatus = (TextView) findViewById(R.id.txtScoreStatus);
        scoreIcon = (ImageView) findViewById(R.id.scoreIcon);


        recyclerView = (RecyclerView) findViewById(R.id.SD_list);
        recyclerView.setLayoutManager(new GridLayoutManager(ctx, 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(ctx, RecyclerView.VERTICAL));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendingEval = true;
                startLocationUpdates();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            intent = new Intent(InsectPickerActivity.this, EvaluationActivity.class);
            intent.putExtra("overallInsect", (java.io.Serializable) mRawImages);
            intent.putExtra("selectedInsects", (java.io.Serializable) mSelectedImages);
            setResult(INSECT_DATA, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
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
            }
            Log.w(LOG, "## requesting location ....lastLocation: "
                    + location.getLatitude() + " "
                    + location.getLongitude() + " acc: "
                    + location.getAccuracy());
        }
    }
    static final float ACCURACY_LIMIT = 15;
    boolean mRequestingLocationUpdates;
    protected void startLocationUpdates() {
        Log.w(LOG, "###### startLocationUpdates: " + new Date().toString());
        snackbar = Snackbar.make(recyclerView,"Getting device GPS coordinates",Snackbar.LENGTH_INDEFINITE);
        snackbar.show();

        if (mGoogleApiClient.isConnected()) {
            mRequestingLocationUpdates = true;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, locationRequest, this);
            snackbar = Snackbar.make(recyclerView, "Getting device GPS coordinates ...", Snackbar.LENGTH_INDEFINITE);
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

    boolean sendingEval;
    @Override
    public void onLocationChanged(Location location) {
        Log.e(LOG, "####### onLocationChanged, accuracy: " + location.getAccuracy());
        this.location = location;
        if (location.getAccuracy() <= ACCURACY_LIMIT) {
            stopLocationUpdates();
            snackbar.dismiss();
            if (sendingEval) {
                sendingEval = false;
                sendEvaluation();
            }

        }
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
