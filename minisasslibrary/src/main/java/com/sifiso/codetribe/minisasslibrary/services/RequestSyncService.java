package com.sifiso.codetribe.minisasslibrary.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sifiso.codetribe.minisasslibrary.dto.InsectImageDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestList;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheck;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheckResult;
import com.sifiso.codetribe.minisasslibrary.util.CacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.ErrorUtil;
import com.sifiso.codetribe.minisasslibrary.util.NetUtil;
import com.sifiso.codetribe.minisasslibrary.util.RequestCacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.NetUtilForRequests;

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

    static final String LOG = RequestSyncService.class.getSimpleName();
    static final Gson gson = new Gson();
    RequestSyncListener requestSyncListener;
    RequestCache requestCache;

    public RequestSyncService() {
        super("RequestSyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.w(LOG, "-------->>> RequestSyncService, onHandleIntent");
        WebCheckResult wcr = WebCheck.checkNetworkAvailability(getApplicationContext());
        if (!wcr.isWifiConnected() && !wcr.isMobileConnected()) {
            Log.e(LOG,"No network connection, service quitting");
            return;
        }
        FileInputStream stream;
        try {

            stream = getApplicationContext().openFileInput("requestCache.json");
            String json = getStringFromInputStream(stream);
            Log.e(LOG, "json from cache: \n" + json);
            RequestCache cache = gson.fromJson(json, RequestCache.class);
            if (cache != null) {
                requestCache = cache;
                Log.i(LOG, "RequestCache returned From disk with the following entries: "
                        + requestCache.getRequestCacheEntryList().size());
                print();
                for (RequestCacheEntry rce : requestCache.getRequestCacheEntryList()) {
                    requests.add(rce.getRequest());
                }
                cleanUpCache();
                startUploads();
            } else {
                Log.e(LOG, "requestCache is null");
                requestSyncListener.onTasksSynced(0, 0);
            }
        } catch (JsonSyntaxException e1) {
            Log.e(LOG, "JsonSyntaxException, there is no request cache currently");
            restoreCache();
            requestSyncListener.onTasksSynced(0, 0);
        } catch (FileNotFoundException e) {
            Log.e(LOG, "fileNotFoundException, there is no request cache currently");
            restoreCache();
            requestSyncListener.onTasksSynced(0, 0);
        } catch (Exception e) {
            Log.e(LOG, "there's an issue with the sync", e);
            restoreCache();
            requestSyncListener.onTasksSynced(0, 0);
        }

    }

    private void restoreCache() {
        for (RequestDTO w: requests) {
            RequestCacheUtil.addRequest(getApplicationContext(),w,null);
        }
        Log.i(LOG,"Requests restored to cache");

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

    private void controlRequestUploadx() {
        WebCheckResult wcr = WebCheck.checkNetworkAvailability(getApplicationContext());
        if (wcr.isWifiConnected() || wcr.isMobileConnected()) {
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
            Log.w(LOG, "sending cached request list after clearing unneeded fields: " + list.getRequests().size());
            for (RequestDTO w: list.getRequests()) {



            }
            NetUtilForRequests.sendRequest(getApplicationContext(), Statics.REQUEST_SERVLET, list, new NetUtilForRequests.NetListener() {
                @Override
                public void onMessage(ResponseDTO response) {
                    if (!ErrorUtil.checkServerError(getApplicationContext(), response)) {
                        return;
                    }
                    Log.i(LOG, "Cached requests have been sent. positive responses: " + response.getGoodCount() +
                            " negative responses: " + response.getBadCount());

                    for (RequestCacheEntry rce : requestCache.getRequestCacheEntryList()) {
                        rce.setDateUploaded(new Date().getTime());
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

    List<RequestDTO> requests = new ArrayList<>();
    int index;

    private void startUploads() {
        if (requests.isEmpty()) {
            Log.w(LOG,"***** no requests to upload");
            return;
        }
        index = 0;
        if (requests.isEmpty()) {
            return;
        }
        sendRequest(requests.get(index));

    }
    private void sendRequest(RequestDTO w) {

        for (InsectImageDTO m: w.getInsectImages()) {
            m.setInsectimagelistList(null);
        }

        Log.e(LOG,"###########################################......index: "+index+" ........Sending request to server: \n" + gson.toJson(w));
        NetUtil.sendRequest(getApplicationContext(), Statics.SERVLET_ENDPOINT, w, new NetUtil.NetListener() {
            @Override
            public void onMessage(ResponseDTO response) {
                index++;
                if (index < requests.size()) {
                    sendRequest(requests.get(index));
                } else {
                    Log.e(LOG,"+++++++++++++++++++++++++++++++++++++++++++++ Request upload completed, uploaded " + (index));
                    cleanUpCache();
                    Intent m = new Intent(BROADCAST_REQUESTS_UPLOADED);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(m);
                }
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(String message) {
                Log.e(LOG,message);
            }
        });
    }
    public static final String BROADCAST_REQUESTS_UPLOADED = "com.boha.BROADCAST_REQUESTS_UPLOADED";
    private void cleanUpCache() {
        requestCache = new RequestCache();
        requestCache.setRequestCacheEntryList(new ArrayList<RequestCacheEntry>());
        CacheUtil.cacheRequest(getApplicationContext(), requestCache, null);
        Log.i(LOG, "....emptied request cache ....... ");
    }

    private void print() {
        for (RequestCacheEntry rce : requestCache.getRequestCacheEntryList()) {
            Log.w(LOG, "+++++++ DATE: " + rce.getDateRequested() + "requestType: " + rce.getRequest().getRequestType());
        }
    }

    public class LocalBinder extends Binder {
        public RequestSyncService getService() {
            return RequestSyncService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new LocalBinder();

    public void startSyncCachedRequests(RequestSyncListener rsl) {
        Log.w(LOG,"+++++++++++++ startSyncCachedRequests ..........");
        requestSyncListener = rsl;
        onHandleIntent(null);
    }

    public interface RequestSyncListener {
        public void onTasksSynced(int goodResponses, int badResponses);

        public void onError(String message);
    }

}
