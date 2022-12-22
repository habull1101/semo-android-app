package com.habull.semo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.cazaea.sweetalert.SweetAlertDialog;

import java.util.ArrayList;
import java.util.List;

public class SourceAppActivity extends AppCompatActivity {
    private ImageView backIcon;
    private AppCompatButton checkBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_app);

        backIcon = findViewById(R.id.backIcon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        checkBtn = findViewById(R.id.btnSourceApp);
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SweetAlertDialog progressDialog = new SweetAlertDialog(SourceAppActivity.this,
                        SweetAlertDialog.PROGRESS_TYPE);
                progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                progressDialog.setTitleText("Đang kiểm tra");
                progressDialog.setCancelable(true);
                progressDialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.hide();

                        ArrayList<String> result = getNumAppFromManually();
                        if (result.size() == 0) {
                            new SweetAlertDialog(SourceAppActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("An toàn!")
                                    .setContentText("Thiết bị của bạn không cài ứng dụng nào từ ngoài Play Store.")
                                    .show();

                        } else {
                            String text = "Thiết bị của bạn cài " + result.size() + " ứng dụng không phải từ Play Store.\n\n\n\n";
                            for (String i: result)
                                text += i + "\n";

                            new SweetAlertDialog(SourceAppActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Cảnh báo!")
                                    .setContentText(text)
                                    .show();
                        }
                    }
                }, 3000);
            }
        });
    }

    public ArrayList<String> getNumAppFromManually() {
        ArrayList<String> result = new ArrayList<>();

        PackageManager pm = getPackageManager();
        List<ApplicationInfo> listApp = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo i: listApp) {
            String s = pm.getInstallerPackageName(i.packageName);
            if (s != null)
            Log.i("hehe", i.packageName + " " + s);
            if (s == null)
                continue;

            if (((i.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
                    && !s.equals("com.google.android") && !s.equals("com.google.android.packageinstaller"))
                result.add(i.packageName);
        }

        return result;
    }
}