package com.habull.semo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.habull.semo.adapter.AdapterMainViewPager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private AnimatedBottomBar navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // run introduce activity
        SharedPreferences sharedPref = getSharedPreferences("semo", Context.MODE_PRIVATE);
        boolean check = sharedPref.getBoolean("checkFirstUse", true);

        if (check) {
            Intent intent = new Intent(this, IntroduceActivity.class);
            startActivity(intent);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("checkFirstUse", false);
            editor.commit();
        }

        viewPager = findViewById(R.id.viewPager);
        navigationView = findViewById(R.id.bottomNavi);

        AdapterMainViewPager adapter = new AdapterMainViewPager(getSupportFragmentManager(), 2);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        navigationView.selectTabById(R.id.info, true);
                        break;
                    case 1:
                        navigationView.selectTabById(R.id.security, true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        navigationView.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NotNull AnimatedBottomBar.Tab tab1) {
                viewPager.setCurrentItem(i1);
            }

            @Override
            public void onTabReselected(int i, @NotNull AnimatedBottomBar.Tab tab) {

            }
        });
    }
}