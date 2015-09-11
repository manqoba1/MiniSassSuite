package com.sifiso.codetribe.riverteamapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.sifiso.codetribe.minisasslibrary.dto.GcmDeviceDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.util.GCMUtil;
import com.sifiso.codetribe.minisasslibrary.util.SharedUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.TimerUtil;
import com.sifiso.codetribe.minisasslibrary.util.Util;
import com.sifiso.codetribe.minisasslibrary.util.WebSocketUtil;

import java.util.Timer;


public class Welcome extends AppCompatActivity {
    ImageView imageView;
    TextView imageText;
    Timer timer;
    Context ctx;
    Button bLogin, bReg;
    private Integer teamID, teamMemberID;
    RelativeLayout holderFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getSupportActionBar().hide();
        ctx = getApplicationContext();
        bLogin = (Button) findViewById(R.id.btnwLog);
        bReg = (Button) findViewById(R.id.btnCreate);
        holderFrame = (RelativeLayout) findViewById(R.id.holderFrame);
       // holderFrame.setBackground(Util.getRandomHeroImage(ctx));
        imageView = (ImageView) findViewById(R.id.imgBackground);
        // imageText = (TextView) findViewById(com.sifiso.codetribe.minisasslibrary.R.id.imageText);
        //flashImages();
        checkVirgin();
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Welcome.this, SignActivity.class);
                startActivity(intent);
                getSupportActionBar().setTitle("Sign in");
            }
        });


        bReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Welcome.this, RegisterActivity.class);
                startActivity(intent);
                getSupportActionBar().setTitle("Register Team");
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
       // startActivity(new Intent(Welcome.this, SplashActivity.class));
        // finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    private void checkVirgin() {

        TeamMemberDTO dto = SharedUtil.getTeamMember(ctx);
        if (dto != null) {
            Log.i(LOG, "++++++++ Not a virgin anymore ...checking GCM registration....");
            teamID = dto.getTeamID();
            teamMemberID = dto.getTeamMemberID();
            String id = SharedUtil.getRegistrationId(getApplicationContext());
            if (id == null) {
                registerGCMDevice();

            }

            Intent intent = new Intent(ctx, MainPagerActivity.class);
            startActivity(intent);
            finish();
            return;
        }

    }

    GcmDeviceDTO gcmDevice;

    private void registerGCMDevice() {
        boolean ok = checkPlayServices();

        if (ok) {
            Log.e(LOG, "############# Starting Google Cloud Messaging registration");
            GCMUtil.startGCMRegistration(getApplicationContext(), new GCMUtil.GCMUtilListener() {
                @Override
                public void onDeviceRegistered(String id) {
                    Log.e(LOG, "############# GCM - we cool, cool.....: " + id);
                    SharedUtil.storeRegistrationId(ctx, id);
                    gcmDevice = new GcmDeviceDTO();
                    gcmDevice.setManufacturer(Build.MANUFACTURER);
                    gcmDevice.setModel(Build.MODEL);
                    gcmDevice.setSerialNumber(Build.SERIAL);
                    gcmDevice.setProduct(Build.PRODUCT);
                    gcmDevice.setAndroidVersion(Build.VERSION.RELEASE);
                    gcmDevice.setRegistrationID(id);
                    gcmDevice.setTeamID(teamID);
                    gcmDevice.setTeamMemberID(teamMemberID);
                    updateGCMDevice(gcmDevice);
                }

                @Override
                public void onGCMError() {
                    Log.e(LOG, "############# onGCMError --- we got GCM problems");

                }
            });
        }
    }

    public boolean checkPlayServices() {
        Log.w(LOG, "checking GooglePlayServices .................");
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(ctx);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                // GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                //         PLAY_SERVICES_RESOLUTION_REQUEST).show();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.gms")));
                return false;
            } else {
                Log.i(LOG, "This device is not supported.");
                throw new UnsupportedOperationException("GooglePlayServicesUtil resultCode: " + resultCode);
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {


        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        TimerUtil.killFlashTimer();
        super.onDestroy();
    }

    @Override
    protected void onPause() {

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        TimerUtil.killFlashTimer();
        super.onPause();
    }

    private void updateGCMDevice(GcmDeviceDTO gcm) {
        RequestDTO w = new RequestDTO(RequestDTO.ADD_GCM_DEVICE);
        w.setGcmDevice(gcm);
        WebSocketUtil.sendRequest(ctx, Statics.MINI_SASS_ENDPOINT, w, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(ResponseDTO response) {

            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(String message) {

            }
        });
    }

    @Override
    protected void onResume() {
        TimerUtil.killFlashTimer();
        super.onResume();
    }

    @Override
    protected void onRestart() {
        TimerUtil.killFlashTimer();
        super.onRestart();
    }

    boolean isFirst;

    @Override
    protected void onStart() {
         if (isFirst) {
           isFirst = false;
       // startActivity(new Intent(Welcome.this, SplashActivity.class));
        }
        super.onStart();
    }

    static final String LOG = Welcome.class.getSimpleName();
}
