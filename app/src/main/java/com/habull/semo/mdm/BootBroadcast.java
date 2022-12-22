package com.habull.semo.mdm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            if (LostDeviceActivity.isLocked) {
                Intent localIntent = new Intent(context, LockActivity.class);
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(localIntent);
            }
        }
    }
}
