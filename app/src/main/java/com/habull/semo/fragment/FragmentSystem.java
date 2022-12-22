package com.habull.semo.fragment;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.GoogleApiAvailability;
import com.habull.semo.R;
import com.habull.semo.adapter.ListViewInfoDeviceAdapter;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class FragmentSystem extends Fragment {
    private ArrayList<Pair<String, String>> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_system, container, false);

        ListView listView = view.findViewById(R.id.systemListView);

        list.clear();
        list.addAll(getSystemInfo());

        ListViewInfoDeviceAdapter adapter = new ListViewInfoDeviceAdapter(getContext(), list);
        listView.setAdapter(adapter);

        return view;
    }

    public ArrayList<Pair<String, String>> getSystemInfo() {
        ArrayList<Pair<String, String>> result = new ArrayList<>();

        // get android version
        Field[] fields = Build.VERSION_CODES.class.getFields();
        String codeName = "";
        for (Field field : fields) {
            try {
                if (field.getInt(Build.VERSION_CODES.class) == Build.VERSION.SDK_INT) {
                    codeName = field.getName();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        result.add(new Pair<>("Android version", Build.VERSION.RELEASE + " (Android " + codeName + ")"));

        // get api level
        result.add(new Pair<>("Android API level", String.valueOf(Build.VERSION.SDK_INT)));

        // get security patch level
        String dateOld = Build.VERSION.SECURITY_PATCH;
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date date = inputFormat.parse(dateOld);
            String dateNew = outputFormat.format(date);

            result.add(new Pair<>("Security patch level", dateNew));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // get build level
        result.add(new Pair<>("Build level", Build.DISPLAY));

        // get Java JVM
        String jvmVersion = System.getProperty("java.vm.version");
        String jvmName = "";
        String[] split = System.getProperty("java.vm.version").split("\\.");
        if (Integer.parseInt(split[0]) >= 2)
            jvmName = "ART";
        else
            jvmName = System.getProperty("java.vm.name");

        result.add(new Pair<>("Java VM", jvmName + " " + jvmVersion));

        // get OpenGL ES
        final ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();

        result.add(new Pair<>("OpenGL SL version", configurationInfo.getGlEsVersion()));

        // get Kernel Architecture
        result.add(new Pair<>("Kernel Architecture", System.getProperty("os.arch")));

        // get Kernel version
        result.add(new Pair<>("Kernel version", System.getProperty("os.version")));

        // get Google play services version
        int apkVersion = GoogleApiAvailability.getInstance().getApkVersion(getContext());

        result.add(new Pair<>("Google Play Service version", String.valueOf(apkVersion)));

        // get system uptime
        long uptime = SystemClock.uptimeMillis();

        long hours = TimeUnit.MILLISECONDS.toHours(uptime);
        uptime -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(uptime);
        uptime -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(uptime);

        result.add(new Pair<>("System uptime", String.format("%d:%02d:%02d", hours, minutes, seconds)));

        return result;
    }
}
