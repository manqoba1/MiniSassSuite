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
import com.sifiso.codetribe.minisasslibrary.util.WebSocketUtilForRequests;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Chris on 2015-03-03.
 */
public class RequestSyncService extends IntentService {

    static  final String LOG = RequestSyncService.class.getSimpleName();
    static final Gson gson = new Gson();
    RequestSyncListener requestSyncListener;
    RequestCache requestCache;

    public RequestSyncService() {
        super("RequestSyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.w(LOG, "FIRED: RequestSyncService, onHandleIntent");
        FileInputStream stream;
        try {

            stream = getApplicationContext().openFileInput("requestCache.json");
            String json = getStringFromInputStream(stream);
            RequestCache cache = gson.fromJson(json, RequestCache.class);
            if (cache != null) {
                requestCache = cache;
                Log.i(LOG, "RequestCache returned From disk with the following entries: "
                + requestCache.getRequestCacheEntryList().size());
                print();
                controlRequestUpload();
            } else {
                Log.e(LOG, "requestCache is null");
                requestSyncListener.onTasksSynced(0, 0);
            }
        } catch (FileNotFoundException e) {
            Log.i(LOG, "fileNotFoundException, there is no request cache currently");
            //onHandleIntent(null);
            requestSyncListener.onTasksSynced(0, 0);
        } catch (Exception e) {
            Log.e(LOG, "there's an issue with the sync", e);
            requestSyncListener.onTasksSynced(0,0);
        }

    }

    private static String getStringFromInputStream(InputStream is) throws IOException {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while((line = br.readLine()) != null) {
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

    private void controlRequestUpload() {
        WebCheckResult wcr = WebCheck.checkNetworkAvailability(getApplicationContext());
        if (wcr.isWifiConnected()||wcr.isMobileConnected()) {
            Log.i(LOG, "WIFI connected, preparing to send cached requests to the cloud");
            RequestList list = new RequestList();
            for (RequestCacheEntry rce : requestCache.getRequestCacheEntryList()) {
                list.getRequests().add(rce.getRequest());
            }
            if (list.getRequests().isEmpty()) {
                Log.d(LOG, "There are no requests that have been cached");
                requestSyncListener.onTasksSynced(0, 0);
                return;
            }
            Log.w(LOG, "sending cached request list:" + list.getRequests().size());
            WebSocketUtilForRequests.sendRequest(getApplicationContext(), list, new WebSocketUtilForRequests.WebSocketListener() {
                @Override
                public void onMessage(ResponseDTO response) {
                    if (!ErrorUtil.checkServerError(getApplicationContext(), response)) {
                        return;
                    }
                    Log.i(LOG, "Cached requests have been sent. positive responses: " + response.getGoodCount() +
                            "negative responses:" + response.getBadCount());

                    for (RequestCacheEntry rce : requestCache.getRequestCacheEntryList()) {
                        rce.setDateUploaded(new Date());
                    }
                    cleanUpCache();
                    requestSyncListener.onTasksSynced(response.getGoodCount(), response.getBadCount());
                }

                @Override
                public void onClose() {
                    Log.i(LOG, "FIRED onClose");
                }

                @Override
                public void onError(String message) {
                    requestSyncListener.onError(message);
                }
            });
        } else {
            Log.e(LOG, "unable to connect WIFI, unable to sync requests");
        }
    }

    private void cleanUpCache() {
        List<RequestCacheEntry> list = new ArrayList<>();
        for (RequestCacheEntry rce : requestCache.getRequestCacheEntryList()) {
            if (rce.getDateUploaded() == null) {
                list.add(rce);
            }
        }
        Log.i(LOG, "cache is now clean: " + list.size());
        requestCache.setRequestCacheEntryList(list);
        CacheUtil.cacheRequest(getApplicationContext(), requestCache, null);
    }

    private void print() {
        for (RequestCacheEntry rce : requestCache.getRequestCacheEntryList()) {
            Log.w(LOG, "++" + rce.getDateRequested() + "requestType: " + rce.getRequest().getRequestType());

        }
    }
    public class LocalBinder extends Binder {
        public RequestSyncService getService() {
            return RequestSyncService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {return mBinder; }
    private final IBinder mBinder = new LocalBinder();
    public void startSyncCachedRequests(RequestSyncListener rsl) {
        requestSyncListener = rsl;
        onHandleIntent(null);
    }

    public interface RequestSyncListener {
        public void onTasksSynced(int goodResponses, int badResponses);
        public void onError(String message);
    }

}
