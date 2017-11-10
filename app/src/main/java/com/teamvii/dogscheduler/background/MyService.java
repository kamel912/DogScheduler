package com.teamvii.dogscheduler.background;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import java.util.Date;

public class MyService extends Service {

    private SMSBroadcastReceiver receiver;
    private IntentFilter filter;
    SentSMSObserver smsObserver;
    ContentResolver contentResolver;
    SharedPreferences preferences;
    boolean isSwitchChecked;

    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver(receiver, filter);

        contentResolver.registerContentObserver(Uri.parse("content://sms"), true, smsObserver);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new SMSBroadcastReceiver();
        filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");

        smsObserver = (new SentSMSObserver(getApplicationContext(), new Handler(),new Date()));
        contentResolver = getApplicationContext().getContentResolver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        contentResolver.unregisterContentObserver(smsObserver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        preferences = getSharedPreferences("SwitchState", MODE_PRIVATE);
        isSwitchChecked = preferences.getBoolean("state", false);
        if (isSwitchChecked) {
            Intent restartServiceTask = new Intent(getApplicationContext(), this.getClass());
            restartServiceTask.setPackage(getPackageName());
            PendingIntent restartPendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceTask, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager myAlarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            myAlarmService.set(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + 1000,
                    restartPendingIntent);
        }
        super.onTaskRemoved(rootIntent);
    }


}
