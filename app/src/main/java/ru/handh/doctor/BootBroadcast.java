package ru.handh.doctor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.handh.doctor.utils.Constants;
import ru.handh.doctor.utils.SharedPref;

/**
 * Created by sgirn on 24.11.2015.
 * для перезапуска сервиса
 */
public class BootBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (SharedPref.getDocStatus(context) == Constants.STATUS_DOC_FREE) {
            //context.startService(new Intent(context, GeoService.class));
            context.startService(new Intent(context, FluffyGeoService.class));
        }
    }
}
