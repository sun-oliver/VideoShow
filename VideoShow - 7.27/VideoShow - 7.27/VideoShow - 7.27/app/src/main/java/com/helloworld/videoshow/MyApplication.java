package com.helloworld.videoshow;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Hello on 2016/7/20.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.e("MyApplication","path:"+path);
        initException();
        initGoogleAnalysis();
    }
    private void initException() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                Writer writer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(writer);
                e.printStackTrace(printWriter);
                Log.d("EXCEPTION",  writer.toString());
                try {
                    long timestamp = System.currentTimeMillis();
                    DateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    String time = formatter.format(new Date());
                    String fileName = "videos-crash-" + time + "-" + timestamp + ".log";
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                        File dir = new File(path, "crash");
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        FileOutputStream fos = new FileOutputStream(dir.getAbsoluteFile() + File.separator + fileName);
                        fos.write(writer.toString().getBytes());
                        fos.close();
                        System.exit(1);
                    }
                } catch (Exception ex) {
                    Log.e("EXCEPTION", "an error occured while writing file...", ex);
                }
            }
        });
    }

    private static GoogleAnalytics analytics;
    private static Tracker mTracker;
    /**
     * The default app tracker. If this method returns null you forgot to either set
     * android:name="&lt;this.class.name&gt;" attribute on your application element in
     * AndroidManifest.xml or you are not setting this.tracker field in onCreate method override.
     */
    public static Tracker tracker() {
        return mTracker;
    }

    private void initGoogleAnalysis(){
        analytics = GoogleAnalytics.getInstance(this);
        mTracker = analytics.newTracker("UA-81391915-1");
        mTracker.enableExceptionReporting(true);
        mTracker.enableAdvertisingIdCollection(true);
        mTracker.enableAutoActivityTracking(true);
    }
}
