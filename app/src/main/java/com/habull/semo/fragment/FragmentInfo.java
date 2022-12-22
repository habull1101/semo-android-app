package com.habull.semo.fragment;

import android.icu.text.IDNA;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.habull.semo.R;
import com.habull.semo.adapter.InfoViewPagerAdapter;

public class FragmentInfo extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager viewPager = view.findViewById(R.id.viewPager);

        InfoViewPagerAdapter adapter = new InfoViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}
