package com.genesis.cloudcarepatient;

import android.app.Application;
import android.content.Context;

/**
 * App class for getting context
 */
public class App extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        // TODO: 23.02.2016 comment when developing is over
        //Stetho.initializeWithDefaults(this);

        App.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return App.context;
    }
}