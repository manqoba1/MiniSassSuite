package com.sifiso.codetribe.minisasslibrary.activities;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.sifiso.codetribe.minisasslibrary.services.RequestTaskService;

/**
 * Created by aubreymalabie on 4/10/16.
 */
public class MSApp extends Application {

    GcmNetworkManager mGcmNetworkManager;
    static final String REQUEST_TAG = "REQUEST_TAG", LOG = MSApp.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n\n#################################################################\n");
        sb.append("#######################################################################\n");
        sb.append("#########\n");
        sb.append("#########  MiniSASS App has started, setting up resources ...............\n");
        sb.append("#########\n");
        sb.append("########################################################################\n\n");

        Log.d(LOG, sb.toString());

        mGcmNetworkManager = GcmNetworkManager.getInstance(this);
        startRequestsTask();
    }
    private void startRequestsTask() {
        PeriodicTask task = new PeriodicTask.Builder()
                .setService(RequestTaskService.class)
                .setPeriod(HOUR_IN_SECONDS)
                .setPersisted(true)
                .setTag(REQUEST_TAG)
                .setRequiredNetwork(PeriodicTask.NETWORK_STATE_CONNECTED)
                .build();

        mGcmNetworkManager.schedule(task);
        Log.i(LOG, "###### mGcmNetworkManager task for REQUESTS upload scheduled: HOUR_IN_SECONDS" );
    }
    static final int MINUTE_IN_SECONDS = 60,
            HALF_HOUR_IN_SECONDS = 60 * 30,
            HOUR_IN_SECONDS = 60 * 60,
            TWO_HOURS_IN_SECONDS = HOUR_IN_SECONDS * 2,
            FIVE_MINUTE_IN_SECONDS = 60 * 5;
}
