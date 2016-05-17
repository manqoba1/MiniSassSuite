package com.sifiso.codetribe.minisasslibrary.util;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.activities.MSApp;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.toolbox.BaseVolley;
import com.snappydb.DB;
import com.snappydb.SnappydbException;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by aubreymalabie on 4/25/16.
 * Get rivers around me
 * 1. Get rivers without riverParts
 * 2. For each river, get riverParts in the background
 * 3. Cache the river
 * <p/>
 * 4. get river from cache
 */
public class RiverDataWorker {

    static final String LOG = RiverDataWorker.class.getSimpleName();


    public static void refreshRiver(final MSApp app, final Context ctx, Integer riverID, final RiverSearchListener listener) {
        RequestDTO w = new RequestDTO(RequestDTO.GET_RIVER_DETAILS);
        w.setRiverID(riverID);

        BaseVolley.sendRequest(Statics.SERVLET_ENDPOINT, w, app, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(final ResponseDTO response) {
                RiverDataWorker.cacheRivers(app, response.getRiverList(), new RiverCacheListener() {
                    @Override
                    public void onRiversCached() {
                        listener.onResponse(response.getRiverList());
                    }

                    @Override
                    public void onRiversFound(List<RiverDTO> rivers) {

                    }

                    @Override
                    public void onError(String message) {
                        listener.onError(message);
                    }
                });
            }

            @Override
            public void onVolleyError(VolleyError error) {
                listener.onError(error.getMessage());
            }

            @Override
            public void onError(String message) {
                listener.onError(message);
            }
        });
    }

    public static void getRiversAroundMe(final MSApp app, final Context ctx,
                                         final double latitude, final double longitude, int radius,
                                         final boolean isDialog,
                                         final RiverSearchListener listener) {
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.LIST_DATA_WITH_RADIUS_RIVERS);
        w.setLatitude(latitude);
        w.setLongitude(longitude);
        w.setRadius(radius);
        w.setZipResponse(true);

        final long start = System.currentTimeMillis();
        Log.w(LOG, "####### getRiversAroundMe: sending request with radius: " + radius);
        BaseVolley.sendRequest(Statics.SERVLET_ENDPOINT, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO r) {
                final long end = System.currentTimeMillis();
                Log.e(LOG, "### getRiversAroundMe, found: " + r.getRiverList().size() + ", elapsed: " + (end - start) / 1000 + " seconds");
                listener.onResponse(r.getRiverList());
                //cache lookups in one chunk ----
                cacheLookups(app, r, null);

                Log.d(LOG, "...........check if river has points in cache, else fetch points from server and cache the result");
                final long start2 = System.currentTimeMillis();

                getRiverDetails(ctx, app, r.getRiverList(), isDialog, new RiverSearchListener() {
                    @Override
                    public void onResponse(List<RiverDTO> rivers) {
                        if (!rivers.isEmpty()) {
                            Log.d(LOG, "$$$$ River points found, hope all good in cache: " + rivers.get(0).getRiverName());
                        }
                        long end2 = System.currentTimeMillis();
                        Log.d(LOG, "####### River points found in cache or server: elapsed: " + (end2 - start2) / 1000 + " seconds");
                    }

                    @Override
                    public void onError(String message) {
                        listener.onError(message);
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


    private static void getRiverDetails(final Context ctx, final MSApp app,
                                        final List<RiverDTO> rivers, final boolean isDialog, final RiverSearchListener listener) {
        Log.e(LOG, "....................Check if rivers are in cache, else get it's details");
        List<Integer> ids = new ArrayList<>();
        for (RiverDTO r : rivers) {
            ids.add(r.getRiverID());
        }
        getRiversByIDs(app, ids, new RiverCacheListener() {
            @Override
            public void onRiversCached() {

            }

            @Override
            public void onRiversFound(List<RiverDTO> mlist) {
                Log.d(LOG, "Have found river points for: " + mlist.size());
                list = new ArrayList<>();
                for (RiverDTO r : rivers) {
                    for (RiverDTO x : mlist) {
                        if (r.getRiverID().intValue() == x.getRiverID().intValue()) {
                            r.setIgnore(true);
                        }
                    }
                }
                for (RiverDTO r : rivers) {
                    if (!r.isIgnore()) {
                        list.add(r);
                    }
                }
                Log.e(LOG, "Have to get river points for: " + list.size());
                index = 0;
                control(ctx, app, isDialog);
            }

            @Override
            public void onError(String message) {

            }
        });


    }


    static int index = 0;
    static List<RiverDTO> list;
    public static final String BROADCAST_POINTS_RETRIEVED = "com.boha.BROADCAST_POINTS", BROADCAST_DIALOG = "com.boha.DIALOG";

    private static void control(final Context ctx, final MSApp app, boolean isDialog) {
        if (index < list.size()) {
            getRiverPoints(ctx, app, list.get(index).getRiverID(), isDialog);
            return;
        }

        Log.e(LOG, "*********************************** Broadcast complete points retrieved");

        if (isDialog) {
            Intent m = new Intent(BROADCAST_DIALOG);
            LocalBroadcastManager.getInstance(ctx).sendBroadcast(m);
        } else {
            Intent m = new Intent(BROADCAST_POINTS_RETRIEVED);
            LocalBroadcastManager.getInstance(ctx).sendBroadcast(m);
        }
    }

    private static void getRiverPoints(final Context ctx, final MSApp app,
                                       Integer riverID, final boolean isDialog) {
        RequestDTO w = new RequestDTO(RequestDTO.GET_RIVER_DETAILS);
        w.setRiverID(riverID);

        final long start = System.currentTimeMillis();
        Log.w(LOG, "****** getRiverPoints: sending river points request for river: " + riverID);
        BaseVolley.sendRequest(Statics.SERVLET_ENDPOINT, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(final ResponseDTO response) {
                final long end = System.currentTimeMillis();
                Log.e(LOG, "********** request elapsed time: " + (end - start) / 100 + " seconds");
                cacheRivers(app, response.getRiverList(), new RiverCacheListener() {
                    @Override
                    public void onRiversCached() {
                        index++;
                        control(ctx, app, isDialog);
                    }

                    @Override
                    public void onRiversFound(List<RiverDTO> rivers) {

                    }

                    @Override
                    public void onError(String message) {

                    }
                });

            }

            @Override
            public void onVolleyError(VolleyError error) {
                Log.e(LOG, "ERROR: " + error.getMessage());
            }

            @Override
            public void onError(String message) {
                Log.e(LOG, "ERROR: " + message);
            }
        });
    }

    public interface LookupsListener {
        void onLookupsRetrieved(ResponseDTO response);

        void onLookupsCached();

        void onError(String message);
    }

    public interface RiverSearchListener {
        void onResponse(List<RiverDTO> rivers);

        void onError(String message);
    }

    public interface RiverCacheListener {
        void onRiversCached();

        void onRiversFound(List<RiverDTO> rivers);

        void onError(String message);
    }

    static final Gson GSON = new Gson();
    static final String RIVER = "RIVER";
    static DB snappyDB;

    public static void cacheLookups(MSApp app, ResponseDTO resp, LookupsListener listener) {
        snappyDB = app.getSnappyDB();
        new MTask(true, resp, listener).execute();
    }

    public static void getLookups(MSApp app, LookupsListener listener) {
        snappyDB = app.getSnappyDB();
        new MTask(true, listener).execute();
    }

    public static void cacheRivers(MSApp app, List<RiverDTO> list, RiverCacheListener listener) {
        snappyDB = app.getSnappyDB();
        new MTask(list, listener).execute();
    }

    public static void getRivers(MSApp app, RiverCacheListener listener) {
        snappyDB = app.getSnappyDB();
        new MTask(listener).execute();
    }

    public static void getRiverByID(MSApp app, Integer riverID, RiverCacheListener listener) {
        snappyDB = app.getSnappyDB();
        new MTask(riverID, listener).execute();
    }

    public static void getRiversByIDs(MSApp app, List<Integer> riverIDs, RiverCacheListener listener) {
        snappyDB = app.getSnappyDB();
        new MTask(riverIDs, true, listener).execute();
    }


    private static class MTask extends AsyncTask<Void, Void, Integer> {
        static final int GET_RIVER_BY_ID = 1, GET_ALL_RIVERS = 2,
                CACHE_RIVERS = 3, GET_RIVERS_BY_IDS = 4,
                CACHE_LOOKUPS = 6, GET_LOOOKUPS = 7;
        Integer riverID;
        List<RiverDTO> rivers;
        List<Integer> riverIDs;
        int type;
        RiverCacheListener listener;
        ResponseDTO response;
        LookupsListener lookupsListener;

        public MTask(RiverCacheListener listener) {
            type = GET_ALL_RIVERS;
            this.listener = listener;
        }

        public MTask(Integer riverID, RiverCacheListener listener) {
            type = GET_RIVER_BY_ID;
            this.riverID = riverID;
            this.listener = listener;
        }

        public MTask(List<RiverDTO> rivers, RiverCacheListener listener) {
            type = CACHE_RIVERS;
            this.rivers = rivers;
            this.listener = listener;
        }

        public MTask(List<Integer> riverIDs, boolean b, RiverCacheListener listener) {
            type = GET_RIVERS_BY_IDS;
            this.riverIDs = riverIDs;
            this.listener = listener;
        }

        public MTask(boolean isCaching, ResponseDTO response, LookupsListener listener) {
            type = CACHE_LOOKUPS;
            this.response = response;
            this.lookupsListener = listener;
        }

        public MTask(boolean isRetreiving, LookupsListener listener) {
            type = GET_LOOOKUPS;
            this.lookupsListener = listener;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            try {
                switch (type) {
                    case CACHE_LOOKUPS:
                        String jsonx = GSON.toJson(response);
                        snappyDB.put("LOOKUPS", jsonx);
                        Log.d(LOG, "+++++++++++++++++ Lookups cached ******");
                        break;
                    case GET_LOOOKUPS:
                        String jsonz = snappyDB.get("LOOKUPS");
                        response = GSON.fromJson(jsonz, ResponseDTO.class);
                        Log.d(LOG, "----------------- Lookups retrieved ******");
                        break;
                    case GET_RIVERS_BY_IDS:
                        rivers = new ArrayList<>();
                        for (Integer id : riverIDs) {
                            try {
                                String json = snappyDB.get(RIVER + id.intValue());
                                RiverDTO r = GSON.fromJson(json, RiverDTO.class);
                                rivers.add(r);

                            } catch (Exception e) {

                            }
                        }
                        Log.e(LOG, "------------- GET_RIVERS_BY_IDS found: " + rivers.size());
                        break;
                    case CACHE_RIVERS:
                        for (RiverDTO r : rivers) {
                            String json = GSON.toJson(r);
                            Log.d(LOG, "....caching river, riverID: " + r.getRiverID()
                                    + " - " + r.getRiverName());
                            snappyDB.put(RIVER + r.getRiverID().intValue(), json);
                        }
                        String[] x = snappyDB.findKeys(RIVER);
                        Log.w(LOG, "######## rivers cached this session: " + rivers.size()
                                + " total rivers in cache: " + x.length);
                        break;
                    case GET_RIVER_BY_ID:
                        rivers = new ArrayList<>();
                        try {
                            String json = snappyDB.get(RIVER + riverID.intValue());
                            if (json != null) {
                                RiverDTO river = GSON.fromJson(json, RiverDTO.class);
                                rivers.add(river);
                                Log.w(LOG, "####### GET_RIVER_BY_ID: River found in cache: "
                                        + river.getRiverName() + " sites: " +
                                        river.getEvaluationsiteList().size());
                            }
                        } catch (SnappydbException e) {
                            Log.e(LOG, "####### GET_RIVER_BY_ID: River NOT found in cache, riverID: "
                                    + riverID);
                        }
                        break;
                    case GET_ALL_RIVERS:
                        rivers = new ArrayList<>();
                        String[] keys = snappyDB.findKeys(RIVER);
                        for (String key : keys) {
                            String j = snappyDB.get(key);
                            RiverDTO r = GSON.fromJson(j, RiverDTO.class);
                            rivers.add(r);
                        }
                        Log.w(LOG, "####### Rivers found in cache: " + rivers.size());
                        break;
                }
            } catch (Exception e) {
                Log.e(LOG, "Failed river cache", e);
                return 9;
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result > 0) {
                listener.onError("Failed to work with River cache");
                return;
            }
            switch (type) {
                case CACHE_RIVERS:
                    if (listener != null)
                        listener.onRiversCached();
                    break;
                case GET_ALL_RIVERS:
                    listener.onRiversFound(rivers);
                    break;
                case GET_RIVER_BY_ID:
                    listener.onRiversFound(rivers);
                    break;
                case GET_RIVERS_BY_IDS:
                    listener.onRiversFound(rivers);
                    break;
                case CACHE_LOOKUPS:
                    if (lookupsListener != null)
                        lookupsListener.onLookupsCached();
                    break;
                case GET_LOOOKUPS:
                    lookupsListener.onLookupsRetrieved(response);
                    break;
            }
        }

    }


}

