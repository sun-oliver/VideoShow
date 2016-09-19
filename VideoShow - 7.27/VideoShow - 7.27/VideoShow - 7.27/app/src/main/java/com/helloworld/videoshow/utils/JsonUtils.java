package com.helloworld.videoshow.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by Hello on 2016/7/18.
 */
public class JsonUtils {

    public String writeJsonToRawResourceId(Context context, int resourceId){
        /*try {
            FileWriter fileWriter = new FileWriter("android:resource://com.helloworld.videoshow.fragment/"+"R.raw.video_list.json");
        }*/
        return "";
    }

    /**
     * 从raw文件夹中读json数据
     * @param context
     * @param resourceId
     * @return
     */
    public static String readTextFileFromRawResourceId(Context context, int resourceId) {
        StringBuilder builder = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(resourceId)));

        try {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                builder.append(line).append("\n");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return builder.toString();
    }
}
