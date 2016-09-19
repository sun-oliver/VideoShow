package com.helloworld.videoshow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.helloworld.videoshow.utils.PreferencesUtil;
import com.helloworld.videoshow.utils.Tool;

import java.text.SimpleDateFormat;

public class StartActivity extends AppCompatActivity {
    String TAG = "StartActivity";
    String currentTime;//当前时间
    SimpleDateFormat format;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //每天第一次使用就请求当天的json数据
        format = new SimpleDateFormat("yyyyMMdd");
        currentTime = format.format(System.currentTimeMillis());
        String initFirstUseTime = PreferencesUtil.getLastUseDay(StartActivity.this);//初始化第一次使用时间
        Log.e(TAG,"currentTime"+currentTime+"initFirstUseTime"+initFirstUseTime);
        if (Integer.parseInt(currentTime) - Integer.parseInt(initFirstUseTime) > 0) {
            PreferencesUtil.setLastUseDay(StartActivity.this, currentTime);//将当前时间保存到sp
            PreferencesUtil.initJsonByDay(StartActivity.this,currentTime);
            Log.e(TAG,"remove sharedPreference which downloaded");
            PreferencesUtil.removeWhichDownloaded(StartActivity.this);
            PreferencesUtil.removeWhichLike(StartActivity.this);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(StartActivity.this, MainActivity.class));
                finish();
            }
        }, 1000);

    }
}
