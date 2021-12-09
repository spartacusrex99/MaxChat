package com.minima.maxchat;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.Toast;

public class ReceiveService  extends Service {

    private static final String ACTION="com.minima.myapplication.MINIMA_MESSAGE";

    private BroadcastReceiver yourReceiver;

    public static final String CHANNEL_ID = "MaximaServiceChannel";

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Toast.makeText(this, "Maxima service onCreate", Toast.LENGTH_SHORT).show();
        System.out.println("MAXCHAT service onCreate");

        final IntentFilter theFilter = new IntentFilter();
        theFilter.addAction(ACTION);
        this.yourReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // Do whatever you need it to do when it receives the broadcast
                // Example show a Toast message...
                showSuccessfulBroadcast();
            }
        };
        // Registers the receiver so that your service will listen for
        // broadcasts
        this.registerReceiver(this.yourReceiver, theFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Maxima service onStartCommand", Toast.LENGTH_SHORT).show();
        System.out.println("MAXCHAT service onStartCommand");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification =
                new Notification.Builder(this, "MaximaNotification")
                        .setContentTitle("MAXCHAT")
                        .setContentText("TEXT")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentIntent(pendingIntent)
                        .setTicker("TICKER")
                        .build();

        // Notification ID cannot be 0.
        startForeground(99, notification);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Maxima service onDestroy", Toast.LENGTH_SHORT).show();
        System.out.println("MAXCHAT service onDestroy");

        // Do not forget to unregister the receiver!!!
        this.unregisterReceiver(this.yourReceiver);
    }

    private void showSuccessfulBroadcast() {
        Toast.makeText(this, "Broadcast Successful!!!", Toast.LENGTH_LONG).show();
        System.out.println("MAXCHAT Broadcast Successful!!");
    }
}