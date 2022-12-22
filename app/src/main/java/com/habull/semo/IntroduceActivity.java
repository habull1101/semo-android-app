package com.habull.semo;

import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroCustomLayoutFragment;
import com.github.appintro.AppIntroPageTransformerType;

import org.jetbrains.annotations.Nullable;

public class IntroduceActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.introduce1));
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.introduce2));
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.introduce3));

        setProgressIndicator();
        setImmersiveMode();
    }

    @Override
    protected void onSkipPressed(@Nullable Fragment currentFragment) {
        super.onSkipPressed(currentFragment);

        finish();
    }

    @Override
    protected void onDonePressed(@Nullable Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        finish();
    }
}