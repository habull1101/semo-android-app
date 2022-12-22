package com.habull.semo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.cazaea.sweetalert.SweetAlertDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class RootActivity extends AppCompatActivity {
    private ImageView backIcon;
    private AppCompatButton checkBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);

        backIcon = findViewById(R.id.backIcon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        checkBtn = findViewById(R.id.btnRoot);
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SweetAlertDialog progressDialog = new SweetAlertDialog(RootActivity.this,
                        SweetAlertDialog.PROGRESS_TYPE);
                progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                progressDialog.setTitleText("Đang kiểm tra");
                progressDialog.setCancelable(true);
                progressDialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.hide();

                        if (isRoot()) {
                            new SweetAlertDialog(RootActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Xin chúc mừng!")
                                    .setContentText("Quyền root hợp lệ trên thiết bị này")
                                    .show();
                        }
                        else {
                            new SweetAlertDialog(RootActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Xin lỗi!")
                                    .setContentText("Quyền root không hợp lệ trên thiết bị này")
                                    .show();
                        }
                    }
                }, 3000);
            }
        });
    }

    public boolean isRoot() {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
    }

    public boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    public boolean checkRootMethod2() {
        String[] paths = {"/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
        for (String path : paths)
            if (new File(path).exists())
                return true;

        return false;
    }

    public boolean checkRootMethod3() {
        Process process = null;

        try {
            process = Runtime.getRuntime().exec(new String[] {"/system/xbin/which", "su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null)
                return true;

            return false;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null)
                process.destroy();
        }
    }
}