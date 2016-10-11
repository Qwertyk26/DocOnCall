package ru.handh.doctor.io.network.send;

/**
 * Created by sgirn on 12.11.2015.
 */
public class StatusSend {
    private String token;
    private String status;

    public StatusSend(String token, String status) {
        this.token = token;
        this.status = status;
    }
}
