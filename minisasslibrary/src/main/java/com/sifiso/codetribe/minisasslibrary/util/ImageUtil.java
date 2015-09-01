package com.sifiso.codetribe.minisasslibrary.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.sifiso.codetribe.minisasslibrary.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Chris on 2015-02-19.
 */
public class ImageUtil {

    public static File getFileFromBitmap(Bitmap bm, String filename)
            throws Exception {
        if (bm == null) throw new Exception();
        File file = null;
        try {
            File rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (rootDir == null) {
                rootDir = Environment.getDataDirectory();
            }
            File imgDir = new File(rootDir, "minisass_app");
            if (!imgDir.exists()) {
                imgDir.mkdir();
            }
            OutputStream outStream = null;
            file = new File(imgDir, filename);
            if (file.exists()) {
                file.delete();
                file = new File(imgDir, filename);
            }
            outStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
            outStream.flush();
            outStream.close();

            Log.e(LOGTAG, "File saved from bitmap: " + file.getAbsolutePath());
        } catch (Exception e) {
            Log.e(LOGTAG, "Failed to fetch file from bitmap", e);
        }
        return file;

    }

    static final Locale locale = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", locale);

    private static String getText(Location loc) {
        StringBuilder sb = new StringBuilder();
        sb.append(sdf.format(new Date()));
        if (loc != null) {
            sb.append(" - lat: ").append(loc.getLatitude());
            sb.append(" lng: ").append(loc.getLongitude());
        }
        return sb.toString();
    }


    public static Bitmap drawTextToBitmap(Context mContext, Bitmap bitmap, Location location) {
        Resources resources = mContext.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap.Config bitmapConfig = bitmap.getConfig();
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(mContext.getResources().getColor(R.color.white));
        paint.setTextSize((int) 13);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint1.setColor(mContext.getResources().getColor(R.color.green));


        String text = getText(location);
        Paint.FontMetrics fm = new Paint.FontMetrics();
        paint1.setTextAlign(Paint.Align.CENTER);
        paint1.getFontMetrics(fm);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (height > width) {
            canvas.drawRect(0, height - 40, height - 40, height, paint1);
            canvas.drawText(text, 5, height - 10, paint);
        } else {
            canvas.drawRect(0, 0, width, 40, paint1);
            canvas.drawText(text, 5, 25, paint);
        }
        Log.i(LOGTAG, "Check: Text drawn on bitmap");
        return bitmap;
    }

    public static Bitmap getBitmapFromUri(Context ctx, Uri uri)
            throws Exception {
        Bitmap bm = MediaStore.Images.Media.getBitmap(ctx.getContentResolver(),
                uri);
        return bm;

    }
    public static Bitmap getResizedBitmap(Bitmap bitmap, int maxWidth,
                                          int maxHeight) {
        System.gc();
        Log.d(LOGTAG, "### Original Bitmap width: " + bitmap.getWidth()
                + " height: " + bitmap.getHeight());
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float xRatio = (float) maxWidth / width;
        float yRatio = (float) maxHeight / height;
        float scaleRatio = xRatio < yRatio ? xRatio : yRatio;
        Bitmap resizedBitmap = null;
        try {
            Matrix matrix = new Matrix();
            matrix.postScale(scaleRatio, scaleRatio);
            resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                    matrix, true);
            Log.d(LOGTAG,
                    "### Resized Bitmap width: " + resizedBitmap.getWidth()
                            + " height: " + resizedBitmap.getHeight()
                            + " rowBytes: " + resizedBitmap.getRowBytes());

        } catch (OutOfMemoryError e) {
            Log.e(LOGTAG, "$$$$$$$$$$ OUT OF MEMORY");
            return null;
        } catch (Exception e) {
            Log.e(LOGTAG, "$$$$$$$$$$ GENERIC EXCEPTION");
            return null;
        } catch (Throwable t) {
            Log.e(LOGTAG, "$$$$ Throwable Exception getting image: ");
            return null;
        }
        return resizedBitmap;
    }
    static final String LOGTAG = ImageUtil.class.getSimpleName();
}
