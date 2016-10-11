package ru.handh.doctor.model;

import ru.handh.doctor.io.network.responce.Transfer;

/**
 * Created by hugochaves on 31.07.2016.
 */
public class TransferPost {
    private String token;
    private Transfer transfer;

    public TransferPost(String token, Transfer transfer) {
        this.token = token;
        this.transfer = transfer;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Transfer getTransfer() {
        return transfer;
    }

    public void setTransfer(Transfer transfer) {
        this.transfer = transfer;
    }
}
