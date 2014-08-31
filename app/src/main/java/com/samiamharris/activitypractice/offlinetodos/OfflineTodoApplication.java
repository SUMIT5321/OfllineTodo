package com.samiamharris.activitypractice.offlinetodos;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by SamMyxer on 8/29/14.
 */
public class OfflineTodoApplication extends Application {

    public static final String TODO_GROUP_NAME = "ALL_TODOS";

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Todo.class);
        Parse.enableLocalDatastore(getApplicationContext());
        Parse.initialize(this, "Eg3t3spfFsng1u5uBgZcaE7xP8yrAIObzm74koh9", "ESQ4xHZjHAMhK7JAolWAToVXJnN4zZD31OBCffr8");
        ParseUser.enableAutomaticUser();

        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true);

    }
}
