package com.sifiso.codetribe.minisasslibrary.fragments;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.text.DecimalFormat;


public class GPSScanFragment extends Fragment implements PageFragment {

    static final DecimalFormat df = new DecimalFormat("###,###,###,##0.00");
    static final String LOG = GPSScanFragment.class.getSimpleName();
    TextView desiredAccuracy, txtLat, txtLng, txtAccuracy;
    RelativeLayout GPS_nameLayout;
    View view;
    SeekBar seekBar;
    boolean isScanning;
    EvaluationSiteDTO evaluationSite;
    ImageView imgLogo, hero;
    Context ctx;
    ObjectAnimator logoAnimator;
    long start, end;
    Chronometer chronometer;
    boolean pleaseStop;
    Location location;
    private GPSScanFragmentListener listener;

    public GPSScanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GPSScanFragment.
     */
    public static GPSScanFragment newInstance() {
        GPSScanFragment fragment = new GPSScanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void animateCounts() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.w(LOG, "###### onCreateView");

        view = inflater.inflate(R.layout.fragment_gps, container, false);
        ctx = getActivity();

        setFields();
        return view;
    }

    public void startScan() {
        listener.onStartScanRequested();
        txtAccuracy.setText("0.00");
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        isScanning = true;

    }

    private void setFields() {

        desiredAccuracy = (TextView) view.findViewById(R.id.GPS_desiredAccuracy);
        txtAccuracy = (TextView) view.findViewById(R.id.GPS_accuracy);
        txtLat = (TextView) view.findViewById(R.id.GPS_latitude);
        txtLng = (TextView) view.findViewById(R.id.GPS_longitude);
        GPS_nameLayout = (RelativeLayout) view.findViewById(R.id.GPS_nameLayout);
        GPS_nameLayout.setVisibility(View.GONE);
        seekBar = (SeekBar) view.findViewById(R.id.GPS_seekBar);
        imgLogo = (ImageView) view.findViewById(R.id.GPS_imgLogo);
        hero = (ImageView) view.findViewById(R.id.GPS_hero);
        chronometer = (Chronometer) view.findViewById(R.id.GPS_chrono);


        Statics.setRobotoFontBold(ctx, txtLat);
        Statics.setRobotoFontBold(ctx, txtLng);

        imgLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(imgLogo, 100, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        listener.onMapRequested(evaluationSite);
                    }
                });
            }
        });

        txtAccuracy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(txtAccuracy, 100, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        if (evaluationSite.getAccuracy() == null) return;
                        listener.onMapRequested(evaluationSite);
                    }
                });

            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                desiredAccuracy.setText("" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        startScan();
    }


    public void resetLogo() {
        logoAnimator = ObjectAnimator.ofFloat(imgLogo, "rotation", 0, 360);
        logoAnimator.setDuration(200);
        logoAnimator.start();
    }


    public void setLocation(Location location) {
        if (evaluationSite == null) {
            evaluationSite = new EvaluationSiteDTO();
        }
        this.location = location;
        txtLat.setText("" + location.getLatitude());
        txtLng.setText("" + location.getLongitude());
        txtAccuracy.setText("" + location.getAccuracy());

        if (location.getAccuracy() == seekBar.getProgress()
                || location.getAccuracy() < seekBar.getProgress()) {
            isScanning = false;
            chronometer.stop();
            resetLogo();
            evaluationSite.setLatitude(location.getLatitude());
            evaluationSite.setLongitude(location.getLongitude());
            evaluationSite.setAccuracy(location.getAccuracy());
            listener.onLocationConfirmed(evaluationSite);
            GPS_nameLayout.setVisibility(View.VISIBLE);
            return;
        }
        Util.flashSeveralTimes(hero, 200, 2, null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (GPSScanFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " - Host activity" + activity.getLocalClassName() + " must implement GPSScanFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public EvaluationSiteDTO getProjectSite() {
        return evaluationSite;
    }

    public void setProjectSite(EvaluationSiteDTO evaluationSite) {
        this.evaluationSite = evaluationSite;

    }

    public void stopScan() {
        listener.onEndScanRequested();
        // listener.onLocationConfirmed(evaluationSite);

        isScanning = false;

        chronometer.stop();
    }

    public interface GPSScanFragmentListener {
        public void onStartScanRequested();

        public void onLocationConfirmed(EvaluationSiteDTO projectSite);

        public void onEndScanRequested();

        public void onMapRequested(EvaluationSiteDTO projectSite);


    }
}
