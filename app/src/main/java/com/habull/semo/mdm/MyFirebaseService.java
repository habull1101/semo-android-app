package com.habull.semo.mdm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.habull.semo.R;

import java.util.Map;

public class MyFirebaseService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // process data message Firebase
        Map<String, String> map = remoteMessage.getData();

        String title = map.get("nameapp");
        String body = map.get("content");

        sendNotification(title, body);

        DevicePolicyManager dpm =
                (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName compName = new ComponentName(getApplicationContext(), OwnerReceiver.class);

        if (title.equals("Semo") && body.equals("Thiết bị này đã bị mất!")) {
            if (dpm.isAdminActive(compName)) {
                // get location
                LostDeviceActivity.getInstance().getLocation();

                // start service camera
                startService(new Intent(getApplicationContext(), CameraService.class));

                // send email
                try {
                    LostDeviceActivity.getInstance().sendEmail();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // factory reset data
                dpm.wipeData(0);

                // lock screen service
                LostDeviceActivity.isLocked = true;
                Intent localIntent = new Intent(this, LockActivity.class);
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(localIntent);

                // lock device
                dpm.lockNow();
            }
        }
    }

    @Override
    public void onNewToken(String token) {
        sendRegistrationToServer(token);

        Log.i(TAG, token);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to save device token to Database.
    }

    private void sendNotification(String title, String body) {
        Intent intent = new Intent(this, LostDeviceActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder noti = new NotificationCompat.Builder(this, "default")
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationManager.IMPORTANCE_HIGH);

        NotificationManager notiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notiManager.createNotificationChannel(channel);
        }

        notiManager.notify(0, noti.build());
    }
}