package ru.handh.doctor.gcm;

import android.app.IntentService;
import android.content.Intent;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import ru.handh.doctor.io.db.Image;
import ru.handh.doctor.utils.Log4jHelper;

/**
 * Created by antonnikitin on 10.10.16.
 */

public class DeleteNotUploadedImagesService extends IntentService {
    private static String TAG = "DeleteNotUploadedImagesService";
    org.apache.log4j.Logger log;
    public DeleteNotUploadedImagesService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        log = Log4jHelper.getLogger(TAG);
        int idCall = intent.getIntExtra("idCall", 0);
        Realm realm = Realm.getInstance(this);
        realm.beginTransaction();
        RealmQuery<Image> image = realm.where(Image.class).equalTo("idCall", idCall).equalTo("isUploaded", true);
        RealmResults<Image> images = image.findAll();
        for (int i = 0; i < images.size(); i++) {
            File file = new File(images.get(i).getPath());
            file.delete();
            log.info(TAG + " file deleted from " + file.getPath());
            log.info(TAG + " file deleted DB " + images.get(i).getName());
            images.get(i).removeFromRealm();
        }
        realm.commitTransaction();
    }
}
