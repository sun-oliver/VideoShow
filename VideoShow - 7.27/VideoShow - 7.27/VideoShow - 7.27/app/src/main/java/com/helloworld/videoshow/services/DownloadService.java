package com.helloworld.videoshow.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.helloworld.videoshow.model.VideoInfo;
import com.helloworld.videoshow.utils.Tool;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Hello on 2016/7/14.
 */
public class DownloadService extends Service {
    private static String TAG = "DownloadService";
    public static String DOWNLOAD_PATH = "";
    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_FINISH = "ACTION_FINISH";
    public static final int MSG_INIT = 0;

    //下载任务集合
    private Map<String, DownloadTask> mTasks = new LinkedHashMap<>();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            DOWNLOAD_PATH = Tool.getDownloadDir(this);
            if (ACTION_START.equals(intent.getAction())) {
                VideoInfo videoInfo = (VideoInfo) intent.getSerializableExtra("videoInfo");
                Log.i(TAG,"Start:" + videoInfo.toString());
                DownloadTask.sExecutorService.execute(new InitThread(videoInfo));
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INIT:
                    VideoInfo videoInfo = (VideoInfo) msg.obj;
                    //启动下载任务
                    DownloadTask task = new DownloadTask(DownloadService.this, videoInfo, 2);
                    task.download();
                    //把下载任务添加到集合中
                    mTasks.put(videoInfo.getUuid(), task);
                    break;
            }
        }
    };

    class InitThread extends Thread {
        private VideoInfo mVideoInfo = null;

        public InitThread(VideoInfo mVideoInfo) {
            this.mVideoInfo = mVideoInfo;
        }

        public void run() {
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            try {
                //连接网络文件
                URL url = new URL(mVideoInfo.getFilepath());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");
                int length = -1;
                //获取文件长度
                if (conn.getResponseCode() == 200) {
                    length = conn.getContentLength();
                }
                if (length > 0) {
                    //在本地创建文件
                    File dir = new File(DOWNLOAD_PATH);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    String mp4Name = mVideoInfo.getFilepath().substring(mVideoInfo.getFilepath().lastIndexOf('/'),mVideoInfo.getFilepath().length());
                    File file = new File(dir,mp4Name);
                    raf = new RandomAccessFile(file, "rwd");
                    //设置文件长度
                    raf.setLength(length);
                }
                mVideoInfo.setSize(length);
                mHandler.obtainMessage(MSG_INIT, mVideoInfo).sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    raf.close();
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
