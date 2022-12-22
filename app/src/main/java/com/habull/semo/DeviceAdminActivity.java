package com.habull.semo;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.cazaea.sweetalert.SweetAlertDialog;
import com.google.android.datatransport.runtime.dagger.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeviceAdminActivity extends AppCompatActivity {
    private ImageView backIcon;
    private AppCompatButton checkBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deviceadmin);

        backIcon = findViewById(R.id.backIcon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        checkBtn = findViewById(R.id.btnDeviceApp);
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SweetAlertDialog progressDialog = new SweetAlertDialog(DeviceAdminActivity.this,
                        SweetAlertDialog.PROGRESS_TYPE);
                progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                progressDialog.setTitleText("Đang kiểm tra");
                progressDialog.setCancelable(true);
                progressDialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.hide();
                        ArrayList<String> result = listApp();

                        if (result.size() == 0) {
                            new SweetAlertDialog(DeviceAdminActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Chúc mừng!")
                                    .setContentText("Thiết bị của bạn không có ứng dụng nào dùng Device Admin.")
                                    .show();
                        }
                        else {
                            String text = "Thiết bị của bạn có " + result.size() + " ứng dụng đang sử dụng Device Admin.\n\n\n\n";
                            for (String i: result)
                                text += i + "\n";

                            new SweetAlertDialog(DeviceAdminActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Hãy kiểm tra lại!")
                                    .setContentText(text)
                                    .show();
                        }
                    }
                }, 3000);
            }
        });
    }

    public ArrayList<String> listApp() {
        ArrayList<String> result = new ArrayList<>();

        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        List<ComponentName> list = dpm.getActiveAdmins();
        for (ComponentName i: list)
            result.add(i.getPackageName());

        return result;
    }
}