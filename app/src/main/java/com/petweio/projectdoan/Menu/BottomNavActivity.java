package com.petweio.projectdoan.Menu;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;

import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.petweio.projectdoan.Model.Device;
import com.petweio.projectdoan.MyAppCompatActivity;
import com.petweio.projectdoan.Notification.MyApplication;
import com.petweio.projectdoan.R;
import com.petweio.projectdoan.fragments.AboutFragment;
import com.petweio.projectdoan.fragments.HomeFragment;
import com.petweio.projectdoan.fragments.MapFragment;
import com.petweio.projectdoan.fragments.NotFFoundFragment;
import com.petweio.projectdoan.fragments.SettingFragment;
import com.petweio.projectdoan.service.LocationService;
import com.petweio.projectdoan.service.MqttViewModel;
import com.petweio.projectdoan.service.MyViewModel;
import com.petweio.projectdoan.splash.SplashActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomNavActivity extends MyAppCompatActivity {
    private static final String TAG ="[BottomNavActivity]" ;
    public static final int REQUEST_LOCATION_PERMISSION = 1001;

    private int selectTab = 1; // 1 - 4 tab, default is 1
    private static final String BROKER_URL = "tcp://petweioapp.online:1883";// "tcp://namcu.ddns.net:1883"
    private static final String CLIENT_ID = "mqtt_pro_max";

    MqttConnectOptions mqttConnectOptions;
    MqttAndroidClient mqttAndroidClient;
    LinearLayout homeLayout ,petsLayout,settingsLayout,aboutLayout,bottomMenuBar;

    ImageView homeIMG , petIMG,settingsIMG,aboutIMG;
    TextView homeText ,petText ,settingText,aBoutText;
    FragmentContainerView fragmentContainerView ;
    FrameLayout loading ;
    ProgressBar progressBarCustom;
    List<Device> devices = new ArrayList<>();
    MapFragment mapFragment;
    MqttViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_bottom_menu);
        requestPermission();
        init();

    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mqttAndroidClient.isConnected()){
            try {
                mqttAndroidClient.disconnect();
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        }
        Log.d(TAG,"onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");
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
         aboutLayout = findViewById(R.id.aboutLayout);
         settingsLayout = findViewById(R.id.settingLayout);
         fragmentContainerView = findViewById(R.id.fragmentContainer);
         bottomMenuBar = findViewById(R.id.bottomBar);
         loading = findViewById(R.id.loadingBottom);
         progressBarCustom = findViewById(R.id.progressBarCustom);

         homeIMG = findViewById(R.id.homeIMG);
         petIMG = findViewById(R.id.petIMG);
         aboutIMG = findViewById(R.id.aboutIMG);
         settingsIMG = findViewById(R.id.settingIMG);

         homeText = findViewById(R.id.homeText);
         petText = findViewById(R.id.petText);
         settingText = findViewById(R.id.settingText);
         aBoutText = findViewById(R.id.aboutText);
         aboutIMG = findViewById(R.id.aboutIMG);
        progressBarCustom.setIndeterminateDrawable(new WanderingCubes());

        setItemMenu();
         new Handler().postDelayed(() ->{
             loading.setVisibility(View.GONE);
            fragmentContainerView.setVisibility(View.VISIBLE);
            bottomMenuBar.setVisibility(View.VISIBLE);
         },5000);
    }

    private void checkDevice(String name, MapFragment mapFragment,NotFFoundFragment notFFoundFragment,AboutFragment aboutFragment) {
        Call<List<Device>> call = apiService.showDevicesFromUser(name);

        call.enqueue(new Callback<List<Device>>() {
            @Override
            public void onResponse(@NonNull Call<List<Device>> call, @NonNull Response<List<Device>> response) {
                if(response.isSuccessful()){
                    devices = response.body();
                    Log.d(TAG,"Ok show : "+ devices);
                    petLayout(mapFragment,notFFoundFragment,devices);
                    aboutLayout(aboutFragment,notFFoundFragment,devices);
                    for(Device device : devices){
                        if(device.isIs_warning()){
                            ((MyApplication)getApplication()).triggerNotificationWithBackStack(SplashActivity.class,
                                    getString(R.string.NEWS_CHANNEL_ID),
                                    "Notification",
                                    "You enabled warning in "+device.getNameDevice(),
                                    "You enabled warning in "+device.getNameDevice(),
                                    NotificationCompat.PRIORITY_HIGH,
                                    true,
                                    getResources().getInteger(R.integer.notificationId),
                                    PendingIntent.FLAG_UPDATE_CURRENT);
                            Intent serviceMqtt = new Intent(BottomNavActivity.this, LocationService.class);
                            startService(serviceMqtt);
                        }
                    }

                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Device>> call, @NonNull Throwable t) {
                Log.d(TAG, "show Devices : onFailure: ");
            }
        });
        Log.d(TAG,"List[0]"+ devices.toString());

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
                aBoutText.setVisibility(View.GONE);
                settingText.setVisibility(View.GONE);

                petIMG.setImageResource(R.drawable.ic_tracking);
                aboutIMG.setImageResource(R.drawable.ic_list);
                settingsIMG.setImageResource(R.drawable.ic_settings);

                petsLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.trans));
                aboutLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.trans));
                settingsLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.trans));
                // select home tab
                homeText.setVisibility(View.VISIBLE);
                homeIMG.setImageResource(R.drawable.ic_home_selected);
                homeLayout.setBackgroundResource(R.drawable.round_back_todo);

                // create animation
                ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f,1.0f,1f,1f, Animation.RELATIVE_TO_SELF,0.0f,Animation.RELATIVE_TO_SELF,0.0f);
                scaleAnimation.setDuration(500);
                scaleAnimation.setFillAfter(true);
                homeLayout.startAnimation(scaleAnimation);
                // set selected
                selectTab = 1;

            }
        });

    }
    private void petLayout(MapFragment mapFragment, NotFFoundFragment notFFoundFragment, List<Device> deviceList){
        petsLayout.setOnClickListener(v -> {
            if(selectTab != 2){
                if(deviceList.isEmpty()){
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragmentContainer, notFFoundFragment,null)
                            .commit();
                }else{

                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragmentContainer, mapFragment,null)
                            .commit();
                }
                homeText.setVisibility(View.GONE);
                settingText.setVisibility(View.GONE);
                aBoutText.setVisibility(View.GONE);

                homeIMG.setImageResource(R.drawable.ic_home);
                settingsIMG.setImageResource(R.drawable.ic_settings);
                aboutIMG.setImageResource(R.drawable.ic_list);

                aboutLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.trans));
                homeLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.trans));
                settingsLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.trans));
                // select home tab
                petText.setVisibility(View.VISIBLE);
                petIMG.setImageResource(R.drawable.ic_tracking_selected);
                petsLayout.setBackgroundResource(R.drawable.round_back_todo);

                // create animation
                ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f,1.0f,1f,1f, Animation.RELATIVE_TO_SELF,1.0f,Animation.RELATIVE_TO_SELF,0.0f);
                scaleAnimation.setDuration(500);
                scaleAnimation.setFillAfter(true);
                petsLayout.startAnimation(scaleAnimation);
                // set selected
                //set pet fragment
                selectTab = 2;

            }
        });
    }
    private void aboutLayout(AboutFragment aboutFragment, NotFFoundFragment notFFoundFragment, List<Device> deviceList){
        aboutLayout.setOnClickListener(v -> {
            if(selectTab != 3){
                if(deviceList.isEmpty()){
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragmentContainer, notFFoundFragment,null)
                            .commit();
                }else{
                    MyViewModel viewModel = new ViewModelProvider(this).get(MyViewModel.class);

                    viewModel.setDevices(deviceList);

                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragmentContainer, aboutFragment,null)
                            .commit();
                }


                petText.setVisibility(View.GONE);
                homeText.setVisibility(View.GONE);
                settingText.setVisibility(View.GONE);

                petIMG.setImageResource(R.drawable.ic_tracking);
                settingsIMG.setImageResource(R.drawable.ic_settings);
                homeIMG.setImageResource(R.drawable.ic_home);

                petsLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.trans));
                settingsLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.trans));
                homeLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.trans));
                // select home tab
                aBoutText.setVisibility(View.VISIBLE);
                aboutIMG.setImageResource(R.drawable.ic_list_selected);
                aboutLayout.setBackgroundResource(R.drawable.round_back_todo);

                // create animation
                ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f,1.0f,1f,1f, Animation.RELATIVE_TO_SELF,1.0f,Animation.RELATIVE_TO_SELF,0.0f);
                scaleAnimation.setDuration(500);
                scaleAnimation.setFillAfter(true);
                aboutLayout.startAnimation(scaleAnimation);
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
                homeText.setVisibility(View.GONE);
                aBoutText.setVisibility(View.GONE);

                petIMG.setImageResource(R.drawable.ic_tracking);
                aboutIMG.setImageResource(R.drawable.ic_list);
                homeIMG.setImageResource(R.drawable.ic_home);

                petsLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.trans));
                aboutLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.trans));
                homeLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.trans));
                // select home tab
                settingText.setVisibility(View.VISIBLE);
                settingsIMG.setImageResource(R.drawable.ic_settings_selected);
                settingsLayout.setBackgroundResource(R.drawable.round_back_todo);

                // create animation
                ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f,1.0f,1f,1f, Animation.RELATIVE_TO_SELF,1.0f,Animation.RELATIVE_TO_SELF,0.0f);
                scaleAnimation.setDuration(500);
                scaleAnimation.setFillAfter(true);
                settingsLayout.startAnimation(scaleAnimation);
                // set selected
                selectTab = 4;

            }
        });
    }



    private void setItemMenu(){

        mqttAndroidClient = new MqttAndroidClient(this,BROKER_URL,CLIENT_ID);
        mqttConnectOptions= new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setUserName("admin24");
        mqttConnectOptions.setPassword("admin24".toCharArray());
        try {
            IMqttToken token = mqttAndroidClient.connect(mqttConnectOptions);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Connected");

//                    mqttSub(mqttAndroidClient,new String[]{"device01,device02,device03"},new int[]{1,1,1});
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(TAG, "Failed to connect");

                }
            });

        }catch (MqttException e){
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }

