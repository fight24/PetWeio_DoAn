package com.petweio.projectdoan.Introduction;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.petweio.projectdoan.MyAppCompatActivity;
import com.petweio.projectdoan.R;
import com.petweio.projectdoan.Sign.LoginActivity;
import com.petweio.projectdoan.Sign.SignUpActivity;


public class IntroductionActivity extends MyAppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);
        init();
    }

    private void init(){
        runOnUiThread(this::addAnim);
        setMyButton();
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