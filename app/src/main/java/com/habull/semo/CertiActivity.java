package com.habull.semo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.cazaea.sweetalert.SweetAlertDialog;

import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;

public class CertiActivity extends AppCompatActivity {
    private ImageView backIcon;
    private AppCompatButton checkBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certi);

        backIcon = findViewById(R.id.backIcon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        checkBtn = findViewById(R.id.btnCerti);
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SweetAlertDialog progressDialog = new SweetAlertDialog(CertiActivity.this,
                        SweetAlertDialog.PROGRESS_TYPE);
                progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                progressDialog.setTitleText("Đang kiểm tra");
                progressDialog.setCancelable(true);
                progressDialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.hide();

                        SweetAlertDialog dialog;
                        int num = getNumUserCerti();
                        if (num == 0) {
                            dialog = new SweetAlertDialog(CertiActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("An toàn!")
                                    .setContentText("Thiết bị của bạn không cài bất kì chứng chỉ nào từ bên ngoài.")
                                    .setCancelText("Đóng")
                                    .setConfirmText("Xem chứng chỉ");
                        }
                        else {
                            dialog = new SweetAlertDialog(CertiActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Cảnh báo!")
                                    .setContentText("Thiết bị của bạn cài " + num + " chứng chỉ từ bên ngoài.")
                                    .setCancelText("Đóng")
                                    .setConfirmText("Xem chứng chỉ");
                        }

                        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                Intent intent = new Intent(CertiActivity.this, ViewInfoCertiActivity.class);
                                startActivity(intent);
                            }
                        });
                        dialog.show();
                    }
                }, 3000);
            }
        });
    }

    public int getNumUserCerti() {
        int count = 0;

        try {
            KeyStore ks = KeyStore.getInstance("AndroidCAStore");

            if (ks != null) {
                ks.load(null, null);
                Enumeration<String> aliases = ks.aliases();

                while (aliases.hasMoreElements()) {
                    String alias = (String) aliases.nextElement();
                    X509Certificate cert = (X509Certificate) ks.getCertificate(alias);

                    if(cert.getIssuerDN().getName().contains("user"))
                        count++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }
}