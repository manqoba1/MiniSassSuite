package com.sifiso.codetribe.minisasslibrary.activities;

import android.app.Application;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.sifiso.codetribe.minisasslibrary.services.RequestTaskService;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

/**
 * Created by aubreymalabie on 4/10/16.
 */
public class MSApp extends Application {

    GcmNetworkManager mGcmNetworkManager;
    static final String REQUEST_TAG = "REQUEST_TAG", LOG = MSApp.class.getSimpleName();
    DB snappyDB;
    private static RequestQueue requestQueue;

    public DB getSnappyDB() {
        try {
            if (!snappyDB.isOpen()) {
                snappyDB = DBFactory.open(getApplicationContext());
            }
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        return snappyDB;
    }

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

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);
        mGcmNetworkManager = GcmNetworkManager.getInstance(this);
        startRequestsTask();
        //SnappyDB
        try {
            snappyDB = DBFactory.open(getApplicationContext());
            Log.w(LOG,"################ SnappyDB has been opened");
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();

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
        Log.i(LOG, "###### mGcmNetworkManager task for REQUESTS upload scheduled: HOUR_IN_SECONDS");
    }

    static final int MINUTE_IN_SECONDS = 60,
            HALF_HOUR_IN_SECONDS = 60 * 30,
            HOUR_IN_SECONDS = 60 * 60,
            TWO_HOURS_IN_SECONDS = HOUR_IN_SECONDS * 2,
            FIVE_MINUTE_IN_SECONDS = 60 * 5;

    public static RequestQueue getRequestQueue() {
        return requestQueue;
    }
}
