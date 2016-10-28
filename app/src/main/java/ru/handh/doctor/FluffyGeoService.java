package ru.handh.doctor;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;
import com.squareup.okhttp.ResponseBody;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.HttpURLConnection;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Converter;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import ru.handh.doctor.event.ForceCordsSendEvent;
import ru.handh.doctor.io.network.ApiInstance;
import ru.handh.doctor.io.network.RestApi;
import ru.handh.doctor.io.network.responce.ModelCoordinates;
import ru.handh.doctor.io.network.responce.ModelErrors;
import ru.handh.doctor.io.network.send.CoordinatesSend;
import ru.handh.doctor.ui.main.MainActivity;
import ru.handh.doctor.utils.Constants;
import ru.handh.doctor.utils.Log;
import ru.handh.doctor.utils.Log4jHelper;
import ru.handh.doctor.utils.SharedPref;

/**
 * Created by samsonov on 04.07.2016.
 */
public class FluffyGeoService extends Service {

    public final static String TAG = "FluffyGeoService";
    private static final float MIN_DISTANCE_TO_SEND = 100;
    private static final long THREAD_STEP = 500;

    public static long LOCATION_UPDATE_INTERVAL = 60 * 1000;

    //private RestApi restApi;

    private LocationInfo previousSentLocation;

    private Thread locationUpdateThread;
    private long timeSinceLastUpdate;

    private boolean destroyed = false;
    private boolean forceSendCords = false;
    org.apache.log4j.Logger log;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log = Log4jHelper.getLogger(TAG);
        final IntentFilter lftIntentFilter = new IntentFilter(LocationLibraryConstants.getLocationChangedPeriodicBroadcastAction());
        registerReceiver(lftBroadcastReceiver, lftIntentFilter);

        if(!EventBus.getDefault().isRegistered(onForceCordsSend)) {
            EventBus.getDefault().register(onForceCordsSend);
        }

        LocationLibrary.forceLocationUpdate(getApplicationContext());

        locationUpdateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!destroyed) {
                    try {
                        Thread.sleep(THREAD_STEP);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    timeSinceLastUpdate+=THREAD_STEP;
                    if(!destroyed && timeSinceLastUpdate > LOCATION_UPDATE_INTERVAL) {
                        timeSinceLastUpdate = 0;
                        LocationLibrary.forceLocationUpdate(getApplicationContext());
                    }
                }
            }
        });
        locationUpdateThread.start();

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(lftBroadcastReceiver);
        EventBus.getDefault().unregister(onForceCordsSend);

        destroyed = true;
        locationUpdateThread.interrupt();
        locationUpdateThread = null;

        super.onDestroy();
    }

    /**
     * отправка координат
     */
    private void sendCoordinates(LocationInfo locationInfo) {
        log.info(TAG + " координаты lat: " + locationInfo.lastLat + ", lon: " + locationInfo.lastLong);
        CoordinatesSend coordSend = new CoordinatesSend(locationInfo.lastLat, locationInfo.lastLong, this);

        Call<ModelCoordinates> call = ApiInstance.defaultService(RestApi.class).getCoordinates(SharedPref.getTokenApp(this), coordSend);
        call.enqueue(new Callback<ModelCoordinates>() {
            @Override
            public void onResponse(Response<ModelCoordinates> response, Retrofit retrofit) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    log.info(TAG + " координаты отправлены");
                } else {
                    if (!response.isSuccess() && response.errorBody() != null) {
                        Converter<ResponseBody, ModelErrors> errorConverter =
                                retrofit.responseConverter(ModelErrors.class, new Annotation[0]);
                        try {
                            ModelErrors error = errorConverter.convert(response.errorBody());
                            log.error(TAG + " координаты не отправлены: " + error == null ? "" : error.toString());

                        } catch (Exception e) {
                            if (e instanceof IOException) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                log.error(TAG + " координаты не отправлены ошибка сети");
            }
        });
    }

    public void onLocationChanged(LocationInfo locationInfo) {
        boolean sendCords = false;
        if(previousSentLocation ==null) sendCords = true;
        else {
            if(getDistance(previousSentLocation, locationInfo)>MIN_DISTANCE_TO_SEND) sendCords = true;
        }
        if(sendCords || forceSendCords) {
            sendCoordinates(locationInfo);
            forceSendCords = false;

            previousSentLocation = locationInfo;
        }

        log.info(TAG + " местоположение обновлено");
        updateCoordinatesInMain(locationInfo);
    }

    public void forceSendCords() {
        forceSendCords = true;
        LocationLibrary.forceLocationUpdate(getApplicationContext());
    }

    private float getDistance(LocationInfo li1, LocationInfo li2) {
        Location l1 = new Location("");
        l1.setLatitude(li1.lastLat);
        l1.setLongitude(li1.lastLong);

        Location l2 = new Location("");
        l2.setLatitude(li2.lastLat);
        l2.setLongitude(li2.lastLong);

        return l1.distanceTo(l2);
    }

    private void updateCoordinatesInMain(LocationInfo location) {
        Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
        intent.putExtra(MainActivity.PARAM_STATUS, MainActivity.STATUS_NEW_COORDINATES);
        intent.putExtra(MainActivity.PARAM_LOCATION, location);
        sendBroadcast(intent);
    }

    private final BroadcastReceiver lftBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // extract the location info in the broadcast
            final LocationInfo locationInfo = (LocationInfo) intent.getSerializableExtra(LocationLibraryConstants.LOCATION_BROADCAST_EXTRA_LOCATIONINFO);
            // refresh the display with it
            onLocationChanged(locationInfo);
        }
    };

    private Object onForceCordsSend = new Object() {
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEvent(ForceCordsSendEvent e) {
            forceSendCords();
        }
    };
}
