package com.habull.semo.fragment;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.habull.semo.AccessibilityActivity;
import com.habull.semo.CertiActivity;
import com.habull.semo.DeviceAdminActivity;
import com.habull.semo.R;
import com.habull.semo.RootActivity;
import com.habull.semo.SourceAppActivity;
import com.habull.semo.mdm.LostDeviceActivity;

public class FragmentSecurity extends Fragment implements View.OnClickListener {
    private Switch btnMdm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_security, container, false);

        ConstraintLayout block1 = view.findViewById(R.id.block1);
        ConstraintLayout root = view.findViewById(R.id.root);
        ConstraintLayout certi = view.findViewById(R.id.certi);
        ConstraintLayout sourceApp = view.findViewById(R.id.sourceApp);
        ConstraintLayout accessibility = view.findViewById(R.id.accessibility);
        ConstraintLayout deviceAdmin = view.findViewById(R.id.mdmApp);

        btnMdm = view.findViewById(R.id.btnMdm);
        updateBtnMdm();

        btnMdm.setOnClickListener(this);
        block1.setOnClickListener(this);
        root.setOnClickListener(this);
        certi.setOnClickListener(this);
        sourceApp.setOnClickListener(this);
        accessibility.setOnClickListener(this);
        deviceAdmin.setOnClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateBtnMdm();
    }

    public void updateBtnMdm() {
        SharedPreferences sharedPref = getContext().getSharedPreferences("semo", Context.MODE_PRIVATE);
        Boolean valueBtnMdm = sharedPref.getBoolean("btnStatus", false);
        btnMdm.setChecked(valueBtnMdm);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.block1:
                Intent intent1 = new Intent(getActivity(), LostDeviceActivity.class);
                intent1.putExtra("status", btnMdm.isChecked());
                startActivity(intent1);

                break;
            case R.id.btnMdm:
                if (btnMdm.isChecked()) {
                    Intent intent2 = new Intent(getActivity(), LostDeviceActivity.class);
                    intent2.putExtra("status", btnMdm.isChecked());
                    startActivity(intent2);
                }

                break;
            case R.id.root:
                Intent intent3 = new Intent(getActivity(), RootActivity.class);
                startActivity(intent3);

                break;
            case R.id.certi:
                Intent intent4 = new Intent(getActivity(), CertiActivity.class);
                startActivity(intent4);

                break;
            case R.id.sourceApp:
                Intent intent5 = new Intent(getActivity(), SourceAppActivity.class);
                startActivity(intent5);

                break;
            case R.id.accessibility:
                Intent intent6 = new Intent(getActivity(), AccessibilityActivity.class);
                startActivity(intent6);

                break;
            case R.id.mdmApp:
                Intent intent7 = new Intent(getActivity(), DeviceAdminActivity.class);
                startActivity(intent7);

                break;
        }
    }
}
