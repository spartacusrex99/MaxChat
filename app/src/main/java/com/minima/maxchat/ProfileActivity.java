package com.minima.maxchat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.minima.maxchat.utils.RPCClient;
import com.minima.maxchat.utils.json.JSONObject;
import com.minima.maxchat.utils.json.parser.JSONParser;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    EditText mInput;

    TextView mChatID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        setTitle("Profile");

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MaximaPrefs", 0); // 0 - for private mode
        String chatname = pref.getString("chatname","noname");

        mInput = findViewById(R.id.profile_chatname);
        mInput.setText(chatname);

        Button update = findViewById(R.id.profile_update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = mInput.getText().toString();

                SharedPreferences pref = getApplicationContext().getSharedPreferences("MaximaPrefs", 0); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("chatname",text);
                editor.commit();

                //And close it down..
                ProfileActivity.this.finish();
            }
        });

        mChatID = findViewById(R.id.chatter_id);
        getID();
    }

    public void getID(){

        Runnable rr = new Runnable() {
            @Override
            public void run() {

                try {
                    String result = RPCClient.sendGET("http://127.0.0.1:9002/maxima");
                    System.out.println(result);

                    JSONObject json     = (JSONObject) new JSONParser().parse(result);
                    JSONObject response =  (JSONObject)json.get("response");
                    String id = (String) response.get("identity");

                    ProfileActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mChatID.setText(id);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Thread tt = new Thread(rr);
        tt.start();
    }
}
