package com.petweio.projectdoan.Notification;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.petweio.projectdoan.R;
public class MyApplication extends Application {

    private static final String TAG = MyApplication.class.getSimpleName();
    public static final String CHANNEL_ID = "Channel_service_pet_id";
    public static final String CHANNEL_Name = "Channel_service_pet_name";

    MyAppsNotificationManager  myAppsNotificationManager;
    private String fcmToken;
    @Override
    public void onCreate() {
        super.onCreate();
        createChannelNotification();

        myAppsNotificationManager = MyAppsNotificationManager.getInstance(this);
        myAppsNotificationManager.registerNotificationChannelChannel(
                getString(R.string.NEWS_CHANNEL_ID),
                getString(R.string.CHANNEL_NEWS),
                getString(R.string.CHANNEL_DESCRIPTION));
        myAppsNotificationManager.registerNotificationChannelChannel(
                getString(R.string.CHANNEL_DISTANCE_ALERT_ID),
                getString(R.string.CHANNEL_DISTANCE_ALERT_NAME),
                getString(R.string.CHANNEL_DISTANCE_DESCRIPTION));
        myAppsNotificationManager.registerNotificationChannelChannel(
                getString(R.string.CHANNEL_STATUS_ALERT_ID),
                getString(R.string.CHANNEL_STATUS_ALERT_NAME),
                getString(R.string.CHANNEL_STATUS_DESCRIPTION));
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String token = task.getResult();
                        // Do something with the FCM token
                        Log.i(getString(R.string.DEBUG_TAG), "The result: "+token);
                        fcmToken = task.getResult();
                    } else {
                        // Handle the error
                        Log.i(getString(R.string.DEBUG_TAG), "Task Failed");

                    }
                });


    }

    public String getFcmToken() {
        return fcmToken;
    }
    private void createChannelNotification() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_Name, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
        NotificationChannel distanceChanel = new NotificationChannel(getString(R.string.CHANNEL_DISTANCE_ALERT_ID),getString(R.string.CHANNEL_DISTANCE_ALERT_NAME), NotificationManager.IMPORTANCE_HIGH);
        manager.createNotificationChannel(distanceChanel);
        NotificationChannel statusChannel = new NotificationChannel(getString(R.string.CHANNEL_STATUS_ALERT_ID),getString(R.string.CHANNEL_STATUS_ALERT_NAME), NotificationManager.IMPORTANCE_HIGH);
        manager.createNotificationChannel(statusChannel);

    }
    public void triggerNotification(Class<?> targetNotificationActivity, String channelId, String title, String text, String bigText, int priority, boolean autoCancel, int notificationId, int pendingIntentFlag){
        myAppsNotificationManager.triggerNotification(targetNotificationActivity,channelId,title,text, bigText, priority, autoCancel,notificationId, pendingIntentFlag);
    }

//    public void triggerNotification_test(Class<?> targetNotificationActivity, String channelId, String title, String text, String bigText, int priority, boolean autoCancel, int notificationId){
//        myAppsNotificationManager.triggerNotification_test(targetNotificationActivity,channelId,title,text, bigText, priority, autoCancel,notificationId);
//    }

    public void triggerNotificationWithBackStack(Class<?> targetNotificationActivity, String channelId, String title, String text, String bigText, int priority, boolean autoCancel, int notificationId, int pendingIntentFlag){
        myAppsNotificationManager.triggerNotificationWithBackStack(targetNotificationActivity,channelId,title,text, bigText, priority, autoCancel,notificationId, pendingIntentFlag);
    }

    public void updateNotification(Class<?> targetNotificationActivity,String title,String text, String channelId, int notificationId, String bigpictureString, int pendingIntentflag){
        myAppsNotificationManager.updateWithPicture(targetNotificationActivity, title, text, channelId, notificationId, bigpictureString, pendingIntentflag);
    }

    public void cancelNotification(int notificationId){
        myAppsNotificationManager.cancelNotification(notificationId);
    }



}