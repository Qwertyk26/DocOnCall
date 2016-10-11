package ru.handh.doctor;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Converter;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import ru.handh.doctor.io.network.ApiInstance;
import ru.handh.doctor.io.network.RestApi;
import ru.handh.doctor.io.network.responce.ModelCallStatus;
import ru.handh.doctor.io.network.responce.ModelCoordinates;
import ru.handh.doctor.io.network.responce.ModelErrors;
import ru.handh.doctor.io.network.send.CallStatusSend;
import ru.handh.doctor.io.network.send.CoordinatesSend;
import ru.handh.doctor.model.CallDataForGeoService;
import ru.handh.doctor.ui.main.MainActivity;
import ru.handh.doctor.utils.Constants;
import ru.handh.doctor.utils.GeofenceErrorMessages;
import ru.handh.doctor.utils.Log;
import ru.handh.doctor.utils.SharedPref;

/**
 * Created by sgirn on 24.11.2015.
 * сервис по опреедлению и отправке координат
 */
public class GeoService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,
        ResultCallback<Status> {

    public final static int RADIUS_BIG = 3000;
    public final static int RADIUS_SMALL = 1000;
    public final static int RADIUS_END = 0;
    public final static String BROADCAST_GEO = "BROADCAST_GEO";
    private final float SMALLEST_DISTANTION = 20;
    private final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private final long coeffBalance = 2;
    private final long coeffLow = 4;
    protected ArrayList<Geofence> mGeofenceList;
    private BroadcastReceiver brGeo;
    private int numberErrorCalls = 3;
    private boolean isFirstSendStatus;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation, locationClient = new Location("dummyprovider");
    private LocationRequest mLocationRequest;
    private CallDataForGeoService callMini;
    //private RestApi restApi;
    private PendingIntent mGeofencePendingIntent;
    private SharedPreferences mSharedPreferences;
    private boolean mGeofencesAdded;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        brGeo = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(MainActivity.PARAM_STATUS, 0);

                switch (status) {
                    case RADIUS_BIG:
                        removeGeofences();
                        changeAccuracyLocation(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                        addGeofences(RADIUS_SMALL);
                        break;
                    case RADIUS_SMALL:
                        removeGeofences();
                        changeAccuracyLocation(LocationRequest.PRIORITY_HIGH_ACCURACY);

                        if (isFirstSendStatus) {
                            isFirstSendStatus = !isFirstSendStatus;
                            Log.d("end");
                            callStatusReq();
                        }

                        break;
                    case RADIUS_END:
                        removeGeofences();
                        changeAccuracyLocation(LocationRequest.PRIORITY_LOW_POWER);
                        break;
                }
            }
        };

        IntentFilter intFilt = new IntentFilter(BROADCAST_GEO);
        registerReceiver(brGeo, intFilt);

        mGeofenceList = new ArrayList<>();
        mGeofencePendingIntent = null;
        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        createLocationRequest();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mGoogleApiClient.connect();
        Log.d("start");
        callMini = SharedPref.getCallDataMini(this);
        locationClient.setLatitude(callMini.getLat());
        locationClient.setLongitude(callMini.getLon());

        //приближение к точке
        mSharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);
        mGeofencesAdded = mSharedPreferences.getBoolean(Constants.GEOFENCES_ADDED_KEY, false);

        removeGeofences();
        addGeofences(RADIUS_BIG);
        ///////////

