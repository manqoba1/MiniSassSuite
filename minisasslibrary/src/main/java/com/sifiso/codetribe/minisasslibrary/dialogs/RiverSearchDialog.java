package com.sifiso.codetribe.minisasslibrary.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.sifiso.codetribe.minisasslibrary.util.CacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.RiverSearcher;
import com.sifiso.codetribe.minisasslibrary.util.Statics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by aubreymalabie on 4/17/16.
 */
public class RiverSearchDialog extends DialogFragment {

    public interface RiverSearchListener {
        void onSearchComplete(List<RiverDTO> rivers);
        void onError(String message);
        void onRiverSelected(RiverDTO river);
        void onEvaluationSiteSelected(EvaluationSiteDTO site);
        void onDirections(RiverDTO river);
        void onDirections(EvaluationSiteDTO site);
    }
    View view;
    private SeekBar seekBar;
    private ImageView iconSend, riverDirections, siteDirections;
    private Context context;
    private Spinner riverSpinner, siteSpinner;
    private View spinnersLayout;
    private double latitude, longitude;
    private int radius;
    ProgressBar progressBar;
    RiverSearchListener listener;
    private TextView riverCount, siteCount, txtRadius;
    static final String LOG = RiverSearcher.class.getSimpleName();


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.river_search, null);
        setFields();
        if (rivers == null || rivers.isEmpty()) {
            getCachedRivers();
        } else {
            setRiverSpinner();
        }
        builder.setView(view);

        return builder.create();
    }
    private void setFields() {

        riverDirections = (ImageView) view.findViewById(R.id.riverDirectionsIcon);
        siteDirections = (ImageView) view.findViewById(R.id.siteDirectionsIcon);
        progressBar = (ProgressBar) view.findViewById(R.id.busy);
        progressBar.setVisibility(View.GONE);
        txtRadius = (TextView) view.findViewById(R.id.progress);
        riverCount = (TextView) view.findViewById(R.id.riverCount);
        siteCount = (TextView) view.findViewById(R.id.sitesCount);
        siteSpinner = (Spinner) view.findViewById(R.id.siteSpinner);
        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        iconSend = (ImageView) view.findViewById(R.id.refresh);
        riverSpinner = (Spinner) view.findViewById(R.id.riverSpinner);
        siteSpinner = (Spinner) view.findViewById(R.id.siteSpinner);
        spinnersLayout = view.findViewById(R.id.siteSpinnerLayout);
       //
        riverDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDirections(selectedRiver);
            }
        });
        siteDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDirections(selectedSite);
            }
        });
        iconSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRiversAroundMe();
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radius = progress;
                txtRadius.setText(""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setListener(RiverSearchListener listener) {
        this.listener = listener;
    }
    List<RiverDTO> rivers;

    boolean isBusy;
    private void getCachedRivers() {
        CacheUtil.getCachedData(getContext(), CacheUtil.CACHE_DATA, new CacheUtil.CacheUtilListener() {
            @Override
            public void onFileDataDeserialized(ResponseDTO response) {
                if (!response.getRiverList().isEmpty()) {
                    rivers = response.getRiverList();
                    setRiverSpinner();
                }
            }

            @Override
            public void onDataCached(ResponseDTO response) {

            }

            @Override
            public void onError() {

            }
        });
    }
    private void getRiversAroundMe() {
        if (isBusy) {
            Log.e(LOG, "### getRiversAroundMe is BUSY!!!");
            return;
        }
        Log.d(LOG, "############### getRiversAroundMe");

        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.LIST_DATA_WITH_RADIUS_RIVERS);
        w.setLatitude(latitude);
        w.setLongitude(longitude);
        w.setRadius(radius);

        isBusy = true;
        progressBar.setVisibility(View.VISIBLE);
        BaseVolley.getRemoteData(Statics.SERVLET_ENDPOINT, w, context, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO r) {
                isBusy = false;
                progressBar.setVisibility(View.GONE);
                Log.e(LOG, "### getRiversAroundMe, found: " + r.getRiverList().size());
                for (RiverDTO river : r.getRiverList()) {
                    river.calculateDistance(latitude, longitude);
                }
                Collections.sort(r.getRiverList());
                rivers = r.getRiverList();
                setRiverSpinner();
               // Util.expand(spinnersLayout,1000,null);
                listener.onSearchComplete(r.getRiverList());
                CacheUtil.cacheData(context, r,
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
                listener.onError(error.getMessage());
            }

            @Override
            public void onError(final String message) {
                listener.onError(message);
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
        ArrayAdapter<String> riverAdapter = new ArrayAdapter<>(
                context, R.layout.spinner_text_black, rlist);
        riverAdapter.setDropDownViewResource(R.layout.spinner_text_black);
        riverCount.setText("" + rivers.size());
        riverSpinner.setAdapter(riverAdapter);
        riverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRiver = rivers.get(position);
                setSiteSpinner();
                listener.onRiverSelected(selectedRiver);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void setSiteSpinner() {
        List<String> siteList = new ArrayList<>();
        for (EvaluationSiteDTO s : selectedRiver.getEvaluationsiteList()) {
            if (s.getSiteName() == null) {
                siteList.add("No name given");
            } else {
                siteList.add(s.getSiteName().trim());
            }
        }
        ArrayAdapter<String> siteAdapter = new ArrayAdapter<>(
                context, R.layout.spinner_text_red, siteList);
        siteAdapter.setDropDownViewResource(R.layout.spinner_text_red);
        siteCount.setText("" + selectedRiver.getEvaluationsiteList().size());
        siteSpinner.setAdapter(siteAdapter);
        siteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedSite = selectedRiver.getEvaluationsiteList().get(position);
                listener.onEvaluationSiteSelected(selectedSite);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setRivers(List<RiverDTO> rivers) {
        this.rivers = rivers;
    }
}
