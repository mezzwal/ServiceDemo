package com.nubiz.servicedemo;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;

/**
 * Created by admin on 6/13/2016.
 */
public class UpdateService extends Service {
    private int hitCount = 0;
    private boolean serviceStarted = false;
    private static final int NOTIFICATION_ID = 1;
    private static final int INTERVAL = 2000;

    private final Handler handler = new Handler();
    private Runnable runnable;

    private NotificationManager nManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }


    public int onStartCommand(final Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (!serviceStarted) {
            nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher);
            builder.setAutoCancel(false);
            runnable = new Runnable() {
                @Override
                public void run() {
                    builder.setContentTitle(intent.getStringExtra("type") + "; " + ++hitCount);
                    nManager.notify(NOTIFICATION_ID, builder.build());
                    if (isRunningApp("com.settings.android"))
                        Toast.makeText(getApplicationContext(), "settings started", Toast.LENGTH_SHORT).show();

                    handler.postDelayed(this, INTERVAL);
                }
            };
            handler.postDelayed(runnable, INTERVAL);
            serviceStarted = true;
        }
        return startServiceForeground();
    }

    private boolean isRunningApp(String processName) {
        if (processName == null)
            return false;

        ActivityManager.RunningAppProcessInfo app;

        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> l = activityManager.getRunningAppProcesses();
        Iterator<ActivityManager.RunningAppProcessInfo> i = l.iterator();
        while (i.hasNext()) {
            app = i.next();
            if (app.processName.equals(processName) && app.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE)
                return true;
        }
        return false;
    }

    public int startServiceForeground() {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Location update Service")
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();

        startForeground(1, notification);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        serviceStarted = false;
        handler.removeCallbacks(runnable);
        nManager.cancel(NOTIFICATION_ID);
        sendBroadcast(new Intent().setAction("MANUALLY_STARTED"));
        super.onDestroy();
    }
}
