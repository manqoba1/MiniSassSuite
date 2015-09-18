package com.sifiso.codetribe.minisasslibrary.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestList;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheck;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheckResult;
import com.sifiso.codetribe.minisasslibrary.util.CacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.ErrorUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.WebSocketUtilForRequests;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class CachedSyncService extends IntentService {


    static final Gson gson = new Gson();
    static final String LOG = CachedSyncService.class.getSimpleName();
    private final IBinder mBinder = new LocalBinder();
    RequestCache requestCache;
    RequestSyncListener requestSyncListener;

    public CachedSyncService() {
        super("CachedSyncService");
    }

    private static String getStringFromInputStream(InputStream is) throws IOException {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } finally {
            if (br != null) {
                br.close();
            }
        }
        String json = sb.toString();
        return json;

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(LOG, "### RequestSyncService onHandleIntent");
        FileInputStream stream;
        try {
            stream = getApplicationContext().openFileInput("requestCache.json");
            String json = getStringFromInputStream(stream);
            RequestCache cache = gson.fromJson(json, RequestCache.class);
            if (cache != null) {
                requestCache = cache;
                Log.i(LOG, "++ RequestCache returned from disk, entries: "
                        + requestCache.getRequestCacheEntryList().size());
                print();
                controlRequestUpload();
            } else {
                Log.e(LOG, "-- requestCache is null");
            }
        } catch (Exception e) {

        }
    }

    private void controlRequestUpload() {
        WebCheckResult r = WebCheck.checkNetworkAvailability(getApplicationContext());
        if (r.isWifiConnected()) {
            Log.i(LOG, "%%% WIFI is connected and cached requests about to be sent to cloud....");
            RequestList list = new RequestList();
            for (RequestCacheEntry e : requestCache.getRequestCacheEntryList()) {
                list.getRequests().add(e.getRequest());
            }
            if (list.getRequests().isEmpty()) {
                Log.d(LOG, "#### no requests cached, quitting...");
                requestSyncListener.onTasksSynced(0, 0);
                return;
            }
            Log.w(LOG, "### sending list of cached requests: " + list.getRequests().size());

            WebSocketUtilForRequests.sendRequest(getApplicationContext(), Statics.REQUEST_ENDPOINT,list, new WebSocketUtilForRequests.WebSocketListener() {
                @Override
                public void onMessage(ResponseDTO response) {
                    if (!ErrorUtil.checkServerError(getApplicationContext(), response)) {
                        return;
                    }
                    Log.i(LOG, "** cached requests sent up! good responses: " + response.getGoodCount() +
                            " bad responses: " + response.getBadCount());
                    for (RequestCacheEntry e : requestCache.getRequestCacheEntryList()) {
                        e.setDateUploaded(new Date().getTime());
                    }
                    cleanupCache();
                    requestSyncListener.onTasksSynced(response.getGoodCount(), response.getBadCount());
                }

                @Override
                public void onClose() {

                }

                @Override
                public void onError(String message) {
                    requestSyncListener.onError(message);
                }
            });
        } else {
            Log.e(LOG, "WIFI is NOT connected so cannot sync");
        }
    }

    private void cleanupCache() {
        List<RequestCacheEntry> list = new ArrayList<>();
        for (RequestCacheEntry e : requestCache.getRequestCacheEntryList()) {
            if (e.getDateUploaded() == null) {
                list.add(e);
            }
        }
        Log.i(LOG, "cache cleaned up, pending: " + list.size());
        requestCache.setRequestCacheEntryList(list);
        CacheUtil.cacheRequest(getApplicationContext(), requestCache, null);
    }

    private void print() {
        for (RequestCacheEntry e : requestCache.getRequestCacheEntryList()) {
            Log.w(LOG, "+++ " + e.getDateRequested() + " requestType: " + e.getRequest().getRequestType());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void startSyncCachedRequests(RequestSyncListener rsl) {
        requestSyncListener = rsl;
        onHandleIntent(null);
    }


    public interface RequestSyncListener {
        public void onTasksSynced(int goodResponses, int badResponses);

        public void onError(String message);
    }

    public class LocalBinder extends Binder {
        public CachedSyncService getService() {
            return CachedSyncService.this;
        }
    }
}
