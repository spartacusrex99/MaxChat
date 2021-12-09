package com.minima.maxchat.db;

import java.util.Date;

import io.realm.RealmObject;

public class MaxMessage extends RealmObject {

    private String roomid;
    private String chatroom;

    private String chatuser;

    private String from;
    private String message;
    private long timemilli = System.currentTimeMillis();
    private boolean unread = true;

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

    public String getChatuser() {
        return chatuser;
    }

    public void setChatuser(String chatuser) {
        this.chatuser = chatuser;
    }

    public String getChatroom() {
        return chatroom;
    }

    public void setChatroom(String chatroom) {
        this.chatroom = chatroom;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    public long getTimeMIlli(){
        return timemilli;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString(){
        return from+" : "+message+" @ "+new Date(timemilli).toString();
    }

}
