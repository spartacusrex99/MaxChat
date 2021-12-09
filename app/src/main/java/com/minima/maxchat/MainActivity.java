package com.minima.maxchat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.minima.maxchat.db.ChatRoom;
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

        mListView = (ListView) findViewById(R.id.messagelist);

        //Listen for clicks
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int zPosition, long l) {
                MaxMessage mm = mMaxAdapter.getItem(zPosition);

                //Now open her up..
                Intent chatter = new Intent(MainActivity.this, ChatStream.class);
                chatter.putExtra("name", mm.getChatroom());
                chatter.putExtra("roomid", mm.getRoomid());
                startActivity(chatter);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("New Chat Room");

                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.new_room, null);

                final EditText roomname = layout.findViewById(R.id.chatroom_name);
                final EditText roomuser = layout.findViewById(R.id.chatroom_user);

                builder.setView(layout);
                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String room  = roomname.getText().toString().trim();
                        String user  = roomuser.getText().toString().trim();

                        if(roomname.equals("") || user.equals("")){
                            Toast.makeText(MainActivity.this, "Cannot have blank fileds..", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Realm realm = Realm.getDefaultInstance();
                        ChatRoom cr = null;
                        try {
                            cr = new ChatRoom();
                            cr.setName(room);
                            cr.setUser(user);

                            realm.beginTransaction();
                                realm.copyToRealm(cr);
                            realm.commitTransaction();

                        } finally {
                            realm.close();
                        }

                        //Create a new room..
                        String chatroom = roomname.getText().toString();
                        newMessage(false, cr.getRandomID(), chatroom, "You", "Room created..", false);
                        newMessage(false, cr.getRandomID(), chatroom, "You", "Added "+user, false);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.profile:
                Intent profile = new Intent(this, ProfileActivity.class);
                startActivity(profile);
                break;

            case R.id.minimahelp:
                Toast.makeText(this,"HELP!",Toast.LENGTH_SHORT).show();
                break;
        }

        return true;
    }


    public void updateListView(){
        //Get all the messages
        RealmResults<MaxMessage> allmessages = mRealm.where(MaxMessage.class).findAll().sort("timemilli", Sort.DESCENDING);

        //Now sort them..
        ArrayList<String> usednames     = new ArrayList<>();
        ArrayList<MaxMessage> shower    = new ArrayList<>();
        for(MaxMessage mm : allmessages){
            String roomid = mm.getRoomid();

            //Get the name..
            if(!usednames.contains(roomid)){
                usednames.add(roomid);
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

    public static void newMessage(boolean zSeparateThread, String zRoomID, String zChatRoom, String zFrom, String zMessage, boolean zUnread){
        // Run a non-Looper thread with a Realm instance.
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Realm realm = Realm.getDefaultInstance();
                try {
                    MaxMessage mm = new MaxMessage();
                    mm.setRoomid(zRoomID);
                    mm.setChatroom(zChatRoom);
                    mm.setFrom(zFrom);
                    mm.setMessage(zMessage);
                    mm.setUnread(zUnread);

                    realm.beginTransaction();
                    realm.copyToRealm(mm);
                    realm.commitTransaction();
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