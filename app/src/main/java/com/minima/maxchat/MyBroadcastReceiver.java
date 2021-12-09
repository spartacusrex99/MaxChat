package com.minima.maxchat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class MyBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "MyBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("Action: " + intent.getAction() + "\n");
//        sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
//        String log = sb.toString();
//        Log.d(TAG, log);
//        String data = intent.getExtras().getString("data");
        Toast.makeText(context, "MAXIMA BR!!", Toast.LENGTH_LONG).show();

        String CHANNEL_ID = "com.minima.maxchat";

        // Create our notification channel here & add channel to Manager's list
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Maxima Node Foreground Service Channel",
                NotificationManager.IMPORTANCE_LOW
        );

        NotificationManager mNotificationManager = context.getSystemService(NotificationManager.class);
        mNotificationManager.createNotificationChannel(serviceChannel);

        Intent NotificationIntent = new Intent(context, MainActivity.class);
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, 0 , NotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification mNotification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Title")
                .setAutoCancel(true)
                .setContentText("Maxima")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(mPendingIntent)
                .build();

        mNotificationManager.notify(1,mNotification);
    }
}
