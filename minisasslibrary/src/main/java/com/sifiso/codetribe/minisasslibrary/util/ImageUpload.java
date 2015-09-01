package com.sifiso.codetribe.minisasslibrary.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ImagesDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.toolbox.BaseVolley;



import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import khandroid.ext.apache.http.HttpEntity;
import khandroid.ext.apache.http.HttpResponse;
import khandroid.ext.apache.http.client.HttpClient;
import khandroid.ext.apache.http.client.methods.HttpPost;
import khandroid.ext.apache.http.entity.mime.MultipartEntity;
import khandroid.ext.apache.http.entity.mime.content.FileBody;
import khandroid.ext.apache.http.entity.mime.content.StringBody;
import khandroid.ext.apache.http.impl.client.DefaultHttpClient;
import khandroid.ext.apache.http.util.ByteArrayBuffer;

/**
 * Created by Chris on 2015-02-26.
 */
public class ImageUpload {

    public interface ImageUploadListener {
        public void onImageUploaded(ResponseDTO response);
        public void onUploadError();
    }
    static final String LOG = "ImageUpload";
    static ImagesDTO images;
    static List<File> mFiles;
    static ImageUploadListener imageUploadListener;
    static ResponseDTO response;

    public static void upload(ImagesDTO dto, List<File> files, Context ctx,
                              ImageUploadListener listener) {
        images = dto;
        mFiles = files;
        imageUploadListener = listener;
        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        Log.i(LOG, "FIRED ImageUpload");
        new ImageUploadTask().execute();
    }


    static class ImageUploadTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            InputStream is = null;
            String responseJSON = null;
            try {
                response = new ResponseDTO();
                MultipartEntity reqEntity = null;
                try {
                    reqEntity = new MultipartEntity();
                } catch (Exception e) {
                    Log.e(LOG, "MultiPartEntity has error - check it out", e);
                    throw new Exception();

                }

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Statics.URL + "photo");
                Log.d(LOG, "sending image upload to:" + Statics.URL + "photo");

                Gson gson = new Gson();
                String json = gson.toJson(images);
                Log.e(LOG, "json to be sent:" + gson.toJson(images));
                reqEntity.addPart("JSON", new StringBody(json));

                int idx = 1;
                for (File file : mFiles) {
                    FileBody fileBody = new FileBody(file);
                    reqEntity.addPart("ImageFile" + idx, fileBody);
                    idx++;
                }
                httppost.setEntity((HttpEntity) reqEntity);
                HttpResponse httpResponse = httpClient.execute(httppost);
                HttpEntity resEntity = httpResponse.getEntity();

                is = resEntity.getContent();
                int size = 0;
                ByteArrayBuffer bab = new ByteArrayBuffer(8192);
                byte[] buffer = new byte[8192];
                while ((size = is.read(buffer, 0, buffer.length)) != -1) {
                    bab.append(buffer, 0, size);
                }
                responseJSON = new String(bab.toByteArray());
                if (responseJSON !=  null) {
                    Log.w(LOG, "Response from upload:\n" + responseJSON);
                    response = gson.fromJson(responseJSON, ResponseDTO.class);
                }
            } catch (Exception e) {
                Log.e(LOG, "Failed to upload", e);
                return 9997;
            } finally  {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            Log.i(LOG, "FIRED onPostExecute");
            if (result > 0) {
                imageUploadListener.onUploadError();
                return;
            }
            if (response.getStatusCode() == null) {
                response.setStatusCode(0);
            }
            imageUploadListener.onImageUploaded(response);
        }

    }

}
