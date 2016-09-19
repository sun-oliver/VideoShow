package com.helloworld.videoshow;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.helloworld.videoshow.base.BaseActivity;

public class AboutActivity extends BaseActivity {

    private static final String TAG = "AboutActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setToolbarTitle(toolbar, getString(R.string.nav_about));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TextView about_version = (TextView) findViewById(R.id.about_version);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            about_version.setText(getString(R.string.version, pInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getPackageManager", e);
        }

        TextView about_privacy_terms = (TextView) findViewById(R.id.about_privacy_terms);
        final WebView webView = (WebView) findViewById(R.id.about_webview);
        final RelativeLayout rlAboutContent = (RelativeLayout) findViewById(R.id.rl_about_content);
        about_privacy_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.tracker().send(new HitBuilders.EventBuilder()
                        .setCategory("CeBianLan")
                        .setAction("GuanYu:"+"YinSiTiaoKuan")
                        .build());
                rlAboutContent.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setWebViewClient(new WebViewClient(){
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Log.e("yjg","url:"+url);
                        view.loadUrl(url);
                        return true;
                    }
                });
                webView.loadUrl("http://www.9videos.cc/term.html");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
