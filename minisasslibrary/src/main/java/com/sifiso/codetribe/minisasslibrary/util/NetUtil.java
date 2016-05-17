package com.sifiso.codetribe.minisasslibrary.util;

import android.content.Context;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestList;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.toolbox.BaseVolley;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Utility class to manage web socket communications for the application
 * Created by aubreyM on 2014/08/10.
 */
public class NetUtil {
    public interface NetListener {
        public void onMessage(ResponseDTO response);

        public void onClose();

        public void onError(String message);

    }

    public static void sendRequest(Context ctx, final String suffix,
                                   RequestList w, final NetListener listener) {

        BaseVolley.sendRequest(suffix, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO response) {
                if (response.getStatusCode() == 0) {
                    listener.onMessage(response);
                } else {
                    listener.onError(response.getMessage());
                }
            }

            @Override
            public void onVolleyError(VolleyError error) {
                listener.onError("Communications Error");
            }

            @Override
            public void onError(String message) {
                listener.onError(message);
            }
        });
    }
    public static void sendRequest(Context ctx, final String suffix,
                                   RequestDTO w, final NetListener listener) {

        BaseVolley.sendRequest(suffix, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO response) {
                if (response.getStatusCode() == 0) {
                    listener.onMessage(response);
                } else {
                    listener.onError(response.getMessage());
                }
            }

            @Override
            public void onVolleyError(VolleyError error) {
                listener.onError("Communications Error");
            }

            @Override
            public void onError(String message) {
                listener.onError(message);
            }
        });
    }


    static NetListener netListener;
    static RequestDTO request;
    static Context context;
    static long start, end;
    static final String LOG = NetUtil.class.getSimpleName();
    static final Gson GSON = new Gson();
    static int retryCount;

    public static String getElapsed() {
        BigDecimal m = new BigDecimal(end - start).divide(new BigDecimal(1000));

        return "" + m.doubleValue() + " seconds";
    }

    private static String getSize(int size) {
        Double x = Double.parseDouble("" + size);
        Double y = x / Double.parseDouble("1024");
        return df.format(y) + "KB";
    }

    static final DecimalFormat df = new DecimalFormat("###,###,###,###,###,##0.00");

}
