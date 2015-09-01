package com.sifiso.codetribe.minisasslibrary.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.tranfer.ResponseDTO;
import com.sifiso.codetribe.minisasslibrary.util.TimerUtil;
import com.sifiso.codetribe.minisasslibrary.viewsUtil.RandomPics;

import java.util.Timer;
/*
* Sasa 2015-02-20
 */

public class SplashActivity extends AppCompatActivity {
    ImageView imageView;
    TextView imageText;
    Timer timer;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        ctx = getApplicationContext();
        imageView = (ImageView) findViewById(R.id.imgBackground);
        imageText = (TextView) findViewById(R.id.imageText);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimerUtil.killFlashTimer();
                finish();

            }
        });
       // flashImages();

    }

    static final int STATUS_REG = 500;

    private void flashImages() {
        TimerUtil.startFlashTime(new TimerUtil.TimerFlashListener() {
            @Override
            public void onStartFlash() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RandomPics.getImage(ctx, imageView, imageText, new RandomPics.RandomPicsListener() {
                            @Override
                            public void onCompleteFlash() {
                                TimerUtil.killFlashTimer();
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        flashImages();

        return true;
    }

    private ResponseDTO response;

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
    protected void onDestroy() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        TimerUtil.killFlashTimer();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        TimerUtil.killFlashTimer();
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
       // flashImages();
    }

    @Override
    protected void onStop() {
        TimerUtil.killFlashTimer();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    static final String LOG = SplashActivity.class.getSimpleName();
}
