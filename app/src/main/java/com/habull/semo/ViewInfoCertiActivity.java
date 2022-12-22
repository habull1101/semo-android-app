package com.habull.semo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.habull.semo.adapter.AdapterInfoCertiViewPager;
import com.habull.semo.adapter.AdapterMainViewPager;
import com.habull.semo.adapter.ListViewInfoCertiAdapter;

import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;

public class ViewInfoCertiActivity extends AppCompatActivity {
    private ImageView backIcon;
    private ViewPager viewPager;
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_info_certi);

        backIcon = findViewById(R.id.backIcon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        viewPager = findViewById(R.id.viewPager);
        navigationView = findViewById(R.id.bottomNavi);

        AdapterInfoCertiViewPager adapter = new AdapterInfoCertiViewPager(getSupportFragmentManager(), 2);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        navigationView.setSelectedItemId(R.id.trusted);
                        break;
                    case 1:
                        navigationView.setSelectedItemId(R.id.user);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.trusted:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.user:
                        viewPager.setCurrentItem(1);
                        break;
                }

                return true;
            }
        });
    }
}