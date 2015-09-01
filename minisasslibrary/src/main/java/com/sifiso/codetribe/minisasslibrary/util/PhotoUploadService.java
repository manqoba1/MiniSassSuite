package com.sifiso.codetribe.minisasslibrary.util;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.util.Log;

import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ImagesDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheck;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheckResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 2015-02-19.
 */
public class PhotoUploadService extends IntentService{
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    List<ImagesDTO> failedUploads = new ArrayList<>();
    static final String LOG = PhotoUploadService.class.getSimpleName();
    static final int MAX_RETRIES = 3;
    int retryCount, index;
    static List<ImagesDTO> list;
    WebCheckResult webCheckResult;
    UploadListener uploadListener;
    List<ImagesDTO> uploadedList = new ArrayList<>();
    static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");


    private  static void getLog(ResponseDTO cache) {
        StringBuilder sb = new StringBuilder();
        sb.append("photos currently in the cache: ")
                .append(cache.getImagesList().size()).append("\n");
        for (ImagesDTO i : cache.getImagesList()){
            sb.append(" ++:").append(i.getDateTaken().toString()).
                    append(" lng: ").append(i.getLongitude());
            sb.append(" lat: ").append(i.getLatitude()).append("pictureType").append(i.getPictureType());
            if (i.getDateThumbUploaded() != null)
                sb.append(" ").append(sdf.format(i.getDateThumbUploaded())).append("\n");
            else
            sb.append("Failed to upload\n");

        }
        Log.w(LOG, sb.toString());

    }


    public void UploadCachedPhotos(UploadListener listener) {
        uploadListener = listener;
        Log.d(LOG, "uploadCachedPhotos, getting cached photos - will  start when connected to wifi");
        PhotoCacheUtil.getCachedPhotos(getApplicationContext(), new PhotoCacheUtil.PhotoCacheListener() {
            @Override
            public void onFileDataDeserialized(ResponseDTO response) {
                Log.e(LOG, "cached photo list returned: " + response.getImagesList().size());
                list = response.getImagesList();
                if (list.isEmpty()) {
                    Log.w(LOG, "no cached photos for download");
                    if (uploadListener != null)
                        uploadListener.onUploadsComplete(0);
                    return;
                }
                getLog(response);
                webCheckResult = WebCheck.checkNetworkAvailability(getApplicationContext());
                if (!webCheckResult.isWifiConnected()) {
                    Log.e(LOG, "uploadedCachedPhotos exit.wifi not connected");
                    return;
                }

                onHandleIntent(null);
            }

            @Override
            public void onDataCached() {
            Log.i(LOG, "found photo's cached");
            }

            @Override
            public void onError() {
            Log.e(LOG, "Failed to getCachedPhotos");
            }
        });
    }


    public PhotoUploadService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    public interface UploadListener {
        public void onUploadsComplete(int count);
    }

    public class LocalBinder extends Binder {
        public PhotoUploadService getService() {
            return PhotoUploadService.this;
        }
    }
}

