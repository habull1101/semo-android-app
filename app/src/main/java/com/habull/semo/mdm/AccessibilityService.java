package com.habull.semo.mdm;

import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

public class AccessibilityService extends android.accessibilityservice.AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED ||
                    event.getEventType() == AccessibilityEvent.TYPE_VIEW_LONG_CLICKED){
            if (LostDeviceActivity.isLocked) {
                Intent localIntent = new Intent(this, LockActivity.class);
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(localIntent);
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }
}