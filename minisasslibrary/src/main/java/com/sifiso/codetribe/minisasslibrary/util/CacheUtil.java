package com.sifiso.codetribe.minisasslibrary.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.dto.RiverDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.services.RequestCache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by aubreyM on 2014/06/30.
 */
public class CacheUtil implements Serializable {

    public interface CacheUtilListener {
        public void onFileDataDeserialized(ResponseDTO response);

        public void onDataCached(ResponseDTO response);

        public void onError();
    }

    public interface CacheRequestListener {
        public void onDataCached();

        public void onRequestCacheReturned(RequestCache cache);

        public void onError();
    }


    static CacheUtilListener utilListener;
    static CacheRequestListener cacheListener;
    public static final int CACHE_DATA = 1, CACHE_REGISTER_DATA = 2, CACHE_SEARCH_DATA = 3, CACHE_EVALUATION = 4, CACHE_REQUEST = 5, CACHE_RIVER = 6, CACHE_RIVER_DATA = 7;
    static int dataType;
    static Integer projectID;
    static ResponseDTO response;
    static RiverDTO river;
    static String countryCode;
    static Context ctx;
    static RequestCache requestCache;
    static final String JSON_DATA = "data.json", JSON_SEARCH = "search.json",
            JSON_EVALUATION_DATA = "evaluation_data", JSON_REGISTER_DATA = "register_data.json",
            JSON_REQUEST = "requestCache.json", JSON_RIVER = "river_data.json";


    public static void cacheRequest(Context context, RequestCache cache, CacheRequestListener listener) {
        requestCache = cache;
        dataType = CACHE_REQUEST;
        cacheListener = listener;
        ctx = context;
        new CacheTask().execute();
    }

    public static void cacheRegisterData(Context context, ResponseDTO r, int type, CacheUtilListener cacheUtilListener) {
        dataType = type;
        response = r;
        response.setLastCacheDate(new Date());
        utilListener = cacheUtilListener;
        ctx = context;
        new CacheTask().execute();
    }

    public static void getCachedRegisterData(Context context, int type, CacheUtilListener cacheUtilListener) {
        dataType = type;
        utilListener = cacheUtilListener;
        ctx = context;
        new CacheRetrieveTask().execute();
    }

    public static void cacheData(Context context, ResponseDTO r, int type, CacheUtilListener cacheUtilListener) {
        dataType = type;
        response = r;
        response.setLastCacheDate(new Date());
        utilListener = cacheUtilListener;
        ctx = context;
        new CacheTask().execute();
    }

    public static void getCachedRiverData(Context context, int type, CacheUtilListener cacheUtilListener) {
        dataType = type;
        utilListener = cacheUtilListener;
        ctx = context;
        new CacheRetrieveTask().execute();
    }

    public static void cacheRiverData(Context context, ResponseDTO r, int type, CacheUtilListener cacheUtilListener) {
        dataType = type;
        response = r;
        response.setLastCacheDate(new Date());
        utilListener = cacheUtilListener;
        ctx = context;
        new CacheTask().execute();
    }

    public static void getCachedData(Context context, int type, CacheUtilListener cacheUtilListener) {
        dataType = type;
        utilListener = cacheUtilListener;
        ctx = context;
        new CacheRetrieveTask().execute();
    }


    public static void getCachedRequests(Context context, CacheRequestListener listener) {
        dataType = CACHE_REQUEST;
        cacheListener = listener;
        ctx = context;
        new CacheRetrieveRequestTask().execute();
    }


    static class CacheTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            String json = null;
            File file = null;
            FileOutputStream outputStream;
            try {
                switch (dataType) {

                    case CACHE_REQUEST:
                        Log.w(LOG, "## before caching request file, list: " + requestCache.getRequestCacheEntryList().size());
                        json = gson.toJson(requestCache);
                        outputStream = ctx.openFileOutput(JSON_REQUEST, Context.MODE_PRIVATE);
                        write(outputStream, json);
                        file = ctx.getFileStreamPath(JSON_REQUEST);
                        if (file != null) {
                            Log.e(LOG, "Request cache written, path: " + file.getAbsolutePath() +
                                    " - length: " + file.length());
                        }
                        break;
                    case CACHE_EVALUATION:
                        json = gson.toJson(response);

                        break;
                    case CACHE_RIVER:
                        json = gson.toJson(response);
                        outputStream = ctx.openFileOutput(JSON_RIVER, Context.MODE_PRIVATE);
                        write(outputStream, json);
                        file = ctx.getFileStreamPath(JSON_RIVER);
                        if (file != null) {
                            Log.e(LOG, "Data cache written, path: " + file.getAbsolutePath() +
                                    " - length: " + file.length());
                        }
                        break;
                    case CACHE_REGISTER_DATA:
                        json = gson.toJson(response);
                        outputStream = ctx.openFileOutput(JSON_REGISTER_DATA, Context.MODE_PRIVATE);
                        write(outputStream, json);
                        file = ctx.getFileStreamPath(JSON_REGISTER_DATA);
                        if (file != null) {
                            Log.e(LOG, "Data cache written, path: " + file.getAbsolutePath() +
                                    " - length: " + file.length());
                        }
                        break;
                    case CACHE_DATA:
                        json = gson.toJson(response);
                        outputStream = ctx.openFileOutput(JSON_DATA, Context.MODE_PRIVATE);
                        write(outputStream, json);
                        file = ctx.getFileStreamPath(JSON_DATA);
                        if (file != null) {
                            Log.e(LOG, "Data cache written, path: " + file.getAbsolutePath() +
                                    " - length: " + file.length());
                        }
                        break;
                    case CACHE_SEARCH_DATA:
                        json = gson.toJson(response);
                        outputStream = ctx.openFileOutput(JSON_SEARCH, Context.MODE_PRIVATE);
                        write(outputStream, json);
                        file = ctx.getFileStreamPath(JSON_SEARCH);
                        if (file != null) {
                            Log.e(LOG, "Country cache written, path: " + file.getAbsolutePath() +
                                    " - length: " + file.length());
                        }
                        break;
                    case CACHE_RIVER_DATA:
                        json = gson.toJson(response);
                        outputStream = ctx.openFileOutput(JSON_RIVER, Context.MODE_PRIVATE);
                        write(outputStream, json);
                        file = ctx.getFileStreamPath(JSON_RIVER);
                        if (file != null) {
                            Log.e(LOG, "Country cache written, path: " + file.getAbsolutePath() +
                                    " - length: " + file.length());
                        }
                        break;
                    default:
                        Log.e(LOG, "######### NOTHING done ...");
                        break;
                }

            } catch (IOException e) {
                Log.e(LOG, "Failed to cache data", e);
                return 9;
            }
            return 0;
        }

        private void write(FileOutputStream outputStream, String json) throws IOException {
            outputStream.write(json.getBytes());
            outputStream.close();
        }

        @Override
        protected void onPostExecute(Integer v) {
            if (utilListener != null) {
                if (v > 0) {
                    utilListener.onError();
                } else

                    utilListener.onDataCached(response);
            }
            if (cacheListener != null) {
                if (v > 0) {
                    cacheListener.onError();
                } else {
                    cacheListener.onDataCached();
                }
            }


        }
    }

    static class CacheRetrieveTask extends AsyncTask<Void, Void, ResponseDTO> {

        private ResponseDTO getData(FileInputStream stream) throws IOException {
            String json = getStringFromInputStream(stream);
            ResponseDTO response = gson.fromJson(json, ResponseDTO.class);
            return response;
        }

        @Override
        protected ResponseDTO doInBackground(Void... voids) {
            ResponseDTO response = new ResponseDTO();
            FileInputStream stream;
            try {
                switch (dataType) {
                    case CACHE_RIVER:
                        stream = ctx.openFileInput(JSON_RIVER);
                        response = getData(stream);
                        Log.i(LOG, "++ river data cache retrieved");
                        break;
                    case CACHE_EVALUATION:

                        break;
                    case CACHE_REGISTER_DATA:
                        stream = ctx.openFileInput(JSON_REGISTER_DATA);
                        response = getData(stream);
                        Log.i(LOG, "++ company data cache retrieved");
                        break;
                    case CACHE_REQUEST:
                        stream = ctx.openFileInput(JSON_REQUEST);
                        response = getData(stream);
                        Log.i(LOG, "++ request cache retrieved");
                        break;

                    case CACHE_DATA:
                        stream = ctx.openFileInput(JSON_DATA);
                        response = getData(stream);
                        Log.i(LOG, "++ company data cache retrieved");
                        break;
                    case CACHE_SEARCH_DATA:
                        stream = ctx.openFileInput(JSON_SEARCH);
                        response = getData(stream);
                        Log.i(LOG, "++ country cache retrieved");
                        break;

                    case CACHE_RIVER_DATA:
                        stream = ctx.openFileInput(JSON_RIVER);
                        response = getData(stream);
                        Log.i(LOG, "++ country cache retrieved");
                        break;

                }
                response.setStatusCode(0);

            } catch (FileNotFoundException e) {
                Log.d(LOG, "#### cache file not found - returning a new response object, type = " + dataType);

            } catch (IOException e) {
                Log.v(LOG, "------------ Failed to retrieve cache", e);
            }

            return response;
        }

        @Override
        protected void onPostExecute(ResponseDTO v) {
            if (utilListener == null) return;
            utilListener.onFileDataDeserialized(v);


        }
    }


    static class CacheRetrieveRequestTask extends AsyncTask<Void, Void, RequestCache> {

        private RequestCache getData(FileInputStream stream) throws IOException {
            String json = getStringFromInputStream(stream);
            RequestCache cache = gson.fromJson(json, RequestCache.class);
            return cache;
        }

        @Override
        protected RequestCache doInBackground(Void... voids) {
            RequestCache cache = null;
            FileInputStream stream;
            try {
                stream = ctx.openFileInput(JSON_REQUEST);
                cache = getData(stream);
                Log.i(LOG, "++ request cache retrieved");
            } catch (FileNotFoundException e) {
                Log.d(LOG, "## cache file not found. not initialised yet. no problem, creating new cache");
                cache = new RequestCache();

            } catch (IOException e) {
                Log.v(LOG, "-- Failed to retrieve cache", e);
            }

            return cache;
        }

        @Override
        protected void onPostExecute(RequestCache v) {
            if (cacheListener == null) return;
            if (v != null) {
                cacheListener.onRequestCacheReturned(v);
            } else {
                Log.e(LOG, "------ No cache, util returns null response object");
                cacheListener.onError();
            }

        }
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

    static final String LOG = CacheUtil.class.getSimpleName();
    static final Gson gson = new Gson();


}
