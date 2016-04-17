package com.sifiso.codetribe.minisasslibrary.util;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.toolbox.BaseVolley;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by aubreymalabie on 4/17/16.
 */
public class RiverSearcher extends CardView {

    public interface RiverSearchListener {
        void onSearchComplete(List<RiverDTO> rivers);

        void onError(String message);
        void onRiverSelected(RiverDTO river);
        void onEvaluationSiteSelected(EvaluationSiteDTO site);
    }

    public RiverSearcher(Context context) {
        super(context);
        ctx = context;
        setFields();
    }

    public RiverSearcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
        setFields();
    }

    public RiverSearcher(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ctx = context;
        setFields();
    }

    private SeekBar seekBar;
    private ImageView iconSend;
    private Context ctx;
    private Spinner riverSpinner, siteSpinner;
    private View spinnersLayout;
    private double latitude, longitude;
    private int radius;
    private TextView riverCount, siteCount;
    private RiverSearchListener riverSearchListener;
    static final String LOG = RiverSearcher.class.getSimpleName();

    private void setFields() {
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.river_search, null);

        riverCount = (TextView) v.findViewById(R.id.riverCount);
        siteCount = (TextView) v.findViewById(R.id.sitesCount);
        siteSpinner = (Spinner) v.findViewById(R.id.siteSpinner);
        seekBar = (SeekBar) v.findViewById(R.id.seekBar);
        iconSend = (ImageView) v.findViewById(R.id.refresh);
        riverSpinner = (Spinner) v.findViewById(R.id.riverSpinner);
        siteSpinner = (Spinner) v.findViewById(R.id.siteSpinner);
        spinnersLayout = v.findViewById(R.id.siteSpinnerLayout);
        spinnersLayout.setVisibility(GONE);
        iconSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getRiversAroundMe();
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radius = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void startSearch(double latitude, double longitude, int radius, RiverSearchListener listener) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.riverSearchListener = listener;

        getRiversAroundMe();
    }

    boolean isBusy;
    Snackbar snackbar;
    List<RiverDTO> rivers;

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
        w.setLatitude(latitude);
        w.setLongitude(longitude);
        w.setRadius(radius);

        isBusy = true;
        BaseVolley.getRemoteData(Statics.SERVLET_ENDPOINT, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO r) {
                isBusy = false;
                snackbar.dismiss();
                Snackbar.make(seekBar, "Found " + r.getRiverList().size() + " rivers around you ....", Snackbar.LENGTH_LONG).show();

                Log.e(LOG, "### getRiversAroundMe, found: " + r.getRiverList().size());
                for (RiverDTO river : r.getRiverList()) {
                    river.calculateDistance(latitude, longitude);
                }
                Collections.sort(r.getRiverList());
                rivers = r.getRiverList();
                setRiverSpinner();
                riverSearchListener.onSearchComplete(r.getRiverList());
                CacheUtil.cacheData(ctx, r,
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
                riverSearchListener.onError(error.getMessage());
            }

            @Override
            public void onError(final String message) {
                riverSearchListener.onError(message);
            }
        });


    }

    private RiverDTO selectedRiver;
    private EvaluationSiteDTO selectedSite;

    private void setRiverSpinner() {
        List<String> rlist = new ArrayList<>();
        for (RiverDTO river : rivers) {
            rlist.add(river.getRiverName().trim());
        }
        ArrayAdapter<String> riverAdapter = new ArrayAdapter<String>(
                ctx, R.layout.spinner_text_black, rlist);
        riverAdapter.setDropDownViewResource(R.layout.spinner_text_black);
        riverCount.setText("" + rivers.size());
        riverSpinner.setAdapter(riverAdapter);
        riverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRiver = rivers.get(position);
                setSiteSpinner();
                riverSearchListener.onRiverSelected(selectedRiver);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void setSiteSpinner() {
        List<String> siteList = new ArrayList<>();
        siteList.add("Please select Site");
        for (EvaluationSiteDTO s : selectedRiver.getEvaluationsiteList()) {
            if (s.getSiteName() == null) {
                siteList.add("No name given");
            } else {
                siteList.add(s.getSiteName().trim());
            }
        }
        ArrayAdapter<String> siteAdapter = new ArrayAdapter<>(
                ctx, R.layout.spinner_text_red, siteList);
        siteAdapter.setDropDownViewResource(R.layout.spinner_text_red);
        siteCount.setText("" + selectedRiver.getEvaluationsiteList().size());
        siteSpinner.setAdapter(siteAdapter);
        siteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedSite = null;
                    return;
                }
                selectedSite = selectedRiver.getEvaluationsiteList().get(position - 1);
                riverSearchListener.onEvaluationSiteSelected(selectedSite);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
