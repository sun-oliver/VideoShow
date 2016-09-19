package com.helloworld.videoshow.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


/**
 * Created by Oliver on 2016/9/11.
 * 为了测试BroadcastReceiver 可以删掉本类
 */
public class AlarmReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
//        Intent i = new Intent(context,MyService.class);
//        context.startService(i);
          Toast.makeText(context,">>>>>>>>AlarmReceiver>>>>>>>",Toast.LENGTH_SHORT).show();
    }
}
