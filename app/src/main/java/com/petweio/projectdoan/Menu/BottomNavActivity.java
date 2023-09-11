package com.petweio.projectdoan.Menu;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.petweio.projectdoan.fragments.HomeFragment;
import com.petweio.projectdoan.MyAppCompatActivity;
import com.petweio.projectdoan.fragments.PetFragment;
import com.petweio.projectdoan.R;
import com.petweio.projectdoan.fragments.SettingFragment;
import com.petweio.projectdoan.fragments.UserFragment;

public class BottomNavActivity extends MyAppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1001;
    private static final String TAG ="[BottomNavActivity]" ;
    private int selectTab = 1; // 1 - 4 tab, default is 1

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bottom_menu);
        requestPermision();
        init();

    }
    private void requestPermision()
    {
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
        else{
            Log.d(TAG, "permission denied");
        }

    }

    private void init(){
        final LinearLayout homeLayout = findViewById(R.id.homeLayout);
        final LinearLayout petsLayout = findViewById(R.id.petLayout);
        final LinearLayout userLayout = findViewById(R.id.userLayout);
        final LinearLayout settingsLayout = findViewById(R.id.settingLayout);

        final ImageView homeIMG = findViewById(R.id.homeIMG);
        final ImageView petIMG = findViewById(R.id.petIMG);
        final ImageView userIMG = findViewById(R.id.userIMG);
        final ImageView settingsIMG = findViewById(R.id.settingIMG);

        final TextView homeText = findViewById(R.id.homeText);
        final TextView petText = findViewById(R.id.petText);
        final TextView userText = findViewById(R.id.userText);
        final TextView settingText = findViewById(R.id.settingText);

        // set home default
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainer, HomeFragment.class,null)
                 .commit();

        homeLayout.setOnClickListener(v -> {
            if(selectTab != 1){
                // set home fragment
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragmentContainer, HomeFragment.class,null)
                        .commit();

                petText.setVisibility(View.GONE);
                userText.setVisibility(View.GONE);
                settingText.setVisibility(View.GONE);

                petIMG.setImageResource(R.drawable.ic_tracking);
                userIMG.setImageResource(R.drawable.ic_avatar);
                settingsIMG.setImageResource(R.drawable.ic_settings);

                petsLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.trans));
                userLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.trans));
                settingsLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.trans));
                // select home tab
                homeText.setVisibility(View.VISIBLE);
                homeIMG.setImageResource(R.drawable.ic_home_selected);
                homeLayout.setBackgroundResource(R.drawable.round_back_todo);

                // create animation
                ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f,1.0f,1f,1f, Animation.RELATIVE_TO_SELF,0.0f,Animation.RELATIVE_TO_SELF,0.0f);
                scaleAnimation.setDuration(200);
                scaleAnimation.setFillAfter(true);
                homeLayout.startAnimation(scaleAnimation);
                // set selected
                selectTab = 1;

            }
        });

        petsLayout.setOnClickListener(v -> {
            if(selectTab != 2){
               Fragment petFragment = new PetFragment();
                //set pet fragment
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragmentContainer, petFragment,null)
                        .commit();

                homeText.setVisibility(View.GONE);
                userText.setVisibility(View.GONE);
                settingText.setVisibility(View.GONE);

                homeIMG.setImageResource(R.drawable.ic_home);
                userIMG.setImageResource(R.drawable.ic_avatar);
                settingsIMG.setImageResource(R.drawable.ic_settings);

                homeLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.trans));
                userLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.trans));
                settingsLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.trans));
                // select home tab
                petText.setVisibility(View.VISIBLE);
                petIMG.setImageResource(R.drawable.ic_tracking_selected);
                petsLayout.setBackgroundResource(R.drawable.round_back_todo);

                // create animation
                ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f,1.0f,1f,1f, Animation.RELATIVE_TO_SELF,1.0f,Animation.RELATIVE_TO_SELF,0.0f);
                scaleAnimation.setDuration(200);
                scaleAnimation.setFillAfter(true);
                petsLayout.startAnimation(scaleAnimation);
                // set selected
                selectTab = 2;

            }
        });

        userLayout.setOnClickListener(v -> {
            if(selectTab != 3){
                //set user fragment
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragmentContainer, UserFragment.class,null)
                        .commit();

                petText.setVisibility(View.GONE);
                homeText.setVisibility(View.GONE);
                settingText.setVisibility(View.GONE);

                petIMG.setImageResource(R.drawable.ic_tracking);
                homeIMG.setImageResource(R.drawable.ic_home);
                settingsIMG.setImageResource(R.drawable.ic_settings);

                petsLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.trans));
                homeLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.trans));
                settingsLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.trans));
                // select home tab
                userText.setVisibility(View.VISIBLE);
                userIMG.setImageResource(R.drawable.ic_avatar_selected);
                userLayout.setBackgroundResource(R.drawable.round_back_todo);

                // create animation
                ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f,1.0f,1f,1f, Animation.RELATIVE_TO_SELF,1.0f,Animation.RELATIVE_TO_SELF,0.0f);
                scaleAnimation.setDuration(200);
                scaleAnimation.setFillAfter(true);
                userLayout.startAnimation(scaleAnimation);
                // set selected
                selectTab = 3;

            }
        });

        settingsLayout.setOnClickListener(v -> {
            if(selectTab != 4){
                //set settings fragment
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragmentContainer, SettingFragment.class,null)
                        .commit();

                petText.setVisibility(View.GONE);
                userText.setVisibility(View.GONE);
                homeText.setVisibility(View.GONE);

                petIMG.setImageResource(R.drawable.ic_tracking);
                userIMG.setImageResource(R.drawable.ic_avatar);
                homeIMG.setImageResource(R.drawable.ic_home);

                petsLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.trans));
                userLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.trans));
                homeLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.trans));
                // select home tab
                settingText.setVisibility(View.VISIBLE);
                settingsIMG.setImageResource(R.drawable.ic_settings_selected);
                settingsLayout.setBackgroundResource(R.drawable.round_back_todo);

                // create animation
                ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f,1.0f,1f,1f, Animation.RELATIVE_TO_SELF,1.0f,Animation.RELATIVE_TO_SELF,0.0f);
                scaleAnimation.setDuration(200);
                scaleAnimation.setFillAfter(true);
                settingsLayout.startAnimation(scaleAnimation);
                // set selected
                selectTab = 4;

            }
        });


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //if permission granted.
                Log.d(TAG,"Permission granted");

            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Log.e(TAG,"permission denied");
            }
        }
    }

}
