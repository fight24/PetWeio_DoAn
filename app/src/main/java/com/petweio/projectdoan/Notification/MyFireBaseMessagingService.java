package com.petweio.projectdoan.Notification;


import android.app.PendingIntent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.petweio.projectdoan.MyAppCompatActivity;
import com.petweio.projectdoan.R;

import java.util.Objects;

public class MyFireBaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFireBaseMessagingService.class.getSimpleName();

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.i(getString(R.string.DEBUG_TAG), "New Token: "+s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i(getString(R.string.DEBUG_TAG),"Message received");
        ((MyApplication)getApplication()).triggerNotificationWithBackStack(MyAppCompatActivity.class,
            getString(R.string.NEWS_CHANNEL_ID),
            Objects.requireNonNull(remoteMessage.getNotification()).getTitle(),
            remoteMessage.getNotification().getBody(),
            "This notification is from FCM console",
                NotificationCompat.PRIORITY_HIGH,
            true,
            getResources().getInteger(R.integer.notificationId),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}