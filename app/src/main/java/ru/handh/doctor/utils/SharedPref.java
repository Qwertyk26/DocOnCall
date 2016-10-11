package ru.handh.doctor.utils;

import android.content.Context;
import android.content.SharedPreferences;

import ru.handh.doctor.model.CallDataForGeoService;

/**
 * Created by sgirn on 06.10.2015.
 * методы сохранения коротких
 */
public class SharedPref {

    private static Context Context;

    private static void setupContext(Context context)
    {
        if(context != null)
            Context = context;
    }

    public static void setIsFirstStartApp(boolean isStart, Context context) {
        setupContext(context);
        SharedPreferences sPref = Context.getSharedPreferences("isFirstStartApp", 1);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean("isStart", isStart);
        ed.apply();
    }

    public static boolean isFirstStartApp(Context context) {
        setupContext(context);
        SharedPreferences sPref = Context.getSharedPreferences("isFirstStartApp", 0);
        return sPref.getBoolean("isStart", true);
    }

    public static void setTokenApp(String token, Context context) {
        setupContext(context);
        SharedPreferences sPref = Context.getSharedPreferences("tokenApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("tokenApp", token);
        ed.apply();
    }
    public static void isNurse(boolean isNurse, Context context) {
        setupContext(context);
        SharedPreferences sPref = Context.getSharedPreferences("isNurse", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean("isNurse", isNurse);
        ed.apply();
    }
    public static void setAppVersion(String version, Context context) {
        setupContext(context);
        SharedPreferences sPref = Context.getSharedPreferences("appVersion", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("appVersion", version);
        ed.apply();
    }

    public static String getAppVersion(Context context) {
        setupContext(context);
        SharedPreferences sPref = Context.getSharedPreferences("appVersion", Context.MODE_PRIVATE);
        return sPref.getString("appVersion", null);
    }

    public static String getTokenApp(Context context) {
        setupContext(context);
        SharedPreferences sPref = Context.getSharedPreferences("tokenApp", Context.MODE_PRIVATE);
        return sPref.getString("tokenApp", null);
    }
    public static Boolean getIsNurse(Context context) {
        setupContext(context);
        SharedPreferences sPref = Context.getSharedPreferences("isNurse", Context.MODE_PRIVATE);
        return sPref.getBoolean("isNurse", true);
    }


    public static void setTokenUser(String token, Context context) {
        setupContext(context);
        SharedPreferences sPref = Context.getSharedPreferences("tokenUser", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("tokenUser", token);
        ed.apply();
    }

    public static String getTokenUser(Context context) {
        setupContext(context);
        SharedPreferences sPref = Context.getSharedPreferences("tokenUser", Context.MODE_PRIVATE);
        return sPref.getString("tokenUser", "");
    }

    public static void setLoginPass(String login, String pass, Context context) {
        setupContext(context);
        SharedPreferences sPref = Context.getSharedPreferences("loginPass", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("login", login);
        ed.putString("pass", pass);
        ed.apply();
    }

    public static String getLogin(Context context) {
        setupContext(context);
        SharedPreferences sPref = Context.getSharedPreferences("loginPass", Context.MODE_PRIVATE);
        return sPref.getString("login", "");
    }

    public static String getPass(Context context) {
        setupContext(context);
        SharedPreferences sPref = Context.getSharedPreferences("loginPass", Context.MODE_PRIVATE);
        return sPref.getString("pass", "");
    }


    public static void setDocStatus(int status, Context context) {
        setupContext(context);
        SharedPreferences sPref = Context.getSharedPreferences("DocStatus", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt("DocStatus", status);
        ed.apply();
    }

    public static int getDocStatus(Context context) {
        setupContext(context);
        SharedPreferences sPref = Context.getSharedPreferences("DocStatus", Context.MODE_PRIVATE);
        return sPref.getInt("DocStatus", Constants.STATUS_DOC_FREE);
    }


    public static void setCallDataMini(CallDataForGeoService data, Context context) {
        setupContext(context);
        SharedPreferences sPref = Context.getSharedPreferences("CallDataForGeoService", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt("idCall", data.getIdCall());
        ed.putFloat("lat", (float) data.getLat());
        ed.putFloat("lon", (float) data.getLon());
        ed.apply();
    }

    public static CallDataForGeoService getCallDataMini(Context context) {
        setupContext(context);
        SharedPreferences sPref = Context.getSharedPreferences("CallDataForGeoService", Context.MODE_PRIVATE);
        CallDataForGeoService callDataForGeoService = new CallDataForGeoService();

        callDataForGeoService.setIdCall(sPref.getInt("idCall", 0));
        callDataForGeoService.setLat(sPref.getFloat("lat", 0));
        callDataForGeoService.setLon(sPref.getFloat("lon", 0));

        return callDataForGeoService;
    }

//    public static void setSessionState(Context context, boolean sessionState) {
//        SharedPreferences sPref = context.getSharedPreferences("sessionState", Context.MODE_PRIVATE);
//        sPref.edit().putBoolean(getSessionOpenedKey(context), sessionState).apply();
//    }
//
//    public static boolean isSessionOpened(Context context) {
//        return context.getSharedPreferences(getSessionOpenedKey(context), Context.MODE_PRIVATE).getBoolean("sessionState", false);
//    }
//
//    private static String getSessionOpenedKey(Context context) {
//        return "sessionState_"+getLogin(context);
//    }

//    раскоментить еще в GeoService
//    public static void setIsSendingIStatus(boolean isSending, Context context) {
//        SharedPreferences sPref = context.getSharedPreferences("isSending", Context.MODE_PRIVATE);
//        SharedPreferences.Editor ed = sPref.edit();
//        ed.putBoolean("isSending", isSending);
//        ed.apply();
//    }
//
//    public static boolean isSendingIStatus(Context context) {
//        SharedPreferences sPref = context.getSharedPreferences("isSending", Context.MODE_PRIVATE);
//        return sPref.getBoolean("isSending", true);
//    }


}
