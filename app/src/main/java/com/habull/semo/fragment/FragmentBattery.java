package com.habull.semo.fragment;

import static android.content.Context.BATTERY_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.habull.semo.BatteryBroadcast;
import com.habull.semo.R;
import com.habull.semo.adapter.ListViewInfoDeviceAdapter;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class FragmentBattery extends Fragment {
    private ArrayList<Pair<String, String>> list = new ArrayList<>();
    private ListViewInfoDeviceAdapter adapter;
    private BatteryBroadcast batteryBroadcast = new BatteryBroadcast();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_battery, container, false);

        ListView listView = view.findViewById(R.id.batteryListView);

        adapter = new ListViewInfoDeviceAdapter(getContext(), list);
        listView.setAdapter(adapter);

        getActivity().registerReceiver(batteryBroadcast, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        list.clear();
        list.addAll(getBatteryInfo());
        adapter.notifyDataSetChanged();

        return view;
    }

    public ArrayList<Pair<String, String>> getBatteryInfo() {
        ArrayList<Pair<String, String>> result = new ArrayList<>();

        // get info from broadcast
        SharedPreferences sharedPref = getContext().getSharedPreferences("infoBattery", Context.MODE_PRIVATE);
        String level = sharedPref.getString("level", "0");
        String charging = sharedPref.getString("charging", "No");
        String typeCharging = sharedPref.getString("type charging", "AC");
        String health = sharedPref.getString("health", "Good");
        String technology = sharedPref.getString("technology", "unknow");
        int temp = sharedPref.getInt("temp", 0);
        int voltage = sharedPref.getInt("voltage", 0);

        list.add(new Pair<>("Battery level", level));
        list.add(new Pair<>("Charging", charging));

        if (charging.equals("Yes")) {
            // type charging
            list.add(new Pair<>("Type charging", typeCharging));

            // get time charging remaining
            BatteryManager batteryManager = (BatteryManager) getContext().getSystemService(BATTERY_SERVICE);
            long timeRemaining = batteryManager.computeChargeTimeRemaining();

            long hours = TimeUnit.MILLISECONDS.toHours(timeRemaining);
            timeRemaining -= TimeUnit.HOURS.toMillis(hours);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(timeRemaining);
            timeRemaining -= TimeUnit.MINUTES.toMillis(minutes);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(timeRemaining);

            result.add(new Pair<>("Time charging remaining", String.format("%d:%02d:%02d", hours, minutes, seconds)));
        }

        list.add(new Pair<>("Health", health));
        list.add(new Pair<>("Technology", technology));

        if (temp % 10 == 0)
            list.add(new Pair<>("Temperature", String.valueOf(temp / 10) + "°C"));
        else
            list.add(new Pair<>("Temperature", String.format("%.2f", (float) temp / 10) + "°C"));

        if (voltage % 1000 == 0)
            list.add(new Pair<>("Voltage", String.valueOf(voltage / 1000) + "V"));
        else
            list.add(new Pair<>("Voltage", String.format("%.2f", (float) voltage / 1000) + "V"));

        adapter.notifyDataSetChanged();

        return result;
    }
}
