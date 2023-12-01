package com.petweio.projectdoan.service;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.petweio.projectdoan.Notification.MyApplication;
import com.petweio.projectdoan.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.android.service.MqttService;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.List;

public class LocationService extends Service {
    private static final String CHANNEL_ID = "location_service";
    private MqttAndroidClient mqttClient;
    private String topic ;
    String message ;
    List<String> distancesList = new ArrayList<>();
    private static final String TAG = "LocationService";
    private LocationEngineCallback<LocationEngineResult> locationCallback;

    @Override
    public void onCreate() {
        super.onCreate();
        String serverUri = "tcp://petweioapp.online:1883";
        String clientId = MqttAsyncClient.generateClientId();
        Log.d(TAG, "on create: ");
        mqttClient = new MqttAndroidClient(this, serverUri, clientId, new MemoryPersistence());
//        MqttViewModel viewModel = new ViewModelProvider(BottomNavActivity.this).get(MqttViewModel.class);
//        mqttClient = viewModel.getMqttClient();
        topic = "user/"+((MyApplication) getApplication()).getFcmToken();

    }

    // Phương thức này sẽ tạo thông báo cho dịch vụ Foreground
    @NonNull
    private Notification createNotification() {
        // Tạo một Notification cho dịch vụ
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("User Location Service")
                .setContentText("Share location of user is running")
                .setSmallIcon(R.drawable.notifications_color);

        // Đảm bảo tạo một kênh thông báo trước khi sử dụng
        createNotificationChannel();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName("admin24");
        options.setPassword("admin24".toCharArray());
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);
        Thread backgroundThread = new Thread(() -> {
            try {
                IMqttToken token = mqttClient.connect(options);
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // Connection successful
                        Log.d(TAG, "Connection successful");

                        locationCallback = new LocationEngineCallback<LocationEngineResult>() {
                            @Override
                            public void onSuccess(LocationEngineResult result) {
                                Location location = result.getLastLocation();
                                if (location != null) {
                                    // Đây là nơi bạn có thể xử lý dữ liệu vị trí, ví dụ: publishLocationToMQTT(location)
                                    message = location.getLatitude() + "," + location.getLongitude();
                                    Log.e(TAG, message);
                                    MqttMessage mqttMessage = new MqttMessage();
                                    mqttMessage.setPayload(message.getBytes());
                                    try {
                                        mqttClient.publish(topic, mqttMessage);
                                    } catch (MqttException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }

                        };
                        updateLocation(locationCallback);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        // Connection failed
                        Log.d(TAG, "Connection failed");
                    }
                });

            } catch (MqttException e) {
                e.printStackTrace();
            }

        });
        backgroundThread.start();


        return builder.build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start the MQTT connection and subscribe to MQTT topics.
        // Your data sending logic can be placed here.


        startForeground(2, createNotification());
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    // ... other service methods

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationCallback != null){
            locationCallback = null;
        }
        Log.d(TAG, "onDestroy");
        mqttClient.unregisterResources();
        Intent intent = new Intent(this, MqttService.class);
        stopService(intent);

    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "MQTT Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

    private void updateLocation(LocationEngineCallback<LocationEngineResult> locationCallback) {
        LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(this);
        LocationEngineRequest locationRequest = new LocationEngineRequest.Builder(5000) // Đặt thời gian cập nhật vị trí (1000ms = 1 giây)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(2500) // Thời gian tối thiểu giữa các cập nhật
                .build();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationEngine.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
    }



}