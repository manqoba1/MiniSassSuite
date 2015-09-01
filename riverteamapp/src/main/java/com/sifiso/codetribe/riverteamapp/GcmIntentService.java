package com.sifiso.codetribe.riverteamapp;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.activities.ProfileActivity;

public class GcmIntentService extends GCMBaseIntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    //TODO register gcm online
    private static final String GCM_SENDER_ID = "884798769581";

    public GcmIntentService() {
        super(GCM_SENDER_ID);
    }

    @Override
    protected void onError(Context arg0, String arg1) {
        Log.i(TAG, "onError ... " + arg1);

    }

    @Override
    protected void onMessage(Context arg0, Intent intent) {
        Log.w(TAG, "onMessage ..trainee:..gcm message here... " + intent.getExtras().toString());
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
        Log.d(TAG, "GCM messageType = " + messageType);
        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                    .equals(messageType)) {
                Log.e(TAG, "GoogleCloudMessaging - MESSAGE_TYPE_SEND_ERROR");
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                    .equals(messageType)) {
                Log.e(TAG, "GoogleCloudMessaging - MESSAGE_TYPE_SEND_ERROR");

            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                    .equals(messageType)) {
                //It's a regular GCM message, do some work.
                sendNotification(intent);
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }

    @Override
    protected void onRegistered(Context arg0, String arg1) {
        Log.i(TAG, "onRegistered ... " + arg1);
        //registerWithAirBop(registrationId);
    }

    @Override
    protected void onUnregistered(Context arg0, String arg1) {
        Log.i(TAG, "onUnRegistered ... " + arg1);
    }

    Gson gson = new Gson();

    private void sendNotification(Intent msgIntent) {

        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        String message = msgIntent.getExtras().getString("message");


        Intent wpaIntent = new Intent(this, ProfileActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                wpaIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.blue_crap)
                .setContentTitle("Invitation request")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    static final String TAG = "GcmIntentService";

}