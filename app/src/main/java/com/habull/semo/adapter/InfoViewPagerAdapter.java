package com.habull.semo.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.habull.semo.fragment.FragmentBattery;
import com.habull.semo.fragment.FragmentCpu;
import com.habull.semo.fragment.FragmentDevice;
import com.habull.semo.fragment.FragmentSystem;

public class InfoViewPagerAdapter extends FragmentPagerAdapter {
    private int numPage = 4;

    public InfoViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentCpu();
            case 1:
                return new FragmentDevice();
            case 2:
                return new FragmentSystem();
            case 3:
                return new FragmentBattery();
        }

        return null;
    }

    @Override
    public int getCount() {
        return numPage;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "CPU/GPU";
            case 1:
                return "DEVICE";
            case 2:
                return "SYSTEM";
            case 3:
                return "BATTERY";
        }

        return null;
    }
}