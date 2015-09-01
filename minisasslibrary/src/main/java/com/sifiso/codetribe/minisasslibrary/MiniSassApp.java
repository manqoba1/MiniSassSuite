package com.sifiso.codetribe.minisasslibrary;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.L;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.sifiso.codetribe.minisasslibrary.toolbox.BitmapLruCache;

import java.io.File;
import java.util.HashMap;

/**
 * Created by sifiso on 2015-08-26.
 */
public class MiniSassApp extends Application {

    static String LOG = MiniSassApp.class.getSimpleName();
    RequestQueue requestQueue;
    BitmapLruCache bitmapLruCache;
   // static BackgroundMail backgroundMail;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n\n#######################################\n");
        sb.append("#######################################\n");
        sb.append("###\n");
        sb.append("###  Minisass App has started\n");
        sb.append("###\n");
        sb.append("#######################################\n\n");

        Log.d(LOG, sb.toString());
//
        //  ACRA.init(this);
        /*RiverDTO river = SharedUtil.getCompany(getApplicationContext());
        if (company != null) {
            ACRA.getErrorReporter().putCustomData("companyID", "" + company.getCompanyID());
            ACRA.getErrorReporter().putCustomData("companyName", company.getCompanyName());
        }*/
        Log.e(LOG, "###### ACRA Crash Reporting has been initiated");
        initializeVolley(getApplicationContext());
        /*backgroundMail = new BackgroundMail(getApplicationContext());
        backgroundMail.setGmailPassword("apk@mlab.co.za");
        backgroundMail.setGmailPassword("mLabtestdevice");*/
        DisplayImageOptions defaultOptions =
                new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .showImageOnFail(getApplicationContext().getResources().getDrawable(R.drawable.under_construction))
                        .showImageOnLoading(getApplicationContext().getResources().getDrawable(R.drawable.under_construction))
                        .build();

        File cacheDir = StorageUtils.getCacheDirectory(this, true);
        if (cacheDir != null) {
            StorageUtils.getCacheDirectory(this, true).deleteOnExit();
        }
        Log.d(LOG, "## onCreate, ImageLoader cacheDir, files: " + cacheDir.listFiles().length);
        //
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .denyCacheImageMultipleSizesInMemory()
                .diskCache(new UnlimitedDiscCache(cacheDir))
                .memoryCache(new LruMemoryCache(16 * 1024 * 1024))
                .defaultDisplayImageOptions(defaultOptions)
                .build();

        if (ImageLoader.getInstance() == null) {
            ImageLoader.getInstance().init(config);
            L.writeDebugLogs(false);
            L.writeLogs(false);

            Log.w(LOG, "###### ImageLoaderConfiguration has been initialised");
        }
    }

    public enum TrackerName {
        APP_TRACKER, //Tracker used for this app use only
        GLOBAL_TRACKER, //Tracker used by all apps from company
        ECOMMERCE_TRACKER //tracker used by all ecommerce transaction from a company
    }

    static final String PROPERTY_ID = "UA-53661372-2";
    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = null;
            if (trackerId == TrackerName.APP_TRACKER) {
                t = analytics.newTracker(PROPERTY_ID);
            }
            if (trackerId == TrackerName.GLOBAL_TRACKER) {
                t = analytics.newTracker(R.xml.global_tracker);
            }
            mTrackers.put(trackerId, t);
        }
        Log.i(LOG, "analytics trackerID:" + trackerId.toString());
        return mTrackers.get(trackerId);
    }

    /**
     * Set up Volley Networking; create RequestQueue and ImageLoader
     *
     * @param context
     */
    public void initializeVolley(Context context) {
        Log.e(LOG, "initializing Volley Networking ...");
        requestQueue = Volley.newRequestQueue(context);
        int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
                .getMemoryClass();

        // Use 1/8th of the available memory for this memory cache.
        int cacheSize = 1024 * 1024 * memClass / 8;
        bitmapLruCache = new BitmapLruCache(cacheSize);
        // imageLoader = new ImageLoader(requestQueue, bitmapLruCache);
        Log.i(LOG, "********** Yebo! Volley Networking has been initialized, cache size: " + (cacheSize / 1024) + " KB");

        // Create global configuration and initialize ImageLoader with this configuration
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();

        ImageLoader.getInstance().init(config);
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    /*public static BackgroundMail getBackgroundMail() {
        return backgroundMail;
    }*/

    public BitmapLruCache getBitmapLruCache() {
        return bitmapLruCache;
    }
}
