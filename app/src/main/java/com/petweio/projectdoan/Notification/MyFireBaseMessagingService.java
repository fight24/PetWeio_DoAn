package com.petweio.projectdoan.Notification;


import android.app.PendingIntent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.petweio.projectdoan.R;
import com.petweio.projectdoan.splash.SplashActivity;

import java.util.Objects;

public class MyFireBaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFireBaseMessagingService.class.getSimpleName();
    private static boolean isServiceRunning = false;
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.i(getString(R.string.DEBUG_TAG), "New Token: "+s);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i(getString(R.string.DEBUG_TAG),"Message received");

        ((MyApplication)getApplication()).triggerNotification(SplashActivity.class,
                getString(R.string.NEWS_CHANNEL_ID),
                Objects.requireNonNull(remoteMessage.getNotification()).getTitle(),
                remoteMessage.getNotification().getBody(),
                remoteMessage.getNotification().getBody(),
                NotificationCompat.PRIORITY_HIGH,
                true,
                getResources().getInteger(R.integer.notificationId),
                PendingIntent.FLAG_UPDATE_CURRENT);
//                ((MyApplication)getApplication()).triggerNotificationWithBackStack(SplashActivity.class,
//            getString(R.string.NEWS_CHANNEL_ID),
//            Objects.requireNonNull(remoteMessage.getNotification()).getTitle(),
//            remoteMessage.getNotification().getBody(),
//                remoteMessage.getNotification().getBody(),
//                NotificationCompat.PRIORITY_HIGH,
//            true,
//            getResources().getInteger(R.integer.notificationId),
//                PendingIntent.FLAG_UPDATE_CURRENT);

    }


    @Override
    public void onCreate() {
        super.onCreate();
        isServiceRunning = true;
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceRunning = false;
        Log.d(TAG,"onDestroy");
    }

    public static boolean isServiceRunning() {
        return isServiceRunning;
    }


}