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
//        Parse.initialize(this, "afadf20e-7719-44f7-9f04-bc320011562a", "2p2cbvs9Rt27AcUyjC4NoPWieKZT9tsn");
        Parse.initialize(new Parse.Configuration.Builder(this)
                        .applicationId("afadf20e-7719-44f7-9f04-bc320011562a")
                        .clientKey("2p2cbvs9Rt27AcUyjC4NoPWieKZT9tsn")
                        .server("https://api.parse.buddy.com/parse/")
                        .enableLocalDataStore()
                        .build()
        );
        ParseUser.enableAutomaticUser();

        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true);

    }
}