//        Intent intent = getIntent();
//
//        Log.d(TAG, "Intent: "+intent.getStringExtra("username"));
//
//        String userName = intent.getStringExtra("username");
//
//        SettingFragment settingFragment = SettingFragment.newInstance(userName);
//        HomeFragment homeFragment = HomeFragment.newInstance(userName);
//        viewModel = new ViewModelProvider(this).get(MqttViewModel.class);
//        viewModel.setMqttData(mqttAndroidClient);
//        mapFragment = MapFragment.newInstance(userName);
//        AboutFragment aboutFragment = new AboutFragment();
//        NotFFoundFragment notFFoundFragment = new NotFFoundFragment();
//        // set home default
//        getSupportFragmentManager().beginTransaction()
//                .setReorderingAllowed(true)
//                .replace(R.id.fragmentContainer, homeFragment,null)
//                .commit();
//        runOnUiThread(() ->{
//            checkDevice(userName,mapFragment,notFFoundFragment);
//            homeLayout(homeFragment);
////            petLayout(mapFragment,notFFoundFragment,devices);
//            settingsLayout(settingFragment);
//            aboutLayout(aboutFragment);
//        });
        new Handler().postDelayed(()->{
            if(mqttAndroidClient.isConnected()){
                functionSetUp();
            }
        },2000);

    }
    private void functionSetUp(){
        Intent intent = getIntent();

        Log.d(TAG, "Intent: "+intent.getStringExtra("username"));

        String userName = intent.getStringExtra("username");

        SettingFragment settingFragment = SettingFragment.newInstance(userName);
        HomeFragment homeFragment = HomeFragment.newInstance(userName);
        viewModel = new ViewModelProvider(this).get(MqttViewModel.class);
        viewModel.setMqttData(mqttAndroidClient);
        mapFragment = MapFragment.newInstance(userName);
        AboutFragment aboutFragment = AboutFragment.newInstance(userName);
        NotFFoundFragment notFFoundFragment = new NotFFoundFragment();
        // set home default
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainer, homeFragment,null)
                .commit();
        runOnUiThread(() -> {
            checkDevice(userName, mapFragment, notFFoundFragment,aboutFragment);
            homeLayout(homeFragment);
//            petLayout(mapFragment,notFFoundFragment,devices);
            settingsLayout(settingFragment);
//            aboutLayout(aboutFragment);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {

            switch (resultCode) {
                case Activity.RESULT_OK:
                    Toast.makeText(this, "GPS is turned on", Toast.LENGTH_SHORT).show();

                case Activity.RESULT_CANCELED:
                    Toast.makeText(this, "GPS required to be turned on", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
