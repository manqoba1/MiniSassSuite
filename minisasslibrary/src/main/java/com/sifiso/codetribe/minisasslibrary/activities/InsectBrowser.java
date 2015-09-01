package com.sifiso.codetribe.minisasslibrary.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sifiso.codetribe.minisasslibrary.R;
import com.sifiso.codetribe.minisasslibrary.dto.InsectDTO;
import com.sifiso.codetribe.minisasslibrary.toolbox.WebCheckResult;

public class InsectBrowser extends AppCompatActivity {
    WebView webView;
    WebCheckResult rs;
    InsectDTO insect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insect_browser);
        webView = (WebView) findViewById(R.id.browser);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState != null) {
            insect = (InsectDTO) savedInstanceState.getSerializable("insect");
        } else {
            insect = (InsectDTO) getIntent().getSerializableExtra("insect");
        }

        getSupportActionBar().setTitle(insect.getGroupName());
        webView.setWebViewClient(new MyWebViewClient());
        webView.setInitialScale(1);
        webView.getSettings().setJavaScriptEnabled(true);
        // webView.addJavascriptInterface(new WebAppInterface(this), "Android");

        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setDomStorageEnabled(true);

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        webView.loadUrl("http://en.wikipedia.org/wiki/" + insect.getGroupName());
        //browser.onKeyDown()
        webView.canGoBack();
    }

    @Override
    protected void onDestroy() {
        overridePendingTransition(com.sifiso.codetribe.minisasslibrary.R.anim.slide_in_left, com.sifiso.codetribe.minisasslibrary.R.anim.slide_out_right);
        //  TimerUtil.killFlashTimer();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("insect", insect);
        super.onSaveInstanceState(outState);
    }

    private class MyClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            // progressBar.setVisibility(View.GONE);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //     progressBar.setVisibility(View.GONE);
        }
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView web, String url) {
            Log.i("SA", "--onPageFinished --- title: " + web.getTitle()
                    + " - url: " + web.getUrl());
            currentTitle = web.getTitle();
            currentURL = web.getUrl();
            end = System.currentTimeMillis();
           /* Toast.makeText(getApplicationContext(), "Page: " + currentTitle +
                    " - loaded in " + (end - start) / 1000 + " seconds", Toast.LENGTH_SHORT).show();*/
            Log.e("SA", "--onPageFinished -- elapsed loading time: " + (end - start) / 1000 + " seconds");
            super.onPageFinished(web, url);
        }

        @Override
        public void onReceivedError(WebView web, int errorCode,
                                    String description, String failingURL) {
            Log.i("SA", "--onReceivedError code: " + errorCode + " "
                    + description + " - failing: " + failingURL);
        }

        @Override
        public void onPageStarted(WebView web, String url, Bitmap favIcon) {
            Log.e("SA", "--onPageStarted url: " + url);
            /*start = System.currentTimeMillis();
            setRefreshActionButtonState(true);*/
        }
    }

    long start, end;
    String currentTitle, currentURL;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_insect_browser, menu);
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

        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
