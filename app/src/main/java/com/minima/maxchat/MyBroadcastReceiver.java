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

        String CHANNEL_ID = "com.minima.myapplication";

        // Create our notification channel here & add channel to Manager's list
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Minima Node Foreground Service Channel",
                NotificationManager.IMPORTANCE_LOW
        );

        NotificationManager mNotificationManager = context.getSystemService(NotificationManager.class);
        mNotificationManager.createNotificationChannel(serviceChannel);

        Intent NotificationIntent = new Intent(context, MainActivity.class);
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, 0 , NotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification mNotification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Title")
                .setAutoCancel(true)
                .setContentText("Maxima Status Channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(mPendingIntent)
                .build();

        mNotificationManager.notify(1,mNotification);

//        String NOTIFICATION_CHANNEL_ID = "com.minima.myapplication";
//        CharSequence name =  "My Background Service";
//        String description =  "My Background Service";
//        int importance = NotificationManager.IMPORTANCE_DEFAULT;
//
//        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
//        channel.setDescription(description);
//
//        // Register the channel with the system; you can't change the importance
//        // or other notification behaviors after this
//        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
//        notificationManager.createNotificationChannel(channel);
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
//
//        Notification  notification  = notificationBuilder.setOngoing(true)
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setContentTitle("Service running")
//                .setContentText("new")
//                .setPriority(NotificationManager.IMPORTANCE_MIN)
//                .setCategory(Notification.CATEGORY_SERVICE)
//                .build();
//
////        NotificationManagerCompat notificationManagerq =
////                NotificationManagerCompat.from(context);
//
//        // notificationId is a unique int for each notification that you must define
//        notificationManager.notify(1, notificationBuilder.build());

    }
}
