package com.helloworld.videoshow.utils;

/**
 * Created by Hello on 2016/6/30.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.helloworld.videoshow.MainActivity;
import com.helloworld.videoshow.StartActivity;
import com.helloworld.videoshow.model.VideoListData;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Preferences类，记录相关文本信息
 */
public class PreferencesUtil {
    private static String TAG = "PreferencesUtil";
    //第一次使用软件
    private static String PREFERENCES_FIRST_LAUNCH = "first_launch";
    private static String LAST_DAY_USE = "last_day_use";
    public static void saveFirstLaunch(Context context, boolean b) {
        SharedPreferences.Editor editor = context.getSharedPreferences("VideoShow",Context.MODE_PRIVATE).edit();
        editor.putBoolean(PREFERENCES_FIRST_LAUNCH, b);
        editor.commit();
    }

    public static Boolean getFirstLaunch(Context context) {
        SharedPreferences pref = context.getSharedPreferences("VideoShow",Context.MODE_PRIVATE);
        Boolean isFirstLaunch = pref.getBoolean(PREFERENCES_FIRST_LAUNCH,true);
        return isFirstLaunch;
    }

    public static void setLastUseDay(Context context, String lastDayUse) {
        SharedPreferences.Editor editor = context.getSharedPreferences("VideoShow",Context.MODE_PRIVATE).edit();
        editor.putString("last_day_use", lastDayUse);
        editor.commit();
    }

    public static String getLastUseDay(Context context) {
        SharedPreferences pref = context.getSharedPreferences("VideoShow",Context.MODE_PRIVATE);
        String lastUseDay = pref.getString("last_day_use","20160715");
        return lastUseDay;
    }

    public static void setChuanYueNum(Context mContext,int num){
        SharedPreferences.Editor editor = mContext.getSharedPreferences("VideoShow",Context.MODE_PRIVATE).edit();
        editor.putInt("Num",num);
        editor.commit();
        /*MainActivity.mTvTravelNum.setText(num+"");*/
        Log.e("PreferencesUtil","chuanyue Num:"+num);
    }

    public static int getChuanYueNum(Context mContext){
        SharedPreferences pref = mContext.getSharedPreferences("VideoShow",Context.MODE_PRIVATE);
        int useNum = pref.getInt("Num",0);
        return useNum;
    }

    public static void initJsonByDay(final Context mContext, final String strData){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(Const.HTTP_URL + strData +"/"+Tool.getCurrentCountry(mContext));
                    Log.e(TAG,"url"+url);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(3000);
                    connection.setConnectTimeout(3000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    String result = response+"";
                    Log.e(TAG,"data:"+result);
                    PreferencesUtil.setJsonData(mContext,result);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /**
     * 设置json到SharedPreferences
     * @param mContext
     * @param strResult
     */
    public static void setJsonData(Context mContext,String strResult){
        SharedPreferences.Editor editor = mContext.getSharedPreferences("VideoShow",Context.MODE_PRIVATE).edit();
        editor.putString("JsonData",strResult);
        editor.commit();
    }

    /**
     * 从SharedPreferences获取json数据
     * @param mContext
     * @return
     */
    public static String getJsonData(Context mContext){
        SharedPreferences pref = mContext.getSharedPreferences("VideoShow",Context.MODE_PRIVATE);
        String resultData = pref.getString("JsonData","");
        return resultData;
    }

    public static void setWhichDownloaded(Context mContext,String num){
        SharedPreferences.Editor editor = mContext.getSharedPreferences("VideoShow",Context.MODE_PRIVATE).edit();
        editor.putString("numDownloaded",num+"*");
        editor.commit();
    }

    public static String getWhichDownloaded(Context mContext){
        SharedPreferences pref = mContext.getSharedPreferences("VideoShow",Context.MODE_PRIVATE);
        String resultData = pref.getString("numDownloaded","");
        return resultData;
    }

    public static void removeWhichDownloaded(Context mContext){
        SharedPreferences.Editor editor = mContext.getSharedPreferences("VideoShow",Context.MODE_PRIVATE).edit();
        editor.remove("numDownloaded");
        editor.commit();
    }

    public static void setWhichLike(Context mContext,String num){
        SharedPreferences.Editor editor = mContext.getSharedPreferences("VideoShow",Context.MODE_PRIVATE).edit();
        editor.putString("numLike",num+"*");
        editor.commit();
    }

    public static String getWhichLike(Context mContext){
        SharedPreferences pref = mContext.getSharedPreferences("VideoShow",Context.MODE_PRIVATE);
        String resultData = pref.getString("numLike","");
        return resultData;
    }

    public static void removeWhichLike(Context mContext){
        SharedPreferences.Editor editor = mContext.getSharedPreferences("VideoShow",Context.MODE_PRIVATE).edit();
        editor.remove("numLike");
        editor.commit();
    }
}
