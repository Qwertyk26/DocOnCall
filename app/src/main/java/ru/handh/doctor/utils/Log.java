package ru.handh.doctor.utils;


import ru.handh.doctor.BuildConfig;

/**
 * Created by sergey on 08.12.15.
 * для логирования
 */
public class Log {

    public static void d(String message) {
        if (BuildConfig.DOCDEBUG)
            android.util.Log.d("DOC", message);
    }

    public static void d(String title, String message) {
        if (BuildConfig.DOCDEBUG)
            android.util.Log.d(title, message);
    }

    public static void d(String title, String message, Throwable e) {
        if (BuildConfig.DOCDEBUG)
            android.util.Log.d(title, message, e);
    }

    public static void e(String message) {
        if (BuildConfig.DOCDEBUG)
            android.util.Log.d("DOC", message);
    }
}
