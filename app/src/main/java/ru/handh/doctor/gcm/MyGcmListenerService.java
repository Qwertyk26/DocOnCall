package ru.handh.doctor.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;

import ru.handh.doctor.R;
import ru.handh.doctor.ui.main.MainActivity;
import ru.handh.doctor.utils.Constants;
import ru.handh.doctor.utils.SharedPref;

/**
 * Created by sgirn on 25.11.2015.
 * слушатель приходящих пушек
 */
public class MyGcmListenerService extends GcmListenerService {

    /**
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
//        Log.d(TAG, "From: " + from);
//        Log.d(TAG, "Message: " + message);

//        if (from.startsWith("/topics/")) {
//            // message received from some topic.
//        } else {
//            // normal downstream message.
//        }

        // [START_EXCLUDE]

//        SharedPreferences sharedPreferences =
//                PreferenceManager.getDefaultSharedPreferences(this);
//        String tokenGCM = sharedPreferences.getString(QuickstartPreferences.STRING_TOKEN_TO_SERVER, "");

//        if (!tokenGCM.equals("")) {
//            sendNotification(message);
//        }
        if (!SharedPref.getTokenUser(this).isEmpty()) {
            sendNotification(message);
        }
        updateUI();

        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constants.EXTRAS_PUSH, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.newCall))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

//        notificationManager.cancel(0);
    }

    private void updateUI() {
        Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
        intent.putExtra(MainActivity.PARAM_STATUS, MainActivity.STATUS_NEW_GCM_MESSAGE);
        sendBroadcast(intent);
    }

}
