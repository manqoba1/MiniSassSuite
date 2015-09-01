package com.sifiso.codetribe.riverteamapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.activities.EvaluationActivity;
import com.sifiso.codetribe.minisasslibrary.activities.MapsActivity;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.fragments.EvaluationListFragment;
import com.sifiso.codetribe.minisasslibrary.services.CreateEvaluationListener;
import com.sifiso.codetribe.minisasslibrary.util.GPSTracker;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.util.List;

public class EvaluationListActivity extends AppCompatActivity implements CreateEvaluationListener {

    EvaluationListFragment evaluationListFragment;


    GPSTracker tracker;
    Context ctx;
    TextView RL_add;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation_list);
        ctx = getApplicationContext();
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //ResponseDTO response = (ResponseDTO) getIntent().getSerializableExtra("response");
        List<EvaluationSiteDTO> evaluationSiteList = (List<EvaluationSiteDTO>) getIntent().getSerializableExtra("evaluationSite");
        evaluationListFragment = (EvaluationListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        evaluationListFragment.setEvaluationSiteList(evaluationSiteList);
        tracker = new GPSTracker(ctx);
        getSupportActionBar().setTitle(evaluationSiteList.get(0).getRiverName());
        setFields();
    }

    private void setFields() {
        RL_add = (TextView) findViewById(R.id.RL_add);
        RL_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(RL_add, 200, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        Intent intent = new Intent(EvaluationListActivity.this, EvaluationActivity.class);
                        startActivity(intent);
                    }
                });

            }
        });
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_evaluation_list, menu);
        return true;
    }

    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        super.onPause();
    }

    @Override
    protected void onResume() {
        overridePendingTransition(R.anim.slide_out_left,R.anim.slide_in_right);

        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRefreshEvaluation(List<EvaluationSiteDTO> siteList, int index, String riverName) {

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
    public void onPullRefresh() {

    }

    @Override
    public void onNewEvaluation() {
        Intent intent = new Intent(EvaluationListActivity.this, EvaluationActivity.class);
        startActivity(intent);
    }
}
