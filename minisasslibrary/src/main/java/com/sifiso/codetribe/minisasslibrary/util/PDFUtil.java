package com.sifiso.codetribe.minisasslibrary.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by aubreyM on 15/09/17.
 */
public class PDFUtil {

    static final String LOG = PDFUtil.class.getSimpleName();
    public interface PDFListener {
        void onDownloaded(File pdfFile);
        void onError();
    }

    public static void downloadPDF(Context ctx, final String mURL, final String fileName, final PDFListener listener) throws Exception {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                File dir = Environment.getExternalStorageDirectory();
                if (!dir.exists()) {
                    dir = Environment.getDataDirectory();
                }
                File file = new File(dir, fileName);
                Log.e(LOG, "## getPDF URL: " + mURL);

                try {
                    URL obj = new URL(mURL);
                    FileUtils.copyURLToFile(obj, file);
                    Log.e(LOG, "## pdf just downloaded: " + file.getAbsolutePath() + " length: " + file.length());
                    listener.onDownloaded(file);
                } catch (MalformedURLException e) {
                    listener.onError();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.i(LOG,"File downloaded: " + file.getAbsolutePath());
            }
        });
        thread.start();





    }

}
