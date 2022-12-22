package com.habull.semo.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.habull.semo.fragment.FragmentCertiTrusted;
import com.habull.semo.fragment.FragmentCertiUser;

public class AdapterInfoCertiViewPager extends FragmentPagerAdapter {
    private int numPage;

    public AdapterInfoCertiViewPager(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        numPage = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentCertiTrusted();
            case 1:
                return new FragmentCertiUser();
        }

        return null;
    }


    @Override
    public int getCount() {
        return numPage;
    }
}
