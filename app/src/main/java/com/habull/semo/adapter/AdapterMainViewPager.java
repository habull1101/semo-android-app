package com.habull.semo.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.habull.semo.fragment.FragmentInfo;
import com.habull.semo.fragment.FragmentSecurity;

public class AdapterMainViewPager extends FragmentPagerAdapter {
    private int numPage;

    public AdapterMainViewPager(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        numPage = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentInfo();
            case 1:
                return new FragmentSecurity();
        }

        return null;
    }


    @Override
    public int getCount() {
        return numPage;
    }
}
