package com.sifiso.codetribe.minisasslibrary.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ImagesDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Chris on 2015-02-19.
 */
public class PhotoCacheUtil {

    static ResponseDTO response = new ResponseDTO();
    static ImagesDTO images;
    static Context ctx;
    static final String JSON_PHOTO = "photos.json";
    static final Gson gson = new Gson();
    static final String LOG = PhotoUploadService.class.getSimpleName();

    public interface PhotoCacheListener {
        public void onFileDataDeserialized(ResponseDTO response);
        public void onDataCached();
        public void onError();

    }
    public interface PhotoCacheRetrieveListener {
        public void onFileDataDeserialized(ResponseDTO response);
        public void onDataCached(

        );
        public void onError();
    }

    static PhotoCacheListener photoCacheListener;
    static PhotoCacheRetrieveListener photoCacheRetrieveListener =
            new PhotoCacheRetrieveListener() {
                @Override
                public void onFileDataDeserialized(ResponseDTO response) {

                }

                @Override
                public void onDataCached() {

                }

                @Override
                public void onError() {

                }
            };

    private static String getStringFromInputStream(InputStream is) throws IOException {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }finally {
            if (br != null) {
                br.close();
            }
        }
        String json = sb.toString();
        return json;
    }

    public static void cachePhoto(Context context, final ImagesDTO image, PhotoCacheListener listener) {

        images = image;
        response.setLastCacheDate(new Date());
        photoCacheListener = listener;
        ctx = context;
        new CacheRetrieveForUpdateTask().execute();
    }

    public static void getCachedPhotos(Context context, PhotoCacheListener listener) {
        photoCacheListener = listener;
        ctx = context;
        new CacheRetrieveTask().execute();
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
            response.setImagesList(new ArrayList<ImagesDTO>());
            FileInputStream stream;
            try {
                stream = ctx.openFileInput(JSON_PHOTO);
                response = getData(stream);
                Log.i(LOG, "photo cache retrieved photos: " + response.getImagesList().size());

            } catch (FileNotFoundException e){
                Log.w(LOG, "cache file not found, not initialised yet");
                return response;
            } catch (IOException e) {
                Log.e(LOG, "doInBackground - returning new response object");

            }
            return response;
        }
        @Override
        protected void onPostExecute(ResponseDTO re) {
            if (photoCacheListener == null) {
                return;
            } else {
                photoCacheListener.onFileDataDeserialized(re);
            }
        }
    }

    static class CacheRetrieveForUpdateTask extends AsyncTask<Void, Void, ResponseDTO> {

        private ResponseDTO getData(FileInputStream stream) throws IOException {
            String json = getStringFromInputStream(stream);
            ResponseDTO response = gson.fromJson(json, ResponseDTO.class);
            return response;
        }

        @Override
        protected ResponseDTO doInBackground(Void... voids) {
            ResponseDTO response = new ResponseDTO();
            response.setImagesList(new ArrayList<ImagesDTO>());
            FileInputStream stream;
            try {
                stream = ctx.openFileInput(JSON_PHOTO);
                response = getData(stream);
            }catch (FileNotFoundException e) {
                Log.w(LOG, "cache file not found, not initialised yet");
                return response;
            }catch (IOException e){
                Log.d(LOG, "doInBackground - returning a new response");
            }
            return response;
        }
        @Override
        protected void onPostExecute(ResponseDTO re) {
            if (re.getImagesList() == null) {
                re.setImagesList(new ArrayList<ImagesDTO>());
            }
            re.getImagesList().add(images);
            response = re;
            new CacheTask().execute();
        }


    }

    public static void clearCache(Context context,
                                  final List<ImagesDTO> uploadedList) {

        ctx = context;
        getCachedPhotos(context, new PhotoCacheListener() {
            @Override
            public void onFileDataDeserialized(ResponseDTO r) {
                List<ImagesDTO> pending = new ArrayList<>();
                for (ImagesDTO i : r.getImagesList()) {
                    for (ImagesDTO im : uploadedList) {
                        if (im.getThumbFilePath().equalsIgnoreCase(i.getThumbFilePath())) {
                            i.setDateUploaded(new Date());
                            File f = new File(im.getThumbFilePath());
                            if (f.exists()) {
                                boolean del = f.delete();
                                Log.w(LOG, "deleted Image file: " + im.getThumbFilePath() + " - " + del);

                            }
                        }

                    }
                    for (ImagesDTO ima : r.getImagesList()) {
                        if (ima.getDateUploaded() == null) {
                            pending.add(ima);
                        }
                    }
                }
                r.setImagesList(pending);
                response = r;
                Log.i(LOG, "photo's pending, after clearing cache: " + pending.size()
                + "new cache ");
                new CacheTask().execute();
            }

            @Override
            public void onDataCached() {
                Log.i(LOG, "DataCached Fired");
            }

            @Override
            public void onError() {
            Log.i(LOG, "onError Fired");
            }
        });
    }

    static class CacheTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            String json;
            File file;
            FileOutputStream outputStream;
            try {
                json = gson.toJson(response);
                outputStream = ctx.openFileOutput(JSON_PHOTO, Context.MODE_PRIVATE);
                Write(outputStream, json);
                file = ctx.getFileStreamPath(JSON_PHOTO);
                if (file != null) {
                    Log.e(LOG, "Photo cache path has been written: " + file.getAbsolutePath()
                    + " - length: " + file.length() + "photos: " + response.getImagesList().size());
                }
                StringBuilder sb = new StringBuilder();
                sb.append("\n Photos in cache\n");
                for (ImagesDTO i : response.getImagesList()) {
                    sb.append("accuracy:").append(i.getAccuracy())
           //                 .append(i.getUri())
                      //      .append("evaluationImageID: " + i.getEvaluationImageID())
                            .append("\n");
                }
                sb.append("--->");
                Log.w(LOG, sb.toString());

            } catch (IOException e) {
                Log.e(LOG, "data cache failed", e);
                return 9;
            }

            return 0;

        }
        private void Write(FileOutputStream outputStream, String json)
            throws IOException {
            outputStream.write(json.getBytes());
            outputStream.close();
        }

        @Override
        protected void onPostExecute(Integer v) {
            if (photoCacheListener != null) {
                if (v > 0) {
                    photoCacheListener.onError();
                } else {
                    photoCacheListener.onDataCached();
                    Log.e(LOG, "onDataCached fired");
                }
            }
        }
    }


}
