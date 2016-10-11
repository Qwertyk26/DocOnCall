package ru.handh.doctor.io.network.send;

/**
 * Created by sgirn on 16.11.2015.
 */
public class CallGetSend {
    private String token;
    private int idCall;
    private String statusCall;

    public CallGetSend(String token, int idCall, String statusCall) {
        this.token = token;
        this.idCall = idCall;
        this.statusCall = statusCall;
    }
}
