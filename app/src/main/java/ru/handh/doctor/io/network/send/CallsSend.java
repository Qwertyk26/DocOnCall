package ru.handh.doctor.io.network.send;

/**
 * Created by sgirn on 13.11.2015.
 */
public class CallsSend {
    private String token;
    private boolean isActive;
    private boolean isNew;

    public CallsSend(String token, boolean isActive, boolean isNew) {
        this.token = token;
        this.isActive = isActive;
        this.isNew = isNew;
    }
}
