package com.habull.semo;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.cazaea.sweetalert.SweetAlertDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

public class AccessibilityActivity extends AppCompatActivity {
    private ImageView backIcon;
    private AppCompatButton checkBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessibility);

        backIcon = findViewById(R.id.backIcon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        checkBtn = findViewById(R.id.btnAccess);
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SweetAlertDialog progressDialog = new SweetAlertDialog(AccessibilityActivity.this,
                        SweetAlertDialog.PROGRESS_TYPE);
                progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                progressDialog.setTitleText("Đang kiểm tra");
                progressDialog.setCancelable(true);
                progressDialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.hide();

                        if (numApp() == 0) {
                            new SweetAlertDialog(AccessibilityActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Chúc mừng!")
                                    .setContentText("Thiết bị của bạn không có ứng dụng nào đang sử dụng Accessibility.")
                                    .show();
                        }
                        else {
                            new SweetAlertDialog(AccessibilityActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Hãy kiểm tra lại!")
                                    .setContentText("Thiết bị của bạn có " + numApp() + " ứng dụng đang sử dụng Accessibility.")
                                    .show();
                        }
                    }
                }, 3000);
            }
        });
    }

    public int numApp() {
        AccessibilityManager manager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> list = manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);

        return list.size();
    }
}