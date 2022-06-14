package com.eliot.bluetoothlowenergielibrary.App;

import android.app.Application;
import android.content.Context;

public class App extends Application
{
    private static Context applicationContext;

    public static Context getContext()
    {
        return applicationContext;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        applicationContext = getApplicationContext();
    }
}
