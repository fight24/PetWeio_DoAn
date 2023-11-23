package com.petweio.projectdoan.Notification;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.petweio.projectdoan.R;

import java.lang.ref.WeakReference;

class MyAppsNotificationManager {


    @SuppressLint("StaticFieldLeak")
    private static MyAppsNotificationManager instance;
    private final NotificationManagerCompat notificationManagerCompat;
    private final NotificationManager notificationManager;
    private final WeakReference<Context> contextRef;
    private final Context context;

    private MyAppsNotificationManager(Context context) {
        this.contextRef = new WeakReference<>(context);
        this.context = context;
        notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    }
    public String getPackageName() {
        return context.getPackageName();
    }
    public static MyAppsNotificationManager getInstance(Context context) {
        if (instance == null) {
            instance = new MyAppsNotificationManager(context);
        }
        return instance;
    }

    public void registerNotificationChannelChannel(String channelId, String channelName, String channelDescription) {
        Context context = contextRef.get();
        Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification);
        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription(channelDescription);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();
        notificationChannel.setSound(soundUri,audioAttributes);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);
    }

//    public void triggerNotification_test(Class<?> targetNotificationActivity, String channelId, String title, String text, String bigText, int priority, boolean autoCancel, int notificationId) {
//        Context context = contextRef.get();
//        Intent intent = new Intent(context, targetNotificationActivity);
//        intent.putExtra("count", title);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
//                .setSmallIcon(R.drawable.pet_we_io)
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.pet_we_io))
//                .setContentTitle(title)
//                .setContentText(text)
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(bigText))
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setContentIntent(pendingIntent)
//                .setChannelId(channelId)
//                .setAutoCancel(true);
//
//        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//
//            return;
//        }
//        notificationManagerCompat.notify(notificationId, builder.build());
//    }

    public void triggerNotification(Class<?> targetNotificationActivity, String channelId, String title, String text, String bigText, int priority, boolean autoCancel, int notificationId, int pendingIntentFlag) {
        Context context = contextRef.get();
        Intent intent = new Intent(context, targetNotificationActivity);
        intent.putExtra("count", title);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, pendingIntentFlag);
        Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification);
//        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.notifications_color)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.notifications_color))
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(bigText))
                .setPriority(priority)
                .setContentIntent(pendingIntent)
                .setChannelId(channelId)
                .setAutoCancel(autoCancel)
                .setSound(soundUri)
                .setColor(Color.parseColor("#2CA2FA"))
                .setVibrate(new long[]{ 500, 1000, 500});  // Thêm rung
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Log.d("NotificationProMax", "Sound Uri: " + soundUri);
        notificationManagerCompat.notify(notificationId, builder.build());
    }
    public void triggerNotificationWithBackStack(Class<?> targetNotificationActivity, String channelId, String title, String text, String bigText, int priority, boolean autoCancel, int notificationId, int pendingIntentFlag) {
        Context context = contextRef.get();
        Intent intent = new Intent(context, targetNotificationActivity);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntentWithParentStack(intent);
        intent.putExtra("count", title);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, pendingIntentFlag);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.notifications_color)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.notifications_color))
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(bigText))
                .setPriority(priority)
                .setAutoCancel(autoCancel)
                .setContentIntent(pendingIntent)
                .setChannelId(channelId)
                .setOngoing(true);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManagerCompat.notify(notificationId, builder.build());
    }

    public void updateWithPicture(Class<?> targetNotificationActivity,String title,String text, String channelId, int notificationId, String bigpictureString, int pendingIntentflag) {
        Context context = contextRef.get();
        Intent intent = new Intent(context, targetNotificationActivity);
        intent.putExtra("count", title);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, pendingIntentflag);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,channelId)
                .setSmallIcon(R.drawable.notifications_color)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.notifications_color))
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setChannelId(channelId)
                .setAutoCancel(true);

        Bitmap androidImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.images);
        builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(androidImage).setBigContentTitle(bigpictureString));
        notificationManager.notify(notificationId, builder.build());
    }

    public void cancelNotification(int notificationId){
        notificationManager.cancel(notificationId);
    }
}