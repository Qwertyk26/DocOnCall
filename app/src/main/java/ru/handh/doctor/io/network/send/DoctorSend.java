package ru.handh.doctor.io.network.send;

import android.content.Context;

import ru.handh.doctor.utils.DeviceUuidFactory;

/**
 * Created by sgirn on 10.11.2015.
 */
public class DoctorSend {
    private String phone;
    private String password;
    private String deviceId;
    private String pushToken;

    public DoctorSend() {
    }

    public DoctorSend(String phone, String password, String pushToken, Context context) {
        this.phone = phone;
        this.password = password;
        this.pushToken = pushToken;

        DeviceUuidFactory uuid = new DeviceUuidFactory(context);
        this.deviceId = uuid.getStringDeviceUuid();
    }
}
