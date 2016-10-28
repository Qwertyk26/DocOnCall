package ru.handh.doctor.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

import ru.handh.doctor.utils.Log4jHelper;

/**
 * Created by sgirn on 25.11.2015.
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService {
    org.apache.log4j.Logger log;
    private static final String TAG = "MyInstanceIDLS";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    // [START refresh_token]

    @Override
    public void onCreate() {
        super.onCreate();
        log = Log4jHelper.getLogger(TAG);
    }
    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
        log.info(TAG + "token refreshed");
    }
    // [END refresh_token]
}