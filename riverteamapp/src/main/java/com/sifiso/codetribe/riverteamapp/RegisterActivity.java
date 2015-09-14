package com.sifiso.codetribe.riverteamapp;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.fragments.RegisterFragment;
import com.sifiso.codetribe.minisasslibrary.toolbox.BaseVolley;
import com.sifiso.codetribe.minisasslibrary.toolbox.BohaVolley;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheck;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheckResult;
import com.sifiso.codetribe.minisasslibrary.util.DataUtil;
import com.sifiso.codetribe.minisasslibrary.util.ErrorUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.ToastUtil;
import com.sifiso.codetribe.minisasslibrary.util.Util;
import com.sifiso.codetribe.minisasslibrary.util.WebSocketUtil;

import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity implements RegisterFragment.RegisterFragmentListener {

    Context ctx;
    Activity activity;

    RegisterFragment registerFragment;

    Menu mMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sign Up");
        activity = this;
        ctx = getApplicationContext();
        countryCode = "ZA";
        registerFragment = (RegisterFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        // buildPages();
    }

    private void buildPages() {
        if (registerFragment != null) {
            registerFragment.setResponse(response);
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("response", response);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        mMenu = menu;
        final WebCheckResult w = WebCheck.checkNetworkAvailability(ctx);
        if (w.isMobileConnected() || w.isWifiConnected()) {
            getRegistrationData();
        } else {
            Util.showErrorToast(ctx, "No connection");
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            getRegistrationData();
        }
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (mMenu != null) {
            final MenuItem refreshItem = mMenu.findItem(R.id.action_refresh);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(R.layout.action_bar_progess);
                } else {
                    refreshItem.setActionView(null);
                }
            }
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


    static final String LOG = "RegistrationActivity";
    ResponseDTO response;


    String countryCode;

    public void getRegistrationData() {

        RequestDTO req = new RequestDTO();
        req.setRequestType(RequestDTO.LIST_REGISTER_DATA);
        try {
            setRefreshActionButtonState(true);
            /*WebSocketUtil.sendRequest(ctx, Statics.MINI_SASS_ENDPOINT, req, new WebSocketUtil.WebSocketListener() {
                @Override
                public void onMessage(final ResponseDTO r) {
                    Log.e(LOG, "## getStarterData responded...statusCode: " + r.getStatusCode());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setRefreshActionButtonState(false);
                            if (!ErrorUtil.checkServerError(ctx, r)) {
                                return;
                            }

                            Log.e(LOG, new Gson().toJson(r));
                            response = r;
                            buildPages();
                        }
                    });


                }

                @Override
                public void onClose() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setRefreshActionButtonState(false);
                            ToastUtil.noNetworkToast(ctx);
                            ErrorDialogFragment dialogFragment = new ErrorDialogFragment();

                            dialogFragment.show(getFragmentManager(), "");
                        }
                    });

                }

                @Override
                public void onError(final String message) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setRefreshActionButtonState(false);
                            ToastUtil.noNetworkToast(ctx);

                        }
                    });
                }
            });*/
            BaseVolley.getRemoteData(Statics.SERVLET_ENDPOINT, req, ctx, new BaseVolley.BohaVolleyListener() {
                @Override
                public void onResponseReceived(final ResponseDTO r) {
                    Log.e(LOG, "## getStarterData responded...statusCode: " + r.getStatusCode());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setRefreshActionButtonState(false);
                            if (!ErrorUtil.checkServerError(ctx, r)) {
                                return;
                            }

                            Log.e(LOG, new Gson().toJson(r));
                            response = r;
                            buildPages();
                        }
                    });
                }

                @Override
                public void onVolleyError(VolleyError error) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setRefreshActionButtonState(false);
                            ToastUtil.noNetworkToast(ctx);

                        }
                    });
                }

                @Override
                public void onError(final String message) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setRefreshActionButtonState(false);
                            if (!message.equals(null)) {

                                Log.e(LOG, message);
                                Util.showErrorToast(ctx, message);

                            }
                        }
                    });
                }
            });
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setRefreshActionButtonState(false);
                }
            });
        }
    }

    @Override
    public void onRegistered() {
        Intent intent = new Intent(RegisterActivity.this, SignActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onTownRequest() {

    }

    private int currentViewPager;


    boolean isBack = false;

    @Override
    public void onBackPressed() {

        if (isBack) {
            super.onBackPressed();
        }
        isBack = true;
    }

}
