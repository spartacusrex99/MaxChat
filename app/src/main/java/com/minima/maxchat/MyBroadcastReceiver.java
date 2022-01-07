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

import com.minima.maxchat.db.ChatRoom;
import com.minima.maxchat.db.MaxMessage;
import com.minima.maxchat.utils.MinimaLogger;
import com.minima.maxchat.utils.json.parser.JSONParser;
import com.minima.maxchat.utils.json.parser.ParseException;
import com.minima.maxchat.utils.objects.MiniData;
import com.minima.maxchat.utils.json.*;
import com.minima.maxchat.utils.objects.MiniString;

import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;

public class MyBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "MyBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        //Get the data
        String data = intent.getExtras().getString("data");

        //Parse the JSONOBject
        JSONObject json = null;
        try {
            json = (JSONObject) new JSONParser().parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Is this a max chat message
        String application = (String)json.get("application");
        if(!application.equals("maxchat")){
            //Not a maxchat message
            return;
        }

        String from      = (String)json.get("from");
        MiniData msgdata = new MiniData((String)json.get("data"));
        String strmsg    = new MiniString(msgdata.getBytes()).toString();

        String username = "Them";
        String sentmsg  = strmsg;
        if(strmsg.startsWith("{")){
            //It's a JSON
            try {
                JSONObject jsonmsg = (JSONObject) new JSONParser().parse(strmsg);

                username = (String)jsonmsg.get("user");
                sentmsg  = (String)jsonmsg.get("message");

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(username == null){
            username = "Them";
        }
        if(sentmsg == null){
            sentmsg = "";
        }


        //The chat room
        ChatRoom cr = null;

        String crname = "";
        String crchat = "";

        //Find the chatroom..
        Realm realm = Realm.getDefaultInstance();

        try {

            RealmResults<ChatRoom> allrooms = realm.where(ChatRoom.class).equalTo("user",from).findAll();
            if(allrooms.size()==0){
                //It's a new room..
                //Toast.makeText(context, "New Room!", Toast.LENGTH_SHORT).show();

                cr = new ChatRoom();
                if(username.equals("Them")){
                    cr.setName("ROOM "+new Random().nextInt(1000));
                }else{
                    cr.setName(username);
                }

                cr.setUser(from);

                realm.beginTransaction();
                    realm.copyToRealm(cr);
                realm.commitTransaction();

                //Add a message
                newMessage(realm,cr.getRandomID(),cr.getName(),from ," contacted you..",true);

            }else {
                cr = allrooms.get(0);
            }

            //Add a message
            newMessage(realm,cr.getRandomID(),cr.getName(),username ,sentmsg,true);

            crname = new String(cr.getName());
            crchat = new String(strmsg);

        } finally {
            realm.close();
        }

        if(((MyApplication)context.getApplicationContext()).isForeground()){
            //Toast.makeText(context, from+" sends "+msg, Toast.LENGTH_LONG).show();
            return;
        }

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
                .setContentTitle(username+" : "+sentmsg)
                .setAutoCancel(true)
                .setContentText("MaxChat")
                .setSmallIcon(android.R.drawable.sym_action_chat)
                .setContentIntent(mPendingIntent)
                .build();

        mNotificationManager.notify(1,mNotification);
    }

    public static void newMessage(Realm zRealm, String zRoomID, String zChatRoom, String zFrom, String zMessage, boolean zUnread){

        MaxMessage mm = new MaxMessage();
        mm.setRoomid(zRoomID);
        mm.setChatroom(zChatRoom);
        mm.setFrom(zFrom);
        mm.setMessage(zMessage);
        mm.setUnread(zUnread);

        zRealm.beginTransaction();
        zRealm.copyToRealm(mm);
        zRealm.commitTransaction();
    }

}
