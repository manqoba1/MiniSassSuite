package com.sifiso.codetribe.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.sifiso.codetribe.minisasslibrary.activities.EvaluationActivity;
import com.sifiso.codetribe.minisasslibrary.activities.MapsActivity;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.fragments.EvaluationListFragment;
import com.sifiso.codetribe.minisasslibrary.services.CreateEvaluationListener;
import com.sifiso.codetribe.minisasslibrary.util.GPSTracker;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.util.List;

public class EvaluationListActivity extends AppCompatActivity implements CreateEvaluationListener {

    EvaluationListFragment evaluationListFragment;


    GPSTracker tracker;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation_list);
        ctx = getApplicationContext();
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        List<EvaluationSiteDTO> evaluationSiteList = (List<EvaluationSiteDTO>) getIntent().getSerializableExtra("evaluationSite");
        Log.e("EvaluationListFragment", "evaluationSiteList: " + evaluationSiteList.size());
        evaluationListFragment = (EvaluationListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        evaluationListFragment.setEvaluationSiteList(evaluationSiteList);
        tracker = new GPSTracker(ctx);
        getSupportActionBar().setTitle("");

        Util.setCustomActionBar(ctx,getSupportActionBar(),
                evaluationSiteList.get(0).getRiverName(), "Observations",
                ContextCompat.getDrawable(ctx, com.sifiso.codetribe.minisasslibrary.R.drawable.ic_launcher),null);
        setFields();
      //  handleIntent(getIntent());
    }

    private void setFields() {
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//
//            }
//        });
    }

    Menu mMenu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_evaluation_list, menu);
        mMenu = menu;
        return true;
    }


    public void setRefreshActionButtonState(final boolean refreshing) {
        if (mMenu != null) {
            final MenuItem refreshItem = mMenu.findItem(com.sifiso.codetribe.minisasslibrary.R.id.action_refresh);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(com.sifiso.codetribe.minisasslibrary.R.layout.action_bar_progess);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }

    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        super.onPause();
    }

    @Override
    protected void onResume() {
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);

        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onListEvaluationSites(List<EvaluationSiteDTO> siteList, int index) {

    }

    @Override
    public void onRefreshMap(RiverDTO river, int result) {
        Intent intent = new Intent(EvaluationListActivity.this, MapsActivity.class);
        intent.putExtra("river", river);
        intent.putExtra("displayType", result);
        startActivity(intent);
    }

    @Override
    public void onCreateEvaluation(RiverDTO river) {

    }

    static String LOG = EvaluationListActivity.class.getSimpleName();

    @Override
    public void onDirection(Double lat, Double lng) {
        Log.i(LOG, "startDirectionsMap ..........");
        String url = "http://maps.google.com/maps?saddr="
                + tracker.getLatitude() + "," + tracker.getLongitude()
                + "&daddr=" + lat + "," + lng + "&mode=driving";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    @Override
    public void onSitesMapRequested(RiverDTO river) {

    }


    @Override
    public void onNewEvaluation() {
        Intent intent = new Intent(EvaluationListActivity.this, EvaluationActivity.class);
        startActivity(intent);
    }
}
