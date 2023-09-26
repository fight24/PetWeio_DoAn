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

import com.petweio.projectdoan.MyAppCompatActivity;
import com.petweio.projectdoan.R;
import com.petweio.projectdoan.fragments.HomeFragment;
import com.petweio.projectdoan.fragments.MapFragment;
import com.petweio.projectdoan.fragments.SettingFragment;
import com.petweio.projectdoan.fragments.UserFragment;
import com.petweio.projectdoan.service.MqttClientManager;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Objects;

public class BottomNavActivity extends MyAppCompatActivity {
    private static final String TAG ="[BottomNavActivity]" ;
    private static final int REQUEST_LOCATION_PERMISSION = 1001;

    private int selectTab = 1; // 1 - 4 tab, default is 1

    private static final String BROKER_URL = "tcp://f3aab273.emqx.cloud:1883";// "tcp://namcu.ddns.net:1883"
    private static final String CLIENT_ID = "your_client_id";
    MqttAndroidClient mqttAndroidClient;
    MqttConnectOptions mqttConnectOptions;

    LinearLayout homeLayout ;
    LinearLayout petsLayout ;
    LinearLayout userLayout;
    LinearLayout settingsLayout;

    ImageView homeIMG ;
    ImageView petIMG ;
    ImageView userIMG ;
    ImageView settingsIMG;

    TextView homeText ;
    TextView petText ;
    TextView userText ;
    TextView settingText;
    static MqttClientManager mqttClientManager = new MqttClientManager();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bottom_menu);
        requestPermission();
        init();

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    private void requestPermission()
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
            Log.d(TAG, "permission granted");
        }

    }

    private void init(){
         homeLayout = findViewById(R.id.homeLayout);
         petsLayout = findViewById(R.id.petLayout);
         userLayout = findViewById(R.id.userLayout);
         settingsLayout = findViewById(R.id.settingLayout);

         homeIMG = findViewById(R.id.homeIMG);
         petIMG = findViewById(R.id.petIMG);
         userIMG = findViewById(R.id.userIMG);
         settingsIMG = findViewById(R.id.settingIMG);

         homeText = findViewById(R.id.homeText);
         petText = findViewById(R.id.petText);
         userText = findViewById(R.id.userText);
         settingText = findViewById(R.id.settingText);
         setMqtt();







    }

    private void homeLayout(HomeFragment homeFragment){
        homeLayout.setOnClickListener(v -> {
            if(selectTab != 1){

                // set home fragment

                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragmentContainer, homeFragment,null)
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

    }
    private void petLayout(MapFragment mapFragment){

        petsLayout.setOnClickListener(v -> {
            if(selectTab != 2){

                //set pet fragment
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragmentContainer, mapFragment,null)
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
    }
    private void profileLayout(UserFragment userFragment){
        userLayout.setOnClickListener(v -> {
            if(selectTab != 3){
                //set user fragment
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragmentContainer, userFragment,null)
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
    }
    private void settingsLayout(SettingFragment settingFragment){
        settingsLayout.setOnClickListener(v -> {
            if(selectTab != 4){
                //set settings fragment
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragmentContainer, settingFragment,null)
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
    private void setMqtt(){
        String topic = "wemeio24";
        mqttAndroidClient = new MqttAndroidClient(this.getApplicationContext(),BROKER_URL,CLIENT_ID);
        mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setUserName("nam");
        mqttConnectOptions.setPassword("nam".toCharArray());
        try {
            IMqttToken token = mqttAndroidClient.connect(mqttConnectOptions);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Connected");
                    mqttSub(mqttAndroidClient,topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(TAG, "Failed to connect");
                }
            });
        }catch (MqttException e){
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
        MqttClientManager.setMqttClient(mqttAndroidClient);
        HomeFragment homeFragment = HomeFragment.newInstance(mqttClientManager);
//        PetFragmentMapBox petFragment = PetFragmentMapBox.newInstance(mqttClientManager);

        UserFragment userFragment = new UserFragment();
        SettingFragment settingFragment = new SettingFragment();
        MapFragment mapFragment = MapFragment.newInstance(mqttClientManager);
        // set home default
//        getSupportFragmentManager().beginTransaction()
//                .setReorderingAllowed(true)
//                .replace(R.id.fragmentContainer, homeFragment,null)
//                .commit();
        runOnUiThread(() ->{
            homeLayout(homeFragment);
            petLayout(mapFragment);
            profileLayout(userFragment);
            settingsLayout(settingFragment);
        });

    }
    private void mqttSub(MqttAndroidClient client,String topic){
        int qos = 1;
        try{
            IMqttToken subToken = client.subscribe(topic,qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });


        }catch (MqttException e){
            Log.e(TAG,"onFailure mqttSub :" + e);
        }

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
