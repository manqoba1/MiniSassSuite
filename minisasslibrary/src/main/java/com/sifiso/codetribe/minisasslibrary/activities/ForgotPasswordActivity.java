package com.sifiso.codetribe.minisasslibrary.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.MiniSassApp;
import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dialogs.AddTeamsDialog;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.RequestDTO;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.emailUtil.BackgroundMail;
import com.sifiso.codetribe.minisasslibrary.toolbox.BaseVolley;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheck;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheckResult;
import com.sifiso.codetribe.minisasslibrary.util.DataUtil;
import com.sifiso.codetribe.minisasslibrary.util.ErrorUtil;
import com.sifiso.codetribe.minisasslibrary.util.SharedUtil;
import com.sifiso.codetribe.minisasslibrary.util.Statics;
import com.sifiso.codetribe.minisasslibrary.util.ToastUtil;
import com.sifiso.codetribe.minisasslibrary.util.Util;

import java.util.ArrayList;
import java.util.List;

public class ForgotPasswordActivity extends AppCompatActivity {
    Context ctx;
    Button bsSignin;
    String email, townList;
    ProgressBar sign_progress;
    AutoCompleteTextView esEmail;
    TextView SI_app, SI_welcome,SI_wifi;
    ImageView SI_banner;
    WebCheckResult wr;
    Menu mMenu;
    BackgroundMail bm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ctx = getApplicationContext();
        bm = new BackgroundMail(ForgotPasswordActivity.this);
        bm.setGmailUserName("apk@mlab.co.za");
        bm.setGmailPassword("mLabtestdevice");
        wr = WebCheck.checkNetworkAvailability(ctx);
        setFields();

        getEmail();
    }

    List<String> emailAccountList;

    public void getEmail() {
        AccountManager am = AccountManager.get(getApplicationContext());
        Account[] accts = am.getAccounts();
        if (accts.length == 0) {
            Toast.makeText(ctx, "No Accounts found. Please create one and try again", Toast.LENGTH_LONG).show();
            finish();
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

    public void setFields() {

        SI_app = (TextView) findViewById(R.id.SI_app);
        SI_banner = (ImageView) findViewById(R.id.SI_banner);
        SI_wifi = (TextView) findViewById(R.id.SI_wifi);
        SI_banner.setImageDrawable(Util.getRandomHeroImage(ctx));
        SI_banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(SI_banner, 100, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {
                        SI_banner.setImageDrawable(Util.getRandomHeroImage(ctx));
                    }
                });
            }
        });
        SI_app.setText("MiniSASS Retrieve Password");
        SI_welcome = (TextView) findViewById(R.id.SI_welcome);
        bsSignin = (Button) findViewById(R.id.btnLogSubmit);
        bsSignin.setText("Submit");
        esEmail = (AutoCompleteTextView) findViewById(R.id.SI_txtEmail);

        sign_progress = (ProgressBar) findViewById(R.id.progressBar);


        bsSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.flashOnce(bsSignin, 100, new Util.UtilAnimationListener() {
                    @Override
                    public void onAnimationEnded() {

                        retrievePassword();

                    }
                });

              /*  Intent intentEva = new Intent(SignActivity.this, EvaluationView.class);
                startActivity(intentEva);
                finish();*/
            }
        });

        //checkVirgin();
        if (SharedUtil.getEmail(ctx) != null) {
            esEmail.setText(SharedUtil.getEmail(ctx));
        }
    }

    static final String LOG = ForgotPasswordActivity.class.getSimpleName();

    public void retrievePassword() {

        if (esEmail.getText() == null) {
            Toast.makeText(ctx, "Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }


        RequestDTO w = new RequestDTO(RequestDTO.GET_PASSWORD);
        w.setEmail(esEmail.getText().toString());

        Log.d(LOG, new Gson().toJson(w));
        bsSignin.setEnabled(false);
        setRefreshActionButtonState(true);

        BaseVolley.getRemoteData(Statics.SERVLET_ENDPOINT, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(final ResponseDTO resp) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);

                        // Log.d(LOG,resp.getTeamMember().getEmail());
                        if (!ErrorUtil.checkServerError(ctx, resp)) {
                            return;
                        }

                        bm.setMailTo(resp.getTeamMember().getEmail());
                        bm.setFormSubject("MiniSASS forgot password");
                        String msg = "Hi, " + resp.getTeamMember().getFirstName() + "\n\n"
                                + "Email \t\t: " + resp.getTeamMember().getEmail() + "\n"
                                + "Password \t: " + resp.getTeamMember().getPin();
                        Log.i(LOG, msg);
                        bm.setFormBody(msg);
                        bm.send();
                        bm.setmListener(new BackgroundMail.BackgroundMailListener() {
                            @Override
                            public void onMailSent() {
                                SI_wifi.setText(resp.getMessage());
                                SI_wifi.setVisibility(View.VISIBLE);
                                //Util.shakeX();
                                SharedUtil.storeEmail(ctx, esEmail.getText().toString());

                            }
                        });


                    }
                });
            }

            @Override
            public void onVolleyError(final VolleyError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        bsSignin.setEnabled(true);
                        if (!error.getMessage().equals(null)) {

                            Log.e(LOG, error.getMessage());
                            Util.showErrorToast(ctx, error.getMessage());

                        }
                    }
                });

            }

            @Override
            public void onError(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        bsSignin.setEnabled(true);
                        if (!message.equals(null)) {

                            Log.e(LOG, message);
                            Util.showErrorToast(ctx, message);

                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_forgot_password, menu);
        mMenu = menu;
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Forgot password");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (mMenu != null) {
            final MenuItem refreshItem = mMenu.findItem(R.id.action_settings);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(R.layout.action_bar_progess);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }
}
