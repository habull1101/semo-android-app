package com.habull.semo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.util.Log;

public class BatteryBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int charging = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        int typeCharging = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
        String technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
        int temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);

        String chargingStr = "No";
        if (charging == BatteryManager.BATTERY_STATUS_CHARGING)
            chargingStr = "Yes";
        if (charging == BatteryManager.BATTERY_STATUS_NOT_CHARGING)
            chargingStr = "No";

        String typeChargingStr = "";
        if (typeCharging == BatteryManager.BATTERY_PLUGGED_AC)
            typeChargingStr = "AC";
        if (typeCharging == BatteryManager.BATTERY_PLUGGED_USB)
            typeChargingStr = "USB";
        if (typeCharging == BatteryManager.BATTERY_PLUGGED_WIRELESS)
            typeChargingStr = "Wireless";


        String healthStr = "";
        if (health == BatteryManager.BATTERY_HEALTH_COLD)
            healthStr = "Cold";
        if (health == BatteryManager.BATTERY_HEALTH_DEAD)
            healthStr = "Dead";
        if (health == BatteryManager.BATTERY_HEALTH_GOOD)
            healthStr = "Good";
        if (health == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE)
            healthStr = "Over-Voltage";
        if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT)
            healthStr = "Overheat";
        if (health == BatteryManager.BATTERY_HEALTH_UNKNOWN)
            healthStr = "Unknown";


        // save info to sharedPreferences
        SharedPreferences sharedPref = context.getSharedPreferences("infoBattery", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("level", String.valueOf(level));
        editor.putString("charging", chargingStr);
        editor.putString("type charging", typeChargingStr);
        editor.putString("health", healthStr);
        editor.putString("technology", technology);
        editor.putInt("temp", temp);
        editor.putInt("voltage", voltage);
        editor.apply();
    }
}