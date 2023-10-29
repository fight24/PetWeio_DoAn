package com.petweio.projectdoan.service;

import static com.petweio.projectdoan.Notification.MyApplication.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.petweio.projectdoan.R;
import com.petweio.projectdoan.Sign.LoginActivity;

public class ForeGroundService extends Service {
    String data;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public ForeGroundService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        data = intent.getStringExtra("key_data_value");
        sendNotification(data);
        return super.onStartCommand(intent, flags, startId);
    }
    private void sendNotification(String data) {
        if(data != null){
            Intent intent = new Intent(this, LoginActivity.class); // xay dung in tntent khi click thong bao no nhay vao
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                    .setContentTitle("Warning of device is starting")
                    .setContentText(data)
                    .setSmallIcon(R.drawable.notifications_color)
                    .setContentIntent(pendingIntent)
                    .build();
            startForeground(1,notification);
        }

    }
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
