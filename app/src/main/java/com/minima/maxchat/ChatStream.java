package com.minima.maxchat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.Layout;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.minima.maxchat.db.ChatRoom;
import com.minima.maxchat.db.MaxMessage;
import com.minima.maxchat.utils.RPCClient;
import com.minima.maxchat.utils.json.JSONObject;
import com.minima.maxchat.utils.objects.MiniData;
import com.minima.maxchat.utils.objects.MiniString;

import java.io.IOException;

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

    String mToUser;

    String mChatName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chatter);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MaximaPrefs", 0); // 0 - for private mode
        mChatName = pref.getString("chatname","noname");

        mRealm = Realm.getDefaultInstance(); // opens "myrealm.realm"
        mChangeListener = new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm realm) {
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
        mToUser    = mChatRoom.getUser();

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

                MainActivity.newMessage(false, mChatRoom.getRandomID(), mChatRoom.getName(),mChatName,text, false);

                //And send it!
                runMaximaCommand(text);

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

        final Layout layout = mChatView.getLayout();
        if(layout != null){
            int scrollDelta = layout.getLineBottom(mChatView.getLineCount() - 1)
                    - mChatView.getScrollY() - mChatView.getHeight();
            if(scrollDelta > 0)
                mChatView.scrollBy(0, scrollDelta);
        }
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

    public void runMaximaCommand(String zMessage){

        JSONObject maxjson = new JSONObject();
        maxjson.put("user",mChatName);
        maxjson.put("message",zMessage);

        MiniString text = new MiniString(maxjson.toString());
        String data     = new MiniData(text.getData()).to0xString();
        String fullcommand = "maxima+function:send+to:"+mToUser+"+application:maxchat+data:"+data;
        runCommand(fullcommand);
    }

    public static void runCommand(String zCommand){

        Runnable rr = new Runnable() {
            @Override
            public void run() {

                try {
                    String result = RPCClient.sendGET("http://127.0.0.1:9002/"+zCommand);

                    System.out.println(result);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread tt = new Thread(rr);
        tt.start();
    }

    @Override
    protected void onResume(){
        super.onResume();
        ((MyApplication)getApplication()).isMainInForeground = true;
    }

    @Override
    protected void onPause(){
        super.onPause();
        ((MyApplication)getApplication()).isMainInForeground = false;
    }
}
