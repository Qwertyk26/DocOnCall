package ru.handh.doctor.io.network.send;

/**
 * Created by sgirn on 17.11.2015.
 */
public class CallStatusSend {
    private String token;
    private int idCall;
    private String status;

    public CallStatusSend(String token, int idCall, String status) {
        this.token = token;
        this.idCall = idCall;
        this.status = status;
    }
}
