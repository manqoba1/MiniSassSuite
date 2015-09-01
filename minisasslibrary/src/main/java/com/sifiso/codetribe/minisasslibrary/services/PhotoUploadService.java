package com.sifiso.codetribe.minisasslibrary.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ImagesDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheck;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheckResult;
import com.sifiso.codetribe.minisasslibrary.util.PhotoCacheUtil;
import com.sifiso.codetribe.minisasslibrary.util.PictureUtil;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Chris on 2015-02-25.
 */
public class PhotoUploadService extends IntentService {

    UploadListener uploadListener;
    int count, index;
    static final int MAX_RETRIES = 3;
    int retryCount;
    private final IBinder mBinder = new LocalBinder();
    List<ImagesDTO> failedUploads =new ArrayList<>();
    List<ImagesDTO> uploadedList = new ArrayList<>();
    static List<ImagesDTO> list;
    WebCheckResult webCheckResult;
    static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");



    public void uploadCachedPhotos(UploadListener listener) {
        uploadListener = listener;
        Log.d(LOG, "uploadCachedPhotos, getting cached photos once the wifi is up");
        PhotoCacheUtil.getCachedPhotos(getApplicationContext(), new PhotoCacheUtil.PhotoCacheListener() {

            @Override
            public void onFileDataDeserialized(ResponseDTO response) {
                Log.e(LOG, "Photo's cached returning list: " + response.getImagesList().size());
                list = response.getImagesList();
                if (list.isEmpty()) {
                    Log.w(LOG, "unable to cache photo's for download");
                    if (uploadListener != null) {
                        uploadListener.onUploadsComplete(0);
                    }
                    return;

                }
                getLog(response);
                webCheckResult = WebCheck.checkNetworkAvailability(getApplicationContext());
                if (!webCheckResult.isWifiConnected()) {
                    Log.e(LOG, "uploadCachedPhotos is leaving, due to no wifi");
                    return;
                }
                onHandleIntent(null);
            }

            @Override
            public void onDataCached() {

            }

            @Override
            public void onError() {

            }
        });
    }

    private void attemptFailedUploads() {

        retryCount++;
        if (retryCount > MAX_RETRIES) {
            if (uploadListener != null) {
                uploadListener.onUploadsComplete(uploadedList.size());
            }
            return;
        }
        index = 0;
        list = failedUploads;
        failedUploads.clear();
        controlThumbUploads();
    }

    private static void getLog(ResponseDTO cache) {

        StringBuilder sb = new StringBuilder();
        sb.append("Photo's are now in the cache: ")
                .append(cache.getImagesList().size()).append("\n");
        for (ImagesDTO i : cache.getImagesList()) {
            sb.append("date: ").append(i.getDateTaken()).append("lng: ").append(i.getLongitude());
            sb.append("acc:").append(i.getAccuracy()).append("lat: ").append(i.getLatitude());
            if (i.getDateThumbUploaded() != null)
                sb.append(" ").append(sdf.format(i.getDateThumbUploaded())).append("\n");
            else
                sb.append("UPLOAD FAILED\n");
        }
        Log.w(LOG, sb.toString());
    }


    public class LocalBinder extends Binder {
        public PhotoUploadService getService() {
            return PhotoUploadService.this;
        }
    }
    public interface UploadListener {
        public void onUploadsComplete(int count);
    }


    public PhotoUploadService() {
        super("PhotoUploadService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        Log.w(LOG, "fired onHandleIntent");
        if (list == null) {
            uploadCachedPhotos(uploadListener);
            return;
        }
        retryCount = 1;
        webCheckResult = WebCheck.checkNetworkAvailability(getApplicationContext());
        if (webCheckResult.isWifiConnected()) {
            controlThumbUploads();
        }
    }

    private void controlThumbUploads() {
        if (index < list.size()) {
            executeThumbUpload(list.get(index));
        } else {
            index++;
            controlThumbUploads();
            return;
        }
        if (index == list.size()) {
            if (failedUploads.isEmpty()) {
                Log.w(LOG, "cache is clean");
                PhotoCacheUtil.clearCache(getApplicationContext(), uploadedList);
            } else {
                attemptFailedUploads();
            }
        }
    }

    private void executeThumbUpload(final ImagesDTO dto) {
        Log.d(LOG, "Fired executeThumbUpload, evaluationImageID: " + dto.getEvaluationImageID());
        dto.setFullPicture(false);
        if (dto.getPictureType() == 0) dto.setPictureType(ImagesDTO.EVALUATION_IMAGE);
        final long start = System.currentTimeMillis();
        PictureUtil.uploadImage(dto, false, getApplicationContext(), new ImagesDTO.PhotoUploadedListener() {

            @Override
            public void onPhotoUploaded() {
                long end = System.currentTimeMillis();
                Log.i(LOG, "Uploaded thumbNail, elapsed = " + Util.getElapsed(start, end) + "seconds");
                dto.setDateThumbUploaded(new Date());
                uploadedList.add(dto);
            }

            @Override
            public void onPhotoUploadFailed() {
                Log.e(LOG, "fired onPhotoUploadFailed");
                failedUploads.add(dto);
                index++;
                controlThumbUploads();
            }
        });
    }

    static final String LOG = PhotoUploadService.class.getSimpleName();
}
