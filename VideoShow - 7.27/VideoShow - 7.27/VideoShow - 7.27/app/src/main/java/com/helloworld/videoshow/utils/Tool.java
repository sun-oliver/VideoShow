package com.helloworld.videoshow.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.helloworld.videoshow.R;

import java.text.SimpleDateFormat;

/**
 * Created by Hello on 2016/7/5.
 */
public class Tool {
    /**
     * 获取手机屏幕的宽度，保证视频长宽比为1:1
     *
     * @param context
     * @return
     */
    public static int getWidth(Context context) {
        final float width = context.getResources().getDisplayMetrics().widthPixels;
        return (int) width;
    }

    /**
     * px转换成dp工具
     *
     * @param context 上下文对象
     * @param pxValue px数值
     * @return
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 设置下载目录，若未设置就是默认   download/9videos
     *
     * @param context
     * @return
     */
    public static String getDownloadDir(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("downloaddir", getDefaultDownloadDir());
    }

    /**
     * 默认视频下载目录，指定的  Download/9videos
     *
     * @return
     */
    public static String getDefaultDownloadDir() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/9videos";
    }

    /**
     * 反馈
     *
     * @param context
     */
    public static void feedback(Context context) {
        Intent email = new Intent(android.content.Intent.ACTION_SEND);
        email.setType("plain/text");
        String[] emailReciver = {"simple.app.2016@gmail.com"};
        String emailSubject = context.getString(R.string.feedback_subject);
        String emailBody = context.getString(R.string.feedback_body);
        email.putExtra(Intent.EXTRA_EMAIL, emailReciver);
        email.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        email.putExtra(Intent.EXTRA_TEXT, emailBody);
        context.startActivity(Intent.createChooser(email, context.getString(R.string.chose_email_client)));
    }

    //share to friends
    public static void share(Context context) {
//        String path = "https://play.google.com/store/apps/details?id=" + context.getPackageName();
//        String path = "https://goo.gl/jtSN1W" + context.getPackageName();
        String path = "https://play.google.com/store/apps/details?id=com.simpleapp.shareapps&referrer=utm_source%3DShareThisApp";
        String str = context.getString(R.string.share_to_friends_text) + path;
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, str);
        sendIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.chose_share_client)));
    }

    /**
     * 评分到谷歌play
     * @param context
     * @param packageName
     */
    public static void rateOrReview(Context context, String packageName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + packageName));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                Toast.makeText(context.getApplicationContext(), context.getString(R.string.no_browser), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 是否联网
     *
     * @param context
     * @return
     */
    public static boolean isNetConnected(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    /**
     * 判断是否处于wifi
     */
    public static boolean isWifi(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String nowTime = format.format(System.currentTimeMillis());
        Log.e("tool", "nowTime:" + nowTime);
        return nowTime;
    }

    public static String getCurrentCountry(Context context) {
        String strCountry = "us";
        String strLanguage = context.getResources().getConfiguration().locale.getLanguage();
        Log.e("tool", "strLanguage:" + strLanguage);
        if(strLanguage.endsWith("pt")){
            strCountry = "pt";
        }
        return strCountry;
    }

}
