package com.sifiso.codetribe.riverteamapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.activities.ForgotPasswordActivity;
import com.sifiso.codetribe.minisasslibrary.dialogs.AddTeamsDialog;
import com.sifiso.codetribe.minisasslibrary.dto.GcmDeviceDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.toolbox.BaseVolley;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheck;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheckResult;
import com.sifiso.codetribe.minisasslibrary.util.DataUtil;
import com.sifiso.codetribe.minisasslibrary.util.ErrorUtil;
import com.sifiso.codetribe.minisasslibrary.util.GCMUtil;
import com.sifiso.codetribe.minisasslibrary.util.SharedUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.TimerUtil;
import com.sifiso.codetribe.minisasslibrary.util.ToastUtil;
import com.sifiso.codetribe.minisasslibrary.util.Util;
import com.sifiso.codetribe.minisasslibrary.util.WebSocketUtil;

import java.util.ArrayList;
import java.util.List;


public class SignActivity extends AppCompatActivity {
    Context ctx;
    Button bsRegister, bsSignin;
    EditText esPin;
    TextView SI_wifi;
    String email, townList;
    ProgressBar sign_progress;
    AutoCompleteTextView esEmail;
    TextView SI_app, SI_welcome;
    RelativeLayout SI_banner;
    WebCheckResult wr;
    Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign);

        ctx = getApplicationContext();
        wr = WebCheck.checkNetworkAvailability(ctx);
        setFields();

        getEmail();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign, menu);
        //getCachedData();
        mMenu = menu;

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sign In");
        getSupportActionBar().setElevation(8);
        getSupportActionBar().setShowHideAnimationEnabled(true);
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
        if (id == R.id.action_forgot_password) {
            Intent intent = new Intent(SignActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        }
        if (id == android.R.id.home) {
            Intent intent = new Intent(SignActivity.this, Welcome.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignActivity.this, Welcome.class);
        startActivity(intent);
        finish();
    }

    public void setFields() {

        SI_app = (TextView) findViewById(R.id.SI_app);
        SI_wifi = (TextView) findViewById(R.id.SI_wifi);
        SI_banner = (RelativeLayout) findViewById(R.id.SI_banner);
        SI_banner.setBackground(Util.getRandomHeroImage(ctx));
        SI_banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(SI_banner, 100, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        SI_banner.setBackground(Util.getRandomHeroImage(ctx));
                    }
                });
            }
        });
        SI_app.setText(ctx.getResources().getString(R.string.sign_in_text));
        SI_welcome = (TextView) findViewById(R.id.SI_welcome);
        bsSignin = (Button) findViewById(R.id.btnLogSubmit);
        bsSignin.setText("Sign In");
        esEmail = (AutoCompleteTextView) findViewById(R.id.SI_txtEmail);
        esPin = (EditText) findViewById(R.id.SI_pin);
        sign_progress = (ProgressBar) findViewById(R.id.progressBar);

       /* Palette.from(bitmap).maximumColorCount(numberOfColors).generate(new Palette.PaletteAsyncListener() {
            @Overridebitmap
            public void onGenerated(Palette palette) {
                // Get the "vibrant" color swatch based on the bitmap
                Palette.Swatch vibrant = palette.getVibrantSwatch();
                if (vibrant != null) {
                    // Set the background color of a layout based on the vibrant color
                    containerView.setBackgroundColor(vibrant.getRgb());
                    // Update the title TextView with the proper text color
                    titleView.setTextColor(vibrant.getTitleTextColor());
                }
            }
        });*/
        bsSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(bsSignin, 100, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {

                        sendSignIn();

                    }
                });

              /*  Intent intentEva = new Intent(SignActivity.this, EvaluationView.class);
                startActivity(intentEva);
                finish();*/
            }
        });

        checkVirgin();
        if (SharedUtil.getEmail(ctx) != null) {
            esEmail.setText(SharedUtil.getEmail(ctx));
        }
    }

    private void checkVirgin() {
        //SharedUtil.clearTeam(ctx);
        TeamMemberDTO dto = SharedUtil.getTeamMember(ctx);
        if (dto != null) {
            Log.i(LOG, "++++++++ Not a virgin anymore ...checking GCM registration....");
            String id = SharedUtil.getRegistrationId(getApplicationContext());
            if (id == null) {
                registerGCMDevice();
            }

            Intent intent = new Intent(ctx, MainPagerActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        if (wr.isMobileConnected() || wr.isWifiConnected()) {
            registerGCMDevice();
        } else {

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

    AddTeamsDialog addTeamDialog;
    ProgressDialog pd;

    public void sendSignIn() {

        if (esEmail.getText() == null) {
            esEmail.setError("Enter email");
            return;
        }

        if (esPin.getText().toString().isEmpty()) {
            esPin.setError("Enter password");
            return;
        }

        RequestDTO w = new RequestDTO(RequestDTO.SIGN_IN_MEMBER);
        w.setEmail(esEmail.getText().toString());
        w.setPassword(esPin.getText().toString());
        w.setGcmDevice(gcmDevice);
        Log.d(LOG, new Gson().toJson(w));
        bsSignin.setEnabled(false);
        //setRefreshActionButtonState(true);
        pd = new ProgressDialog(SignActivity.this);

        pd.setMessage("Signing in...");

        pd.setCancelable(false);
        pd.show();
        BaseVolley.getRemoteData(Statics.SERVLET_ENDPOINT, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(final ResponseDTO resp) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //setRefreshActionButtonState(false);
                        pd.dismiss();
                        // Log.d(LOG,resp.getTeamMember().getEmail());
                        if (resp.getStatusCode() > 0) {
                            return;
                        }
                        // Log.d(LOG,resp.getTeamMember().getEmail());
                        if (resp.getTeamMember().getTeam() == null) {
                            addTeamDialog = new AddTeamsDialog();
                            addTeamDialog.setTeamData(resp);
                            addTeamDialog.setListener(new AddTeamsDialog.AddTeamDialogListener() {
                                @Override
                                public void onAddTeamToMember(TeamDTO team) {
                                    DataUtil.addTeam(team, new DataUtil.DataUtilInterface() {
                                        @Override
                                        public void onResponse(ResponseDTO r) {
                                            if (!ErrorUtil.checkServerError(ctx, r)) {
                                                return;
                                            }
                                            ToastUtil.toast(ctx, r.getMessage());
                                            resp.getTeamMember().setTeam(r.getTeam());
                                            SharedUtil.saveTeamMember(ctx, resp.getTeamMember());
                                            SharedUtil.storeEmail(ctx, esEmail.getText().toString());
                                            Intent intent = new Intent(SignActivity.this, MainPagerActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onError(String error) {

                                        }
                                    });
                                }
                            });
                            addTeamDialog.show(getFragmentManager(), " ");
                            return;
                        }
                        SharedUtil.saveTeamMember(ctx, resp.getTeamMember());
                        SharedUtil.storeEmail(ctx, esEmail.getText().toString());
                        Intent intent = new Intent(SignActivity.this, MainPagerActivity.class);
                        startActivity(intent);
                        finish();

                    }
                });
            }

            @Override
            public void onVolleyError(final VolleyError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // setRefreshActionButtonState(false);
                        pd.dismiss();
                        bsSignin.setEnabled(true);
                        if (!error.getMessage().equals(null)) {

                            Log.e(LOG, error.getMessage());

                           // Util.showErrorToast(ctx, error.getMessage());

                        }
                    }
                });

            }

            @Override
            public void onError(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //setRefreshActionButtonState(false);
                        pd.dismiss();
                        bsSignin.setEnabled(true);
                        if (!message.equals(null)) {
                            SI_wifi.setVisibility(View.VISIBLE);
                            Util.shakeX(SI_wifi, 100, 4, new Util.UtilAnimationListener() {
                                @Override
                                public void onAnimationEnded() {

                                }
                            });
                            SI_wifi.setText("Sign In Failed. We can not find your email or password. Check and Try again.");
                            Log.e(LOG, message);
                            //Util.showErrorToast(ctx, "Sign In Failed. We can not find your email or password. Check and Try again.");

                        }
                    }
                });
            }
        });
    }


    List<String> emailAccountList;

    public void getEmail() {
        AccountManager am = AccountManager.get(getApplicationContext());
        Account[] accts = am.getAccounts();
        if (accts.length == 0) {
            Toast.makeText(ctx, "No Accounts found. Please create one and try again", Toast.LENGTH_LONG).show();
            //finish();
            return;
        }

        emailAccountList = new ArrayList<String>();
        if (accts != null) {
            for (int i = 0; i < accts.length; i++) {
                emailAccountList.add(accts[i].name);
            }
            ArrayAdapter adapter = new ArrayAdapter<String>(ctx, R.layout.xsimple_spinner_item, emailAccountList);

            esEmail.setAdapter(adapter);
            esEmail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    email = emailAccountList.get(position);
                    esEmail.setText(email);
                }
            });
            // esEmail.setText(email);

        }

    }

    @Override
    protected void onPause() {
        TimerUtil.killFlashTimer();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        TimerUtil.killFlashTimer();
        super.onDestroy();
    }

    static final String LOG = "SigninActivity";

    public void setRefreshActionButtonState(final boolean refreshing) {

        if (mMenu != null) {

            final MenuItem refreshItem = mMenu.findItem(R.id.action_settings);

            if (refreshItem != null) {
                Log.d(LOG, "after");
                if (refreshing) {
                    Log.d(LOG, "Before");
                    refreshItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                    refreshItem.setActionView(R.layout.action_bar_progess);
                } else {
                    refreshItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                    refreshItem.setActionView(null);
                }
            }
        }
    }
}
