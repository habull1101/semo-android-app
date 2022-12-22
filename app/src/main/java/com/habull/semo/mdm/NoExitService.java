package com.habull.semo.mdm;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.habull.semo.R;

import java.util.List;

public class NoExitService extends Service {

    private Thread thread = null;
    private Context context = null;
    private boolean isRunning = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;
        context = this;

        // cu 0.1s thi check xem co thoat khoi ung dung khong, neu co thi quay lai ung dung ngay
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    if (isInBackground()) {
                        Intent i = new Intent(context, LockActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }
                } while (isRunning);

                stopSelf();
            }
        });

        thread.start();

        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        Intent notificationIntent = new Intent(this, LostDeviceActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "default channel")
                        .setContentTitle("Semo")
                        .setContentText("Semo đang chạy")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pendingIntent)
                        .setChannelId("default");

        // Notification ID cannot be 0.
        startForeground(1234, builder.build());

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        super.onDestroy();
    }

    private boolean isInBackground() {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = activityManager.getRunningTasks(1);
        ComponentName componentInfo = taskInfo.get(0).topActivity;
//        Log.i("check", componentInfo.getPackageName());

        return (!context.getApplicationContext().getPackageName().equals(componentInfo.getPackageName()));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}