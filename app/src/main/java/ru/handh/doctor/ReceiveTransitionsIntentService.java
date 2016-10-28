package ru.handh.doctor;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import ru.handh.doctor.ui.main.MainActivity;
import ru.handh.doctor.utils.GeofenceErrorMessages;
import ru.handh.doctor.utils.Log;
import ru.handh.doctor.utils.Log4jHelper;

/**
 * Created by sergey on 27.01.16.
 * сервис для отслеживания входв в геозоны
 */
public class ReceiveTransitionsIntentService extends IntentService {

    public static final String TRANSITION_INTENT_SERVICE = "ReceiveTransitionsIntentService";
    org.apache.log4j.Logger log;
    public ReceiveTransitionsIntentService() {
        super(TRANSITION_INTENT_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        log = Log4jHelper.getLogger(TRANSITION_INTENT_SERVICE);
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
            log.error(TRANSITION_INTENT_SERVICE + errorMessage);
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
//            String geofenceTransitionDetails = getGeofenceTransitionDetails(
//                    geofenceTransition,
//                    triggeringGeofences
//            );

//            Log.d(TRANSITION_INTENT_SERVICE, geofenceTransitionDetails);
            intersectionCircle(Integer.valueOf(triggeringGeofences.get(0).getRequestId()));
        } else {
            log.debug(TRANSITION_INTENT_SERVICE + getString(R.string.geofence_transition_invalid_type, geofenceTransition));
        }


    }

    private void intersectionCircle(int radius) {
        Intent intent = new Intent(GeoService.BROADCAST_GEO);
        intent.putExtra(MainActivity.PARAM_STATUS, radius);
        sendBroadcast(intent);
    }


    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param geofenceTransition  The ID of the geofence transition.
     * @param triggeringGeofences The geofence(s) triggered.
     * @return The transition details formatted as String.
     */
//    private String getGeofenceTransitionDetails(
//            int geofenceTransition,
//            List<Geofence> triggeringGeofences) {
//
//        String geofenceTransitionString = getTransitionString(geofenceTransition);
//
//        // Get the Ids of each geofence that was triggered.
//        ArrayList triggeringGeofencesIdsList = new ArrayList();
//        for (Geofence geofence : triggeringGeofences) {
//            triggeringGeofencesIdsList.add(geofence.getRequestId());
//        }
//        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);
//
//        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
//    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType A transition type constant defined in Geofence
     * @return A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }
}