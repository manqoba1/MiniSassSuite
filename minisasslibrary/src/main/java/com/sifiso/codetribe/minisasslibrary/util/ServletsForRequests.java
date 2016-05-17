package com.sifiso.codetribe.minisasslibrary.util;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestList;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.toolbox.BaseVolley;

import java.math.BigDecimal;

/**
 * Created by CodeTribe1 on 2015-06-23.
 */
public class ServletsForRequests {
    public interface ServletsListener {
        public void onResponse(ResponseDTO response);

        public void onClose();

        public void onError(String message);

    }

    static ServletsListener servletsListener;
    static RequestList requestList;
    static Context ctx;
    static long start, end;

    public static void sendRequest(Context c, RequestList req, ServletsListener listener) {
        if (!BaseVolley.checkNetworkOnDevice(c)) {
            listener.onError(c.getString(R.string.no_networks));
            return;
        }

        servletsListener = listener;
        requestList = req;
        ctx = c;
        BaseVolley.sendRequest(Statics.REQUEST_SERVLET, requestList, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO r) {
                parseData(r);
            }

            @Override
            public void onVolleyError(VolleyError error) {

            }

            @Override
            public void onError(String message) {

            }
        });

    }

    private static void parseData(ResponseDTO response) {
        Log.i(LOG, "### parseData ByteBuffer capacity: ");
        String content = null;
        start = System.currentTimeMillis();
        try {

            if (response.getStatusCode() == 0) {
                Log.w(LOG, "### response status code is 0 -  OK");
                servletsListener.onResponse(response);
            } else {
                Log.e(LOG, "## response status code is > 0 - server found ERROR");
                servletsListener.onError(response.getMessage());
            }

            end = System.currentTimeMillis();
            Log.d(LOG, "### parseData finished, elapsed: " + Util.getElapsed(start, end) + " seconds");
        } catch (Exception e) {
            Log.e(LOG, "parseData Failed", e);
            servletsListener.onError("Failed to unpack server response");
        }
    }

    private static void helperRequest(RequestList req) {
        BaseVolley.sendRequest(Statics.SERVLET_ENDPOINT, requestList, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO r) {
                if (r.getStatusCode() == null || r.getStatusCode() == 0) {

                    //SharedUtil.saveSessionID(ctx, r.getSessionID());
                    String json = gson.toJson(requestList);
                    //helperRequest(req);
                    Log.i(LOG, "### web socket request sent after onOpen\n" + json);


                } else {
                    servletsListener.onError(r.getMessage());
                }
            }

            @Override
            public void onVolleyError(VolleyError error) {

            }

            @Override
            public void onError(String message) {

            }
        });
    }

    static final String LOG = ServletsForRequests.class.getSimpleName();
    static final Gson gson = new Gson();

    public static String getElapsed() {
        BigDecimal m = new BigDecimal(end - start).divide(new BigDecimal(1000));

        return "" + m.doubleValue() + " seconds";
    }
}
