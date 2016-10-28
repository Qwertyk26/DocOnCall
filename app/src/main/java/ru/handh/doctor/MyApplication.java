package ru.handh.doctor;

import android.app.Application;
import android.content.Context;
import android.os.Environment;



/**
 * Created by sgirn on 06.10.2015.
 */
public class MyApplication extends Application {
    public static boolean isSessionStateReceived = false;
    public static boolean isSessionOpened = false;

    public static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }
}
