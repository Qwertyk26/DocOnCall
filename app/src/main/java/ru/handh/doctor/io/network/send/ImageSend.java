package ru.handh.doctor.io.network.send;

/**
 * Created by antonnikitin on 07.10.16.
 */

public class ImageSend {
    private String token;
    private int idCall;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getIdCall() {
        return idCall;
    }

    public void setIdCall(int idCall) {
        this.idCall = idCall;
    }
}
