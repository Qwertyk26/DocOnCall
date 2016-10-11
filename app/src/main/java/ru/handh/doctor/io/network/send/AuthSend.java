package ru.handh.doctor.io.network.send;

/**
 * Created by sgirn on 10.11.2015.
 */
public class AuthSend {
    private String clientId;
    private String password;

    public AuthSend() {
    }

    public AuthSend(String clientId, String password) {
        this.clientId = clientId;
        this.password = password;
    }
}