//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(Constants.API_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        restApi = retrofit.create(RestApi.class);


        isFirstSendStatus = true;
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }
        unregisterReceiver(brGeo);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    /**
     * отправка координат
     */
    private void sendCoordinates() {
        Log.d("координаты lat: " + mCurrentLocation.getLatitude() + ", lon: " + mCurrentLocation.getLongitude());
        CoordinatesSend coordSend = new CoordinatesSend(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), this);

        Call<ModelCoordinates> call = ApiInstance.restApi.getCoordinates(SharedPref.getTokenApp(this), coordSend);
        call.enqueue(new Callback<ModelCoordinates>() {
            @Override
            public void onResponse(Response<ModelCoordinates> response, Retrofit retrofit) {

                if (response.code() == HttpURLConnection.HTTP_OK) {
                    Log.d("координаты отправлены");
                } else {
                    if (!response.isSuccess() && response.errorBody() != null) {
                        Converter<ResponseBody, ModelErrors> errorConverter =
                                retrofit.responseConverter(ModelErrors.class, new Annotation[0]);
                        try {
                            ModelErrors error = errorConverter.convert(response.errorBody());
                            Log.d("координаты не отправлены: " + error.data.errors.message);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("координаты не отправлены ошибка сети");
            }
        });
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        changeAccuracyLocation(LocationRequest.PRIORITY_LOW_POWER);
    }


    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        sendCoordinates();
        Log.d("местоположение обновлено");
        updateCoordinatesInMain(mCurrentLocation);
    }

    /**
     * изменение статуса вызова (подъезжаю)
     */
    private void callStatusReq() {
        CallStatusSend statusSend = new CallStatusSend(SharedPref.getTokenUser(this), callMini.getIdCall(), Constants.STATUS_CALL_ARRIVED_I);
        Call<ModelCallStatus> call = ApiInstance.restApi.getCallStatus(SharedPref.getTokenApp(this), statusSend);
        call.enqueue(new Callback<ModelCallStatus>() {
            @Override
            public void onResponse(Response<ModelCallStatus> response, Retrofit retrofit) {
//полный путь B \\- M \- С - I - D - E -\\ L -
                if (response.code() == HttpURLConnection.HTTP_OK) {

                    callMini.setIdCall(0);
                    callMini.setLat(0);
                    callMini.setLon(0);
                    SharedPref.setCallDataMini(callMini, GeoService.this);
//                    SharedPref.setIsSendingIStatus(true, GeoService.this);//если нужно будет скрывать поле в  дополнительной инфе

                    updateChangeCallStatus();

                } else {
                    if (numberErrorCalls > 0) {
                        --numberErrorCalls;
                        callStatusReq();
//                        SharedPref.setIsSendingIStatus(false, GeoService.this);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (numberErrorCalls > 0) {
                    --numberErrorCalls;
                    callStatusReq();
//                    SharedPref.setIsSendingIStatus(false, GeoService.this);
                }
            }
        });
    }

    private void updateCoordinatesInMain(Location location) {
        Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
        intent.putExtra(MainActivity.PARAM_STATUS, MainActivity.STATUS_NEW_COORDINATES);
        intent.putExtra(MainActivity.PARAM_LOCATION, location);
        sendBroadcast(intent);
    }

    private void updateChangeCallStatus() {
        Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
        intent.putExtra(MainActivity.PARAM_STATUS, MainActivity.STATUS_CHANGE_STATUS);
        sendBroadcast(intent);
    }


    /////////////////////


    private void logSecurityException(SecurityException securityException) {
        Log.d("GEO Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences " + securityException);
    }

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        return builder.build();
    }


    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, ReceiveTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    /**
     * Runs when the result of calling addGeofences() and removeGeofences() becomes available.
     * Either method can complete successfully or with an error.
     * <p>
     * Since this activity implements the {@link ResultCallback} interface, we are required to
     * define this method.
     *
     * @param status The Status returned through a PendingIntent when addGeofences() or
     *               removeGeofences() get called.
     */
    @Override
    public void onResult(Status status) {
        if (status.isSuccess()) {
            mGeofencesAdded = !mGeofencesAdded;
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(Constants.GEOFENCES_ADDED_KEY, mGeofencesAdded);
            editor.apply();
        } else {
            String errorMessage = GeofenceErrorMessages.getErrorString(this, status.getStatusCode());
            Log.e(errorMessage);
        }
    }


    private void changeAccuracyLocation(int accuracy) {

        switch (accuracy) {
            case LocationRequest.PRIORITY_HIGH_ACCURACY:
                mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
                mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setSmallestDisplacement(SMALLEST_DISTANTION);

                break;
            case LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY:
                mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS * coeffBalance);
                mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS * coeffBalance);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                mLocationRequest.setSmallestDisplacement(SMALLEST_DISTANTION * coeffBalance);
                break;
            case LocationRequest.PRIORITY_LOW_POWER:
                mLocationRequest.setInterval(60000);
                mLocationRequest.setFastestInterval(30000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
                mLocationRequest.setSmallestDisplacement(100);
                break;
        }

    }

    public void populateGeofenceList(int radius) {
        mGeofenceList.add(new Geofence.Builder()
                .setRequestId(String.valueOf(radius))
                .setCircularRegion(
                        locationClient.getLatitude(),
                        locationClient.getLongitude(),
                        radius
                )
                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build());
    }

    private void addGeofences(int radius) {

        if (locationClient.getLatitude() == 0) {
            return;
        }

        populateGeofenceList(radius);

        if (!mGoogleApiClient.isConnected()) {
//            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
        } else {
            try {
                LocationServices.GeofencingApi.addGeofences(
                        mGoogleApiClient,
                        getGeofencingRequest(),
                        // A pending intent that that is reused when calling removeGeofences(). This
                        // pending intent is used to generate an intent when a matched geofence
                        // transition is observed.
                        getGeofencePendingIntent()
                ).setResultCallback(this); // Result processed in onResult().
            } catch (SecurityException securityException) {
                logSecurityException(securityException);
            }
        }
    }

    public void removeGeofences() {
        if (!mGoogleApiClient.isConnected()) {
//            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            // Remove geofences.
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    // This is the same pending intent that was used in addGeofences().
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().


            mGeofenceList.clear();

        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }
}
