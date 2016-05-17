package com.sifiso.codetribe.minisasslibrary.dialogs;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.activities.MSApp;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.util.RiverDataWorker;

import java.text.DecimalFormat;
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
    Button btnClose, btnLoad;
    ProgressBar progressBar;
    RiverSearchListener listener;
    private TextView riverCount, siteCount, txtRadius;
    static final String LOG = RiverSearchDialog.class.getSimpleName();


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.river_search, null);
        setFields();
        if (rivers != null && !rivers.isEmpty()) {
            setRiverSpinner();
        }
        builder.setView(view);
        IntentFilter m = new IntentFilter(RiverDataWorker.BROADCAST_DIALOG);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(new SearchBroadCastReceiver(),m);
        return builder.create();
    }
    private void setFields() {

        btnClose = (Button) view.findViewById(R.id.btnClose);
        btnLoad = (Button) view.findViewById(R.id.btnLoad);
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
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCachedData();
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSearchComplete(rivers);
            }
        });
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
        seekBar.setProgress(10);
        seekBar.setMax(50);
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

    public void setApp(MSApp app) {
        this.app = app;
    }

    public void setListener(RiverSearchListener listener) {
        this.listener = listener;
    }
    List<RiverDTO> rivers;
    MSApp app;
    Snackbar snackbar;

    private void getRiversAroundMe() {

        Log.d(LOG, "############### getRiversAroundMe");
        progressBar.setVisibility(View.VISIBLE);
        snackbar = Snackbar.make(progressBar,"Searching for rivers ....",Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
        RiverDataWorker.getRiversAroundMe(app, context, latitude, longitude,
                radius, true, new RiverDataWorker.RiverSearchListener() {
            @Override
            public void onResponse(List<RiverDTO> list) {
                progressBar.setVisibility(View.GONE);
                snackbar.dismiss();
                Log.e(LOG, "### .....RiverDataWorker.getRiversAroundMe onResponse, " +
                        "found: " + list.size());
                setRivers(list,true);
                Snackbar.make(progressBar,"Search for rivers found: " +
                list.size(),Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String message) {
                snackbar.dismiss();
                snackbar =Snackbar.make(progressBar,message,Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Close", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
                snackbar.setActionTextColor(ContextCompat.getColor(context,R.color.amber_500));
                snackbar.show();

            }
        });






    }

    private void getCachedData() {
        riverSpinner.setVisibility(View.GONE);
        siteSpinner.setVisibility(View.GONE);
        RiverDataWorker.getRivers(app, new RiverDataWorker.RiverCacheListener() {
            @Override
            public void onRiversCached() {

            }
            @Override
            public void onRiversFound(List<RiverDTO> list) {
                if (!list.isEmpty()) {
                    rivers = list;
                    for (RiverDTO r: rivers) {
                        r.calculateDistance(latitude,longitude);
                    }
                    Collections.sort(rivers);
                    riverSpinner.setVisibility(View.VISIBLE);
                    siteSpinner.setVisibility(View.VISIBLE);
                    setRiverSpinner();
                }
            }

            @Override
            public void onError(String message) {

            }
        });
    }
    class SearchBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(LOG,"======= SearchBroadCastReceiver onReceive: getCachedData starting");
            getCachedData();

        }
    }
    private RiverDTO selectedRiver;
    private EvaluationSiteDTO selectedSite;
    private boolean isFirsTime = true;
    static final DecimalFormat df = new DecimalFormat("###,###,###,###,###,##0.0");

    private void setRiverSpinner() {
        List<String> rlist = new ArrayList<>();
        for (RiverDTO river : rivers) {
            rlist.add(river.getRiverName().trim()
                    + "\t\t(" + df.format(river.getDistanceFromMe()/1000) + " km)" );
        }
        ArrayAdapter<String> riverAdapter = new ArrayAdapter<>(
                context, R.layout.spinner_text_black, rlist);
        riverAdapter.setDropDownViewResource(R.layout.spinner_text_black);
        riverCount.setText("" + rivers.size());
        riverSpinner.setAdapter(riverAdapter);
        riverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (rivers.isEmpty()) {
                    Log.e(LOG,"............no rivers for spinner");
                }
                selectedRiver = rivers.get(position);
                setSiteSpinner();
                if (isFirsTime) {
                    isFirsTime = false;
                    Log.d(LOG,"ignored onItemSelected because its first time thru");
                } else {
                    listener.onRiverSelected(selectedRiver);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void setSiteSpinner() {
        List<String> siteList = new ArrayList<>();
        for (EvaluationSiteDTO s : selectedRiver.getEvaluationsiteList()) {
            s.calculateDistance(latitude,longitude);
        }
        Collections.sort(selectedRiver.getEvaluationsiteList());
        for (EvaluationSiteDTO s : selectedRiver.getEvaluationsiteList()) {
            if (s.getSiteName() == null) {
                siteList.add("No name given (" + df.format(s.getDistanceFromMe()/1000) + " km)" );
            } else {
                siteList.add(s.getSiteName().trim() +  " (" + df.format(s.getDistanceFromMe()/1000) + " km)");
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

    public void setFirsTime(boolean firsTime) {
        isFirsTime = firsTime;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setRivers(List<RiverDTO> riverList, final boolean setSpinner) {
        this.rivers = riverList;
        List<Integer> ids = new ArrayList<>();
        for (RiverDTO r: rivers) {
            ids.add(r.getRiverID());
        }
        RiverDataWorker.getRiversByIDs(app, ids, new RiverDataWorker.RiverCacheListener() {
            @Override
            public void onRiversCached() {

            }
            @Override
            public void onRiversFound(List<RiverDTO> list) {
                rivers = list;
                for (RiverDTO r: rivers) {
                    r.calculateDistance(latitude,longitude);
                }
                Collections.sort(rivers);
                if (setSpinner || riverSpinner != null) {
                    setRiverSpinner();
                }
            }

            @Override
            public void onError(String message) {

            }
        });

    }
}
