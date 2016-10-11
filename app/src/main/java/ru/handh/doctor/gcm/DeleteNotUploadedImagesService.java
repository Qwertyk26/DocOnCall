package ru.handh.doctor.gcm;

import android.app.IntentService;
import android.content.Intent;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import ru.handh.doctor.io.db.Image;

/**
 * Created by antonnikitin on 10.10.16.
 */

public class DeleteNotUploadedImagesService extends IntentService {
    private static String TAG = "DeleteNotUploadedImagesService";

    public DeleteNotUploadedImagesService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int idCall = intent.getIntExtra("idCall", 0);
        Realm realm = Realm.getInstance(getApplicationContext());
        realm.beginTransaction();
        RealmQuery<Image> image = realm.where(Image.class).equalTo("idCall", idCall).equalTo("isUploaded", true);
        RealmResults<Image> images = image.findAll();
        for (int i = 0; i < images.size(); i++) {
            File file = new File(images.get(i).getPath());
            file.delete();
            images.get(i).removeFromRealm();
        }
        realm.commitTransaction();
    }
}
