package com.minima.maxchat;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {

    public boolean isMainInForeground       = false;
    public boolean isChatInForeground       = false;
    public boolean isProfileInForeground    = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration.Builder()
                .allowWritesOnUiThread(true)
                .deleteRealmIfMigrationNeeded()
                .name("myrealm.realm").build();

        Realm.setDefaultConfiguration(config);
    }

    public boolean isForeground(){
        return isChatInForeground || isMainInForeground || isProfileInForeground;
    }


}
