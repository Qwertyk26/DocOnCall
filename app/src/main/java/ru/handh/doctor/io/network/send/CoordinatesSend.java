package ru.handh.doctor.io.network.send;

import android.content.Context;

import ru.handh.doctor.utils.DeviceUuidFactory;

/**
 * Created by sgirn on 24.11.2015.
 */
public class CoordinatesSend {
    private String deviceId;
    private String latitude;
    private String longitude;

    public CoordinatesSend(double latitude, double longitude, Context Context) {

        DeviceUuidFactory uuid = new DeviceUuidFactory(Context);
        this.deviceId = uuid.getStringDeviceUuid();
        this.latitude = String.valueOf(latitude);
        this.longitude = String.valueOf(longitude);
    }
}
