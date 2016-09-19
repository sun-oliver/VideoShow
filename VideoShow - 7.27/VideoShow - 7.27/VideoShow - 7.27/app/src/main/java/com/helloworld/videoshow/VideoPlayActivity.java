package com.helloworld.videoshow;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.helloworld.videoshow.fragment.VideoFragment;
import com.helloworld.videoshow.utils.Tool;
import com.helloworld.videoshow.view.MyVideoView;
import java.io.File;

public class VideoPlayActivity extends AppCompatActivity {
    static String TAG ="VideoPlayActivity";
    RelativeLayout rlVideoPlayControl;//整个屏幕的布局
    ImageView play, pause;//播放、暂停按钮
    MyVideoView myVideoView;

    RelativeLayout rlShareWhatsapp;//底部一键分享到whatsapp布局
    ImageView ivVoiceOpen,ivVoiceClose;//声音打开、关闭按钮

    String videoUrl;//视频地址

    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        videoUrl = getIntent().getStringExtra("videoUrl");
        initView();
        initListener();
    }

    private void initView(){
        rlVideoPlayControl = (RelativeLayout) findViewById(R.id.rl_video_play);
        rlVideoPlayControl.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        play = (ImageView) findViewById(R.id.iv_play);
        pause = (ImageView) findViewById(R.id.iv_pause);
        myVideoView = (MyVideoView) findViewById(R.id.video_view);

        rlShareWhatsapp = (RelativeLayout) findViewById(R.id.rl_play_whatsapp);
        ivVoiceOpen = (ImageView) findViewById(R.id.iv_play_voice_open);
        ivVoiceClose = (ImageView) findViewById(R.id.iv_play_voice_close);
    }

    private void initListener(){
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                play.setVisibility(View.GONE);
                pause.setVisibility(View.GONE);
                rlShareWhatsapp.setVisibility(View.GONE);
                ivVoiceOpen.setVisibility(View.GONE);
                ivVoiceClose.setVisibility(View.GONE);
            }
        };
        handler.postDelayed(runnable, 1000);

        myVideoView.setVideoUri(Uri.parse(videoUrl));
        myVideoView.setOnPlayerListener(new MyVideoView.OnPlayerListener() {
            @Override
            public void onVideoSizeChanged(int width, int height) {
                final int vWidth = width;
                final int vHeight = height;
                resizeVideoPlayer(vWidth, vHeight);
            }

            @Override
            public void onPrepared() {
                myVideoView.start();
            }

            @Override
            public void onProgress(int progress) {
                Log.e("yjg","onProgress");
            }

            @Override
            public void onVideoCompletion() {
                if(myVideoView.isPlaying()){
                    myVideoView.pause();
                }
                finish();
            }

            @Override
            public void onError(int what, int error) {
                Log.e("yjg","onError");
            }

            @Override
            public void onInfo(int what, int extra) {
                Log.e("yjg","onInfo");
            }
        });

        rlVideoPlayControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myVideoView.isPlaying()){
                    play.setVisibility(View.GONE);
                    pause.setVisibility(View.VISIBLE);
                }else {
                    pause.setVisibility(View.GONE);
                    play.setVisibility(View.VISIBLE);
                }
                rlShareWhatsapp.setVisibility(View.VISIBLE);
                if(myVideoView.isVoice()){
                    ivVoiceClose.setVisibility(View.GONE);
                    ivVoiceOpen.setVisibility(View.VISIBLE);
                }else{
                    ivVoiceOpen.setVisibility(View.GONE);
                    ivVoiceClose.setVisibility(View.VISIBLE);
                }
                handler.postDelayed(runnable,2000);
            }
        });

        /**
         * 视频播放暂停事件
         */
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.tracker().send(new HitBuilders.EventBuilder()
                        .setCategory("ShouYe")
                        .setAction(videoUrl+":"+"BoFang")
                        .build());
                myVideoView.start();
                play.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.tracker().send(new HitBuilders.EventBuilder()
                        .setCategory("ShouYe")
                        .setAction(videoUrl+":"+"ZanTing")
                        .build());
                myVideoView.pause();
                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
            }
        });
        ivVoiceOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.tracker().send(new HitBuilders.EventBuilder()
                        .setCategory("ShouYe")
                        .setAction(videoUrl+":"+"ShengYinKai")
                        .build());
                ivVoiceOpen.setVisibility(View.GONE);
                ivVoiceClose.setVisibility(View.VISIBLE);
                myVideoView.setVolume(0.0f);
            }
        });
        ivVoiceClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.tracker().send(new HitBuilders.EventBuilder()
                        .setCategory("ShouYe")
                        .setAction(videoUrl+":"+"ShengYinGuan")
                        .build());
                ivVoiceClose.setVisibility(View.GONE);
                ivVoiceOpen.setVisibility(View.VISIBLE);
                myVideoView.setVolume(1.0f);
            }
        });

        rlShareWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myVideoView.isPlaying()){
                    myVideoView.pause();
                }
                MyApplication.tracker().send(new HitBuilders.EventBuilder()
                        .setCategory("ShouYe")
                        .setAction(videoUrl+":"+"ShiPinBoFang:"+"WhatsappButton")
                        .build());
                shareWhatsApp(videoUrl);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(myVideoView.isPlaying()){
            myVideoView.pause();
        }
        myVideoView.stopPlayback();
        handler.removeCallbacks(runnable);
    }

    private void shareWhatsApp(String path) {
        File sendFile = new File(path);
        if (!sendFile.exists()) {
            Toast.makeText(this, getString(R.string.play_before_share), Toast.LENGTH_SHORT).show();
        } else {
            if (VideoFragment.checkApkExist(this, "com.whatsapp")) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setPackage("com.whatsapp");
                intent.setType("video/*");
                Uri VideoUri = Uri.fromFile(new File(path));
                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                intent.putExtra(Intent.EXTRA_STREAM, VideoUri);
                startActivity(Intent.createChooser(intent, "Send"));

                /*//点击分享，穿越次数+1
                int useNum = PreferencesUtil.getChuanYueNum(this);
                Log.e(TAG, "useNum" + useNum);
                PreferencesUtil.setChuanYueNum(this, useNum + 1);
                Log.e(TAG, "Chuanyue" + (useNum + 1));*/
            } else {
                Toast.makeText(this, getString(R.string.no_find_whatsapp), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 重新计算视频控件的宽高
    * @param width
    * @param height
    */
    private void resizeVideoPlayer(int width, int height) {
        float videoRatio = width * 1.0F / height;
        int videoPlayerWidth = Tool.getWidth(this);
        int videoPlayerHeight = (int) (videoPlayerWidth / videoRatio);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(videoPlayerWidth, videoPlayerHeight);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        myVideoView.setLayoutParams(layoutParams);
        Log.e("yjg","width"+width+"height"+height + "videoRatio:"+videoRatio+"videoPlayerWidth:"+videoPlayerWidth+"videoPlayerHeight"+videoPlayerHeight);
    }

}
