package com.habull.semo.mdm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.habull.semo.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LockActivity extends Activity {
    private final List blockedKeys = new ArrayList(
            Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        // hide navigation bar
        hideNavigationBar();

        setContentView(R.layout.activity_lock);

        SharedPreferences sharedPref = getSharedPreferences("semo", Context.MODE_PRIVATE);
        String valueOwner = sharedPref.getString("owner", "");
        TextView ownerTv = findViewById(R.id.textView3);
        ownerTv.setText(valueOwner);

        startService(new Intent(this, NoExitService.class));
    }

    @Override
    protected void onResume() {
        hideNavigationBar();

        super.onResume();
    }

    @Override
    protected void onPause() {
        hideNavigationBar();

        super.onPause();
    }

    public void onBackPressed() {
        // nothing
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (blockedKeys.contains(event.getKeyCode())) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    public void hideNavigationBar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }
}