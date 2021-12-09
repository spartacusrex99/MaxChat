package com.minima.maxchat;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.minima.maxchat.db.ChatRoom;
import com.minima.maxchat.db.MaxMessage;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class ChatStream extends AppCompatActivity {

    Realm mRealm;

    RealmChangeListener<Realm> mChangeListener;

    TextView mChatView;

    EditText mInput;

    ChatRoom mChatRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chatter);

        mRealm = Realm.getDefaultInstance(); // opens "myrealm.realm"
        mChangeListener = new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm realm) {
                System.out.println("MAX CHANGE");
                //Redo the listview..
                updateChatText();
            }
        };

        Bundle extras   = getIntent().getExtras();
        String roomid   = extras.getString("roomid");

        //Get this room..
        RealmResults<ChatRoom> allrooms = mRealm.where(ChatRoom.class).equalTo("RandomID",roomid).findAll();
        if(allrooms.size()==0){
            Toast.makeText(this, "ERROR Room does not exist", Toast.LENGTH_SHORT).show();
            return;
        }
        mChatRoom  = allrooms.get(0);

        //First - before we add the change listener.. set to read
        RealmResults<MaxMessage> allmessages =
                mRealm.where(MaxMessage.class).equalTo("roomid",roomid).findAll();

        mRealm.beginTransaction();
        for(MaxMessage msg : allmessages){
            msg.setUnread(false);
        }
        mRealm.commitTransaction();

        mChatView = findViewById(R.id.chatter_window);
        mChatView.setMovementMethod(new ScrollingMovementMethod());

        mInput = findViewById(R.id.chatter_input);
        mInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                //Send a message!..
                String text = mInput.getText().toString();
                mInput.setText("");

                MainActivity.newMessage(false, mChatRoom.getRandomID(), mChatRoom.getName(),"You",text, false);

                return false;
            }
        });

        setTitle(mChatRoom.getName());
    }

    public void updateChatText(){

        RealmResults<MaxMessage> allmessages =
                mRealm.where(MaxMessage.class)
                        .equalTo("roomid",mChatRoom.getRandomID())
                        .findAll()
                        .sort("timemilli", Sort.ASCENDING);

        StringBuffer fulltext = new StringBuffer();

        for(MaxMessage mm : allmessages){
            fulltext.append("<b>"+mm.getFrom()+"</b> : "+mm.getMessage()+"<br><br>");
        }

        Spanned html = Html.fromHtml(fulltext.toString(), Html.FROM_HTML_MODE_LEGACY);
        mChatView.setText(html);
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
        updateChatText();
    }

    @Override
    public void onStop(){
        super.onStop();
        mRealm.removeChangeListener(mChangeListener);
    }

}
