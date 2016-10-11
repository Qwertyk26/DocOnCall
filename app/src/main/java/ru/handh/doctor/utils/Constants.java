package ru.handh.doctor.utils;


import ru.handh.doctor.BuildConfig;

/**
 * Created by sgirn on 11.11.2015.
 * контатнты
 */
public class Constants {

    public static final String API_URL = BuildConfig.API_URL;

    public final static int VIEW_PROGRESS = 0;
    public final static int VIEW_CONTENT = 1;
    public final static int VIEW_ERROR = 2;

    public final static int STATUS_DOC_FREE = 0;
    public final static int STATUS_DOC_BUSY = 1;
    public final static int STATUS_DOC_LANCH = 2;
    public final static int STATUS_DOC_OFFLINE = 3;

    public final static int REQ_CALLS_NEW = 0;
    public final static int REQ_CALLS_ACTIVE = 1;
    public final static int REQ_CALLS_HISTORY = 2;

    public final static String STATUS_CALL_ASSIGNED_M = "M";
    public final static String STATUS_CALL_START_C = "C";
    public final static String STATUS_CALL_ARRIVED_I = "I";
    public final static String STATUS_CALL_COMPLETE_D = "D";
    public final static String STATUS_CALL_END_CLIENT_E = "E";
//    public final static String STATUS_CALL_FINISH_L = "L";

    public final static float MIN_DISTANCE_TO_CLIENT = 1000;
    public final static String EXTRAS_PUSH = "newCalls";

    public final static int NURSE_ID = 2138;
    public final static int MAX_NURSE_PHOTO_COUNT = 5; //0 - неограничено

    public static final String PACKAGE_NAME = "com.google.android.gms.location.Geofence";

    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";

    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 8;

    /**
     * For this sample, geofences expire after twelve hours.
     */
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 500; // 1 mile, 1.6 km

}
