package com.minima.maxchat.db;

import com.minima.maxchat.utils.objects.MiniData;

import io.realm.RealmObject;

public class ChatRoom extends RealmObject {

    private String RandomID = MiniData.getRandomData(32).to0xString();
    private String Name;
    private String User;

    public String getRandomID() {
        return RandomID;
    }

    public void setRandomID(String randomID) {
        RandomID = randomID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

}

