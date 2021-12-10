package com.minima.maxchat.db;

import com.minima.maxchat.utils.objects.MiniData;

import io.realm.RealmObject;

public class ChatRoom extends RealmObject {

    private String RandomID = MiniData.getRandomData(32).to0xString();
    private String name;
    private String user;

    public String getRandomID() {
        return RandomID;
    }

    public void setRandomID(String randomID) {
        RandomID = randomID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}

