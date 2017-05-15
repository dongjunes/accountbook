package com.hipo.component.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.Profile;
import com.hipo.component.receiver.BroadCast;
import com.hipo.model.pojo.UserVo;

/**
 * Created by dongjune on 2017-04-25.
 */

public class MyService extends Service {

    private BroadcastReceiver broadCast;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("service is running", "onStartCommand");

        startBroadCast();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(broadCast);
        Log.d("service is stop", "onDestory");
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startBroadCast() {
        broadCast = new BroadCast();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);

        Log.d("service to broadcast", "startBroadCast");
        registerReceiver(broadCast, intentFilter);

    }
}
