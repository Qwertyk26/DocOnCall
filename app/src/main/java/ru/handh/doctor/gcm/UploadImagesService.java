package ru.handh.doctor.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;


import java.net.HttpURLConnection;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;

import ru.handh.doctor.R;
import ru.handh.doctor.io.db.Image;
import ru.handh.doctor.io.network.ApiInstance;
import ru.handh.doctor.io.network.CallbackWithTag;
import ru.handh.doctor.io.network.RestApi;
import ru.handh.doctor.io.network.responce.ModelCallImage;
import ru.handh.doctor.model.CallImagePost;
import ru.handh.doctor.utils.Log;
import ru.handh.doctor.utils.Log4jHelper;
import ru.handh.doctor.utils.SharedPref;
import ru.handh.doctor.utils.Utils;

/**
 * Created by antonnikitin on 04.10.16.
 */

public class UploadImagesService extends IntentService {
    private static String TAG = "UploadImageService";

    public static final String ACTION_UPDATE = "ru.handh.doctor.gcm.RESPONSE";
    public static int status = 0;
    private RestApi restApi = ApiInstance.uploadService(RestApi.class);
    private Realm realm;
    org.apache.log4j.Logger log;

    public UploadImagesService() {
        super(TAG);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        log = Log4jHelper.getLogger(TAG);
        log.info(TAG + " created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log.info(TAG + " destroyed");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        final Intent updateIntent = new Intent();
        realm = Realm.getInstance(this);
        final RealmQuery<Image> image = realm.where(Image.class).contains("userToken", SharedPref.getTokenUser(this)).equalTo("idCall", intent.getIntExtra("idCall", 0)).equalTo("isUploaded", false);
        final RealmResults<Image> images = image.findAll();
        for (int i = 0; i < images.size(); i++) {
            updateIntent.setAction(ACTION_UPDATE);
            updateIntent.addCategory(Intent.CATEGORY_DEFAULT);
            updateIntent.putExtra("status", true);
            sendBroadcast(updateIntent);
            String _imageData = Utils.ToBase64FromFilePath(images.get(i).getPath());
            CallImagePost _image = new CallImagePost(SharedPref.getTokenUser(this), intent.getIntExtra("idCall", 0), images.get(i).getName(), _imageData);
            Call<ModelCallImage> call = restApi.uploadImages(SharedPref.getTokenApp(this), _image);
            CallbackWithTag _callback = new CallbackWithTag<ModelCallImage>() {
                private int tag = -1;

                @Override
                public void onResponse(Response<ModelCallImage> response, Retrofit retrofit) {
                    if (response.code() == HttpURLConnection.HTTP_OK) {
                        log.info(TAG + response.raw().body());
                        realm = Realm.getInstance(UploadImagesService.this);
                        realm.beginTransaction();
                        RealmQuery<Image> image = realm.where(Image.class).equalTo("id", tag);
                        Image images = image.findFirst();
                        images.setUploaded(true);
                        realm.commitTransaction();
                        updateIntent.setAction(ACTION_UPDATE);
                        updateIntent.addCategory(Intent.CATEGORY_DEFAULT);
                        updateIntent.putExtra("status", true);
                        sendBroadcast(updateIntent);
                        log.info(TAG + " image uploaded");
                    } else {
                        updateIntent.setAction(ACTION_UPDATE);
                        updateIntent.addCategory(Intent.CATEGORY_DEFAULT);
                        updateIntent.putExtra("status", false);
                        sendBroadcast(updateIntent);
                        Handler handler = new Handler(Looper.getMainLooper());
                        log.info(TAG + " image uploaded error");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UploadImagesService.this.getApplicationContext(),getResources().getString(R.string.upload_error),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    log.info(TAG + " image uploaded error " + t.getMessage());
                    updateIntent.setAction(ACTION_UPDATE);
                    updateIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    updateIntent.putExtra("status", false);
                    sendBroadcast(updateIntent);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UploadImagesService.this.getApplicationContext(),getResources().getString(R.string.upload_error),Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void setTag(int tag) {
                    this.tag = tag;
                }

                @Override
                public int getTag() {
                    return tag;
                }
            };
            _callback.setTag(images.get(i).getId());
            call.enqueue(_callback);
        }
    }
}