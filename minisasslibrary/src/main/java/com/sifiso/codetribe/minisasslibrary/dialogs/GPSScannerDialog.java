package com.sifiso.codetribe.minisasslibrary.dialogs;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationSiteDTO;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.text.DecimalFormat;

/**
 * Created by CodeTribe1 on 2015-02-27.
 */
public class GPSScannerDialog extends DialogFragment {
    static final DecimalFormat df = new DecimalFormat("###,###,###,##0.00");
    static final String LOG = GPSScannerDialog.class.getSimpleName();
    TextView desiredAccuracy, txtLat, txtLng, txtAccuracy;

    View view;
    SeekBar seekBar;
    boolean isScanning;
    EvaluationSiteDTO evaluationSite;
    ImageView imgLogo, hero;
    Context ctx;
    ObjectAnimator logoAnimator;
    long start, end;
    Chronometer chronometer;
    boolean isStop = false;
    Location location;
    boolean isFirst = true;
    private GPSScannerDialogListener listener;

    public GPSScannerDialog() {
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
        startScan();
        return view;
    }

    private void setFields() {

        desiredAccuracy = (TextView) view.findViewById(R.id.GPS_desiredAccuracy);
        txtAccuracy = (TextView) view.findViewById(R.id.GPS_accuracy);
        txtLat = (TextView) view.findViewById(R.id.GPS_latitude);
        txtLng = (TextView) view.findViewById(R.id.GPS_longitude);
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



    }

    public void startScan() {
        if (isFirst) {
            listener.onStartScanRequested();
            txtAccuracy.setText("0.00");
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
            isScanning = true;

            isFirst = false;
        }
    }

    public void setLocation(Location location) {
        // if (evaluationSite == null) return;
        this.location = location;
        txtLat.setText("" + location.getLatitude());
        txtLng.setText("" + location.getLongitude());
        txtAccuracy.setText("" + location.getAccuracy());
        evaluationSite = new EvaluationSiteDTO();
        evaluationSite.setLatitude(location.getLatitude());
        evaluationSite.setLongitude(location.getLongitude());
        evaluationSite.setAccuracy(location.getAccuracy());
        if (location.getAccuracy() == seekBar.getProgress()
                || location.getAccuracy() < seekBar.getProgress()) {

            isScanning = false;
            chronometer.stop();

            //remove the initialisation when code combined

            evaluationSite.setLatitude(location.getLatitude());
            evaluationSite.setLongitude(location.getLongitude());
            evaluationSite.setAccuracy(location.getAccuracy());
            //confirmLocation();

            return;
        }
        Util.flashSeveralTimes(hero, 200, 2, null);
    }

    public void stopScan() {
        listener.onEndScanRequested();
        // listener.onLocationConfirmed(evaluationSite);

        isScanning = false;

        chronometer.stop();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (GPSScannerDialogListener) activity;
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

    @Override
    public void onDismiss(DialogInterface dialog) {
        listener.onEndScanRequested();
        super.onDismiss(dialog);
    }

    public interface GPSScannerDialogListener {
        public void onStartScanRequested();

        public void onLocationConfirmed(EvaluationSiteDTO evaluationSite);

        public void onEndScanRequested();

        public void onMapRequested(EvaluationSiteDTO evaluationSite);


    }
}
