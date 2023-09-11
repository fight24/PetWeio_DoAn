package com.petweio.projectdoan.splash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.petweio.projectdoan.Introduction.IntroductionActivity;
import com.petweio.projectdoan.MyAppCompatActivity;
import com.petweio.projectdoan.R;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends MyAppCompatActivity {
    private static final int SPLASH_TIME=10000;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        init();
    }
    @SuppressLint("SetTextI18n")
    private void init(){


        TextView tvAppVersion = findViewById(R.id.tvAppVersion);
        tvAppVersion.setText(R.string.msg_app_version_1_0);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, IntroductionActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            finish();
        }, SPLASH_TIME);
    }
}
