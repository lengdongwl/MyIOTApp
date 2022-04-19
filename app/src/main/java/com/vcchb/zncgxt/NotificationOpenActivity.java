package com.vcchb.zncgxt;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;

public class NotificationOpenActivity extends AppCompatActivity {
    public PendingIntent pendingIntentGet;
    public NotificationManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        while(true){
            if (MainActivity.Activitystate == 1) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Notification notificationGet = new NotificationCompat.Builder(this, "subscribe")
                        .setAutoCancel(true)
                        .setContentTitle("收到报警信息")
                        .setContentText("")
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setContentIntent(pendingIntentGet)
                        .build();
                manager.notify(2, notificationGet);
                //startActivity(MainActivity.intent1);
//                Intent intent1 = new Intent(NotificationOpenActivity.this, MainActivity.class);
                MainActivity.Activitystate=2;
                finish();
            }
        }
    }
}
