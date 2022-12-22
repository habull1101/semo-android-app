package com.habull.semo.fragment;

import android.app.ActivityManager;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.habull.semo.R;
import com.habull.semo.adapter.ListViewInfoDeviceAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FragmentDevice extends Fragment {
    private ArrayList<Pair<String, String>> list = new ArrayList<>();
    private ArcProgress ramCircle, romCircle;
    private TextView ram1, ram2, ram3, rom1, rom2, rom3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device, container, false);

        ListView listView = view.findViewById(R.id.deviceListView);
        ramCircle = view.findViewById(R.id.ramCircle);
        romCircle = view.findViewById(R.id.romCircle);
        ram1 = view.findViewById(R.id.ramTotal);
        ram2 = view.findViewById(R.id.ramUsed);
        ram3 = view.findViewById(R.id.ramUnused);

        rom1 = view.findViewById(R.id.romTotal);
        rom2 = view.findViewById(R.id.romUsed);
        rom3 = view.findViewById(R.id.romUnused);

        list.clear();
        list.addAll(getDeviceInfo());

        ListViewInfoDeviceAdapter adapter = new ListViewInfoDeviceAdapter(getContext(), list);
        listView.setAdapter(adapter);

        executeStorage();

        return view;
    }

    public ArrayList<Pair<String, String>> getDeviceInfo() {
        ArrayList<Pair<String, String>> result = new ArrayList<>();

        // get name
        result.add(new Pair<>("Name", Build.MODEL));

        // get manufacturer
        result.add(new Pair<>("Manufacturer", Build.MANUFACTURER));

        // get brand
        result.add(new Pair<>("Brand", Build.BRAND));

        // get screen size
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getRealSize(size);
        int widthScreen = size.x;
        int heightScreen = size.y;

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        double xdpi = metrics.xdpi;
        double ydpi = metrics.ydpi;

        double screenInches = Math.sqrt(Math.pow(widthScreen/xdpi, 2) + Math.pow(heightScreen/ydpi, 2));

        result.add(new Pair<>("Screen size", String.format("%.2f", screenInches) + " inches"));

        // get screen resolution
        result.add(new Pair<>("Screen resolution", size.x + " * " + size.y + " pixels"));

        // get screen density
        int density = (int)(metrics.density * 160f);
        result.add(new Pair<>("Screen density", density + " dpi"));

        // get external storage
        boolean checkSdCard = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

        if (checkSdCard) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long totalBlock = stat.getBlockCountLong();
            float totalSd = (float) totalBlock * blockSize / 1024 / 1024 / 1024;

            result.add(new Pair<>("Total SD card size", String.format("%.2f", totalSd) + " GB"));

            long  availableBlock = stat.getAvailableBlocksLong();
            float availableSd = (float) availableBlock * blockSize / 1024 / 1024 / 1024;
            float percent = availableSd/totalSd * 100;

            result.add(new Pair<>("Available SD card size", String.format("%.2f", availableSd) + " GB ("
                    + String.format("%.2f", percent) + "%)"));
        }
        else {
            result.add(new Pair<>("Total SD card size", ""));
            result.add(new Pair<>("Available SD card size", ""));
        }
        return result;
    }

    public void executeStorage() {
        // get total ram
        ActivityManager actManager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);

        long totalRam = memInfo.totalMem/1024/1024;
        ram1.setText("Tổng RAM: " + String.valueOf(totalRam) + " MB");

        // get available, used ram
        long availableRam = memInfo.availMem/1024/1024;
        long usedRam = totalRam - availableRam;
        ram2.setText("Đã dùng: " + usedRam + " MB");
        ram3.setText("Còn lại: " + availableRam + " MB");

        float percentUsed = (float)availableRam/totalRam * 100;
        ramCircle.setProgress(100 - (int) percentUsed);

        // get internal storage
        StorageStatsManager storageStatsManager = (StorageStatsManager) getActivity().getSystemService(Context.STORAGE_STATS_SERVICE);
        StorageManager storageManager = (StorageManager) getActivity().getSystemService(Context.STORAGE_SERVICE);

        UUID uuid = null;
        List<StorageVolume> storageVolumes = storageManager.getStorageVolumes();
        for (StorageVolume i : storageVolumes) {
            String uuidStr = i.getUuid();

            if (uuidStr == null)
                uuid = StorageManager.UUID_DEFAULT;
            else
                uuid = UUID.nameUUIDFromBytes(i.getUuid().getBytes());

            try {
                String totalRom = Formatter.formatShortFileSize(getContext(), storageStatsManager.getTotalBytes(uuid));
                rom1.setText("Tổng ROM: " + totalRom);
                String[] split1 = totalRom.split(" ");
                float totalSize = Float.parseFloat(split1[0]);

                String freeRom = Formatter.formatShortFileSize(getContext(), storageStatsManager.getFreeBytes(uuid));
                rom3.setText("Còn lại: " + freeRom);
                String[] split2 = freeRom.split(" ");
                float freeSize = Float.parseFloat(split2[0]);

                rom2.setText("Đã dùng: " + (totalSize - freeSize) + " GB");

                float percent = freeSize / totalSize * 100;
                romCircle.setProgress(100 - (int) percent);
            } catch (Exception e) {
            }
        }
    }
}
