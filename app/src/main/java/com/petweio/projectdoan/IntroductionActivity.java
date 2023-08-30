package com.petweio.projectdoan;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import com.petweio.projectdoan.Sign.LoginActivity;
import com.petweio.projectdoan.Sign.SignUpActivity;


public class IntroductionActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_introduction);
        init();
    }

    private void init(){
        runOnUiThread(this::addAnim);
        setMyButton();
    }
    private void setStatusBar(){
        Window window = getWindow();
        Drawable gradientDrawable = ContextCompat.getDrawable(this,R.drawable.gradient_status_bar);
        window.setStatusBarColor(Color.TRANSPARENT); // Make the status bar transparent
        window.setBackgroundDrawable(gradientDrawable); // Apply the gradient as the background
        // Enable immersive mode with sticky immersive flag

    }

    private void setMyButton(){
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnSignUp = findViewById(R.id.btnSignup);
        btnLogin.setOnClickListener(view ->{
            // overridePendingTransition added to both onCreate of Test and MainActivity
            Intent intent=new Intent(IntroductionActivity.this, LoginActivity.class);
                startActivity(intent);
                    overridePendingTransition(R.anim.slide_left_in,R.anim.fade_out );
        });
        btnSignUp.setOnClickListener(view ->{
            // overridePendingTransition added to both onCreate of Test and MainActivity
            Intent intent=new Intent(IntroductionActivity.this, SignUpActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_right_in,R.anim.fade_out );
        });
    }
    private void addAnim(){
        FrameLayout frameGraphic= findViewById(R.id.frameGraphicPart);
        LinearLayout linearColumnTitle = findViewById(R.id.linearColumntitle);
        Animation topAnim = AnimationUtils.loadAnimation(this,R.anim.slide_up_to_down);
        Animation bottomAnim = AnimationUtils.loadAnimation(this,R.anim.slide_down_to_up);
        frameGraphic.setAnimation(topAnim);
        linearColumnTitle.setAnimation(bottomAnim);
    }

}