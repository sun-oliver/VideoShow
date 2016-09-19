package com.helloworld.videoshow.utils;

import android.util.Log;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

/**
 * Created by Hello on 2016/6/25.
 */
public class DataUtils {
    public static final int BANNER_FIRST = 0;
    public static final int BANNER_SECOND = 1;
    public static final int BANNER_THIRD = 2;
    public static final int BANNER_FOUR = 3;


    /**
     * 分享次数
     * @return
     */
    public static long shareNum() {
        int baseHour;
        int randNum;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HHmmss");
        String nowTime = format.format(System.currentTimeMillis());
        int hour = Integer.parseInt(nowTime.substring(9,11).trim());
        int minuter = Integer.parseInt(nowTime.substring(11,13).trim());

        int mhour = hour;
        int baseData=0;
        if(hour <=8){
            baseHour = 20;
            mhour = hour;
            baseData =0;
        }else if(hour <= 20){
            baseHour = 120;
            mhour = hour - 8;
            baseData = 8 * 20;
        }else {
            baseHour = 40;
            mhour = hour - 20;
            baseData = 8 * 20 + 120 * 12;
        }
        randNum = (int)((Math.random()+1)*baseHour/2);
        long shareNo = Math.round(mhour * baseHour + baseData + randNum);
       /* double shareNo = new java.math.BigDecimal(hour * baseHour + randNum).setScale(3,java.math.BigDecimal.ROUND_HALF_UP).doubleValue();*/
        return shareNo;
    }

    /**
     * 总共看的人数
     * @return
     */
    public static long TotalSeeNum() {
        int baseHour;
        int randNum;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HHmmss");
        String nowTime = format.format(System.currentTimeMillis());
        int hour = Integer.parseInt(nowTime.substring(9,11).trim());
        int minuter = Integer.parseInt(nowTime.substring(11,13).trim());

        int mhour = hour;
        int baseData=0;
        if(hour <=8){
            baseHour = 60;
            mhour = hour;
            baseData = 0;
        }else if(hour <= 20){
            baseHour = 360;
            mhour = hour - 8;
            baseData = 8 * 60;
        }else {
            baseHour = 120;
            mhour = hour - 20;
            baseData = 8 * 60 + 12 * 360;
        }
        randNum = (int)((Math.random()+1)*baseHour/2);
        long seeNo = Math.round(mhour * baseHour + baseData + randNum);
        return seeNo;
    }

}
