package com.minima.maxchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.minima.maxchat.db.MaxMessage;

import java.util.ArrayList;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {

    Realm mRealm;

    ListView mListView;

    RealmChangeListener<Realm> mChangeListener;

    MaxAdapter mMaxAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRealm = Realm.getDefaultInstance(); // opens "myrealm.realm"

        mChangeListener = new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm realm) {
                //Redo the listview..
                updateListView();
            }
        };

//        //Wipe it..
//        mRealm.beginTransaction();
//        mRealm.where(MaxMessage.class).findAll().deleteAllFromRealm();
//        mRealm.commitTransaction();

        mListView = (ListView)findViewById(R.id.messagelist);

        //Listen for clicks
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int zPosition, long l) {
                MaxMessage mm = mMaxAdapter.getItem(zPosition);

                //Now open her up..
                Intent chatter = new Intent(MainActivity.this, ChatStream.class);
                chatter.putExtra("name",mm.getChatroom());
                startActivity(chatter);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random rand = new Random();
                String chatroom = "Paddy "+rand.nextInt(5);
                String message = "Hello ! "+rand.nextInt(1000);
                newMessage(true, chatroom,chatroom,message, true);

                //Braodcast an Intent..
//                Intent i= new Intent( MainActivity.this, MyBroadcastReceiver.class);
//                sendBroadcast(i);
            }
        });

    }

    public void updateListView(){
        RealmResults<MaxMessage> allmessages = mRealm.where(MaxMessage.class).findAll().sort("timemilli", Sort.DESCENDING);

        ArrayList<MaxMessage> shower = new ArrayList<>();

        //Now sort them..
        ArrayList<String> usednames = new ArrayList<>();
        for(MaxMessage mm : allmessages){
            String chatroom = mm.getChatroom();

            //Get the name..
            if(!usednames.contains(chatroom)){
                usednames.add(chatroom);
                shower.add(mm);
            }
        }

        mMaxAdapter = new MaxAdapter(shower,this);

        mListView.setAdapter(mMaxAdapter);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public void onStart(){
        super.onStart();
        mRealm.addChangeListener(mChangeListener);
        updateListView();
    }

    @Override
    public void onStop(){
        super.onStop();
        mRealm.removeChangeListener(mChangeListener);
    }

    public static void newMessage(boolean zSeparateThread, String zChatRoom, String zFrom, String zMessage, boolean zUnread){
        // Run a non-Looper thread with a Realm instance.
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Realm realm = Realm.getDefaultInstance();
                try {
                    MaxMessage mm = new MaxMessage();
                    mm.setChatroom(zChatRoom);
                    mm.setFrom(zFrom);
                    mm.setMessage(zMessage);
                    mm.setUnread(zUnread);

                    realm.beginTransaction();
                    realm.copyToRealm(mm);
                    realm.commitTransaction();

//                    // on below line we are calling a method to execute a transaction.
//                    realm.executeTransaction(new Realm.Transaction() {
//                        @Override
//                        public void execute(Realm realm) {
//                            // inside on execute method we are calling a method
//                            // to copy to real m database from our modal class.
//                            realm.copyToRealm(mm);
//                        }
//                    });

                } finally {
                    realm.close();
                }
            }
        });

        if(zSeparateThread){
            thread.start();
        }else{
            thread.run();
        }

    }
}