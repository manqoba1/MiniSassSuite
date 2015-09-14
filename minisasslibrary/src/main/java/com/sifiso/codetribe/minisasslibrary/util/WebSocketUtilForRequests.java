package com.sifiso.codetribe.minisasslibrary.util;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestList;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.toolbox.BaseVolley;



import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import de.tavendo.autobahn.WebSocketOptions;

/**
 * Utility class to manage web socket communications for the application
 * Created by aubreyM on 2014/08/10.
 */
public class WebSocketUtilForRequests {
    public interface WebSocketListener {
        public void onMessage(ResponseDTO response);

        public void onClose();

        public void onError(String message);

    }


   /* public static void disconnectSession() {
        if (mWebSocketClient != null) {
            mWebSocketClient.close();
            Log.e(LOG, "@@ webSocket session disconnected");
        }
    }*/
   public static void sendRequest(Context ctx, final String suffix,
                                  RequestList w, WebSocketListener listener) {
       webSocketListener = listener;
       context = ctx;
       retryCount = 0;
       final String url = Statics.WEBSOCKET_URL + suffix;
       final String json = GSON.toJson(w);
       try {

           if (mConnection.isConnected()) {
               Log.i(LOG, "### WebSocket Status: Connected. using: " + url + " sending: \n" + json);
               mConnection.sendTextMessage(json);
           } else {
               connect(url, json);
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
    private static void connect(final String url, final String json) {
        Log.d(LOG, "&&&&&&& CONNECT TO WEBSOCKET, retryCount: " + retryCount);
        WebSocketOptions options = new WebSocketOptions();
        options.setSocketConnectTimeout(5000);
        options.setSocketReceiveTimeout(5000);
        try {
            mConnection.connect(url, new WebSocketHandler() {
                @Override
                public void onOpen() {
                    Log.w(LOG, "OnOpen: Connected to " + url + " sending...: \n" + json);
                    if (mConnection.isConnected()) {
                        mConnection.sendTextMessage(json);
                    } else {
                        Log.e(LOG, "-- mConnection is not connected. wtf?");
                    }
                }

                @Override
                public void onTextMessage(String payload) {
                    Log.d(LOG, "+++ onTextMessage payload size: " + getSize(payload.length()));
                    retryCount = 0;
                    try {
                        ResponseDTO r = GSON.fromJson(payload, ResponseDTO.class);
                        if (r.getSessionID() != null) {
                            Log.i(LOG, "Response with sessionID: " + r.getSessionID());
                        } else {
                            if (r.getStatusCode() == 0) {
                                webSocketListener.onMessage(r);
                            } else {
                                webSocketListener.onError(r.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        webSocketListener.onError("Communications with server failed");
                    }
                }

                @Override
                public void onBinaryMessage(byte[] payload) {
                    retryCount = 0;
                    ByteBuffer byteBuffer = ByteBuffer.wrap(payload);
                    parseData(byteBuffer);
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.e(LOG, "Connection lost. " + reason + ". will issue disconnect");
                }
            }, options);

        } catch (IllegalStateException e) {
            Log.e(LOG, "IllegalStateException .", e);
        } catch (WebSocketException e) {
            Log.e(LOG, "WebSocketException failed.", e);
            webSocketListener.onError(e.getMessage());
        }
    }

   /* public static void sendRequest(Context c, RequestList req, WebSocketListener listener) {
        if (!BaseVolley.checkNetworkOnDevice(c)) {
            listener.onError(c.getString(R.string.no_networks));
            return;
        }

        webSocketListener = listener;
        requestList = req;
        ctx = c;
        TimerUtil.startTimer(new TimerUtil.TimerListener() {
            @Override
            public void onSessionDisconnected() {
                try {
                    connectWebSocket();
                    return;
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            if (mWebSocketClient == null) {
                connectWebSocket();
            } else {
                String json = gson.toJson(requestList);
                start = System.currentTimeMillis();
                mWebSocketClient.send(json);
                Log.d(LOG, "## web socket message sent\n" + json);
            }


        } catch (WebsocketNotConnectedException e) {
            try {
                Log.e(LOG, "WebsocketNotConnectedException. Problems with web socket", e);
                connectWebSocket();
            } catch (URISyntaxException e1) {
                Log.e(LOG, "Problems with web socket", e);
                webSocketListener.onError("Problem starting server socket communications\n" + e1.getMessage());
            }
        } catch (URISyntaxException e) {
            Log.e(LOG, "Problems with web socket", e);
            webSocketListener.onError("Problem starting server socket communications");
        }
    }


    private static void connectWebSocket() throws URISyntaxException {
        URI uri = new URI(Statics.WEBSOCKET_URL + Statics.REQUEST_ENDPOINT);
        Log.i(LOG, "## connectWebSocket uri: " + uri.toString());
        start = System.currentTimeMillis();
        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i(LOG, "########## WEBSOCKET Opened: " + serverHandshake.getHttpStatusMessage());
            }

            @Override
            public void onMessage(String response) {
                TimerUtil.killTimer();
                end = System.currentTimeMillis();
                Log.i(LOG, "### onMessage returning socket sessionID, length: " + response.length() + " elapsed: " + Util.getElapsed(start, end) + " seconds"
                        + "\n" + response);
                try {
                    ResponseDTO r = gson.fromJson(response, ResponseDTO.class);
                    if (r.getStatusCode() == null || r.getStatusCode() == 0) {
                        if (r.getSessionID() != null) {
                            //SharedUtil.saveSessionID(ctx, r.getSessionID());
                            String json = gson.toJson(requestList);
                            mWebSocketClient.send(json);
                            Log.i(LOG, "### web socket request sent after onOpen\n" + json);

                        }
                    } else {
                        webSocketListener.onError(r.getMessage());
                    }
                } catch (Exception e) {
                    Log.e(LOG, "Failed to parse response from server", e);
                    webSocketListener.onError("Failed to parse response from server");
                }

            }

            @Override
            public void onMessage(ByteBuffer bb) {
                TimerUtil.killTimer();
                end = System.currentTimeMillis();
                Log.i(LOG, "### onMessage returning data, roundtrip elapsed: " + Util.getElapsed(start, end) + " seconds");
                parseData(bb);
            }


            @Override
            public void onClose(final int i, String s, boolean b) {
                Log.e(LOG, "########## WEBSOCKET onClose, status code:  " + i);
                TimerUtil.killTimer();
                webSocketListener.onClose();
            }

            @Override
            public void onError(final Exception e) {
                Log.e(LOG, "onError ", e);
                TimerUtil.killTimer();
                webSocketListener.onError(ctx.getString(R.string.server_comms_failed));


            }
        };

        Log.d(LOG, "### #### -------------> starting mWebSocketClient.connect ...");

        mWebSocketClient.connect();
    }*/

    private static void parseData(ByteBuffer bb) {
        Log.i(LOG, "### parseData ByteBuffer capacity: " + ZipUtil.getKilobytes(bb.capacity()));
        String content = null;
        start = System.currentTimeMillis();
        try {
            try {
                content = new String(bb.array());
                ResponseDTO response = GSON.fromJson(content, ResponseDTO.class);
                if (response.getStatusCode() == 0) {
                    webSocketListener.onMessage(response);
                } else {
                    webSocketListener.onError(response.getMessage());
                }
                return;

            } catch (Exception e) {
                content = ZipUtil.uncompressGZip(bb);
            }

            if (content != null) {
                ResponseDTO response = GSON.fromJson(content, ResponseDTO.class);
                if (response.getStatusCode() == 0) {
                    Log.w(LOG, "### response status code is 0 -  OK");
                    webSocketListener.onMessage(response);
                } else {
                    Log.e(LOG, "## response status code is > 0 - server found ERROR");
                    webSocketListener.onError(response.getMessage());
                }
            } else {
                Log.e(LOG, "-- Content from server failed. Response content is null");
                webSocketListener.onError("Content from server failed. Response is null");
            }
            end = System.currentTimeMillis();
            Log.d(LOG, "### parseData finished, elapsed: " + Util.getElapsed(start, end) + " seconds");
        } catch (Exception e) {
            Log.e(LOG, "parseData Failed", e);
            webSocketListener.onError("Failed to unpack server response");
        }
    }


    static final String LOG = WebSocketUtilForRequests.class.getSimpleName();


    public static String getElapsed() {
        BigDecimal m = new BigDecimal(end - start).divide(new BigDecimal(1000));

        return "" + m.doubleValue() + " seconds";
    }
    private static String getSize(int size) {
        Double x = Double.parseDouble("" + size);
        Double y = x / Double.parseDouble("1024");
        return df.format(y) + "KB";
    }
    static WebSocketConnection mConnection = new WebSocketConnection();
    static WebSocketListener webSocketListener;
    static RequestDTO request;
    static Context context;
    static long start, end;
    static final Gson GSON = new Gson();
    static int retryCount;

    static final DecimalFormat df = new DecimalFormat("###,###,###,###,###,##0.00");
}
