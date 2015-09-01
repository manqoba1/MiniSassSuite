package com.sifiso.codetribe.minisasslibrary.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ImagesDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Chris on 2015-02-26.
 */
public class PictureUtil {

    public static void uploadImage (ImagesDTO dto , boolean isFullPicture,
                                      Context ctx, final ImagesDTO.PhotoUploadedListener listener) {

        if (dto.getDateUploaded() != null) return;
        if (dto.getThumbFilePath() == null) return;
        File imageFile = new File(dto.getThumbFilePath());
        if (isFullPicture) {
            imageFile = new File(dto.getImageFilePath());
        }
        Log.w(LOG, "FILE about to upload - length: " + imageFile.length() + " - " + imageFile.getAbsolutePath());
        List<File> files = new ArrayList<File>();
        if (imageFile.exists()) {
            files.add(imageFile);
            //setting up image uploading process
            ImageUpload.upload(dto, files, ctx,
                    new ImageUpload.ImageUploadListener(){
                        @Override
                    public void onUploadError() {
                            listener.onPhotoUploadFailed();
                            Log.e(LOG, "issue uploading - onUploadError");
                        }
                        @Override
                    public void onImageUploaded(ResponseDTO response) {
                            if (response.getStatusCode() == 0) {
                                listener.onPhotoUploaded();
                            } else {
                                Log.e(LOG, "Uploading error - onImageUploaded: " + response.getMessage()
                                );
                            }
                        }
                    });
        }


    }
    public static Uri getImageFileUri() {
        File rootDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        if (rootDir == null) {
            rootDir = Environment.getRootDirectory();
        }
        File imgDir = new File(rootDir, "minisass_app");
        if (!imgDir.exists()) {
            imgDir.mkdir();
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                .format(new Date());

        return Uri.fromFile(new File(imgDir,"CM_" + timeStamp + ".jpg"));

    }
    public static Intent getCameraIntent(ImageView image, Uri fileUri) {

        int w = image.getWidth();
        int h = image.getWidth();
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra("crop", "true");
        cameraIntent.putExtra("outputX", w);
        cameraIntent.putExtra("outputY", h);
        cameraIntent.putExtra("aspectX", 1);
        cameraIntent.putExtra("aspectY", 1);
        cameraIntent.putExtra("scale", true);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        return cameraIntent;

    }

    private static final String LOG = "pictureUtil";
}
