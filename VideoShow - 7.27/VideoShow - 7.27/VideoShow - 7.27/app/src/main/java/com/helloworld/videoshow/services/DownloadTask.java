package com.helloworld.videoshow.services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.helloworld.videoshow.database.ThreadDAO;
import com.helloworld.videoshow.database.ThreadDAOImpl;
import com.helloworld.videoshow.model.ThreadInfo;
import com.helloworld.videoshow.model.VideoInfo;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Du on 2015/11/24.
 * 多线程管理器
 */
public class DownloadTask {
    private static final String TAG = "DownloadTask";
    private VideoInfo mVideoInfo;
    private Context mContext;
    private ThreadDAO mDao = null;
    private long mFinished = 0;
    public boolean isPause = false;
    private int mThreadCount = 1;
    private List<DownloadThread> mThreadList = null;//线程集合
    public static ExecutorService sExecutorService =
            Executors.newCachedThreadPool();
    private String mp4Name;//视频名称

    public DownloadTask(Context context, VideoInfo mVideoInfo, int mThreadCount) {
        this.mContext = context;
        this.mVideoInfo = mVideoInfo;
        this.mThreadCount = mThreadCount;
        mDao = new ThreadDAOImpl(mContext);
    }
    
    public void download() {
        if (mVideoInfo.getSize() == -1) {
            sendFinishBroadcast();
            return;
        }
        List<ThreadInfo> threadInfos = mDao.getThreads(mVideoInfo.getFilepath());
        if (threadInfos.size() == 0) {
            long length = mVideoInfo.getSize() / mThreadCount;
            for (int i = 0; i < mThreadCount; i++) {
                ThreadInfo threadInfo = new ThreadInfo((i + 1) * length - 1, 0, i, length * i, mVideoInfo.getFilepath());
                if (i == mThreadCount - 1) {
                    threadInfo.setEnd(mVideoInfo.getSize());
                }
                threadInfos.add(threadInfo);
                //向数据库插入一条线程信息
                mDao.insertThread(threadInfo);
            }
        }
        //启动多个线程进行下载
        mThreadList = new ArrayList<>();
        for (ThreadInfo info : threadInfos) {
            DownloadThread thread = new DownloadThread(info);
            //用线程池执行
            DownloadTask.sExecutorService.execute(thread);
            //thread.start();
            //添加线程到集合
            mThreadList.add(thread);
        }
        //向activity发广播
        Intent intent = new Intent(DownloadService.ACTION_START);
        intent.putExtra("videoInfo", mVideoInfo);
        intent.putExtra("id",mVideoInfo.getUuid());
        intent.putExtra("downloadedLength",mVideoInfo.getFinished());
        mContext.sendBroadcast(intent);
    }

    /**
     * 判断所有线程是否都执行完毕
     */
    private synchronized void checkAllThreadsFinish() {
        boolean allFinished = true;
        for (DownloadThread thread : mThreadList) {
            if (!thread.isFinished) {
                allFinished = false;
                break;
            }
        }
        if (allFinished) {
            //删除线程信息
            mDao.deleteThread(mVideoInfo.getFilepath());
            sendFinishBroadcast();
        }
    }

    private void sendFinishBroadcast() {
        //向activity发广播
        Intent intent = new Intent(DownloadService.ACTION_FINISH);
        intent.putExtra("id", mVideoInfo.getUuid());
        intent.putExtra("videoName", mp4Name);
        mContext.sendBroadcast(intent);
    }

    class DownloadThread extends Thread {
        private ThreadInfo mThreadInfo = null;
        public boolean isFinished = false;//用来标识线程是否结束

        public DownloadThread(ThreadInfo mThreadInfo) {
            this.mThreadInfo = mThreadInfo;
        }

        @Override
        public void run() {
            //设置线程下载位置（可能已经下载过）
            HttpURLConnection conn = null;
            RandomAccessFile randomAccessFile = null;
            InputStream inputStream = null;
            try {
                URL url = new URL(mThreadInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");
                //设置下载位置
                long start = mThreadInfo.getStart() + mThreadInfo.getFinished();
                Log.i(TAG, "mThreadInfo.getStart() + mThreadInfo.getFinished() "+start);
                conn.setRequestProperty("Range", "bytes=" + start + "-" + mThreadInfo.getEnd());
                //设置文件写入位置
                mp4Name = mVideoInfo.getFilepath().substring(mVideoInfo.getFilepath().lastIndexOf('/'),mVideoInfo.getFilepath().length());
                File file = new File(DownloadService.DOWNLOAD_PATH, mp4Name);
                randomAccessFile = new RandomAccessFile(file, "rwd");
                randomAccessFile.seek(start);
                Intent intent = new Intent(DownloadService.ACTION_START);
                mFinished += mThreadInfo.getFinished();
                // 开始下载
                if (conn.getResponseCode() == 206) {
                    // 读取数据
                    inputStream = conn.getInputStream();
                    byte[] buffer = new byte[1024 * 4];
                    int len = -1;
                    long time = System.currentTimeMillis();
                    while ((len = inputStream.read(buffer)) != -1) {
                        // 写入文件
                        randomAccessFile.write(buffer, 0, len);
                        // 累加整个文件下载进度
                        mFinished += len;
                        //累加每个线程完成的进度
                        mThreadInfo.setFinished(mThreadInfo.getFinished() + len);
                        if (System.currentTimeMillis() - time > 1500) {
                            time = System.currentTimeMillis();
                            //发送进度到activity
                            intent.putExtra("length", mVideoInfo.getSize());
                            intent.putExtra("id", mVideoInfo.getUuid());
                            long progress = mFinished * 100 / mVideoInfo.getSize();
                            intent.putExtra("finished", progress);
                            mContext.sendBroadcast(intent);
                        }
                        // 保存下载进度
                        if (isPause) {
                            mDao.updateThread(mThreadInfo.getUrl(), mThreadInfo.getId(), mThreadInfo.getFinished());
                            return;
                        }
                    }
                    //标识线程执行完毕
                    isFinished = true;
                    //检查下载任务是否执行完毕
                    checkAllThreadsFinish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                    randomAccessFile.close();
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
