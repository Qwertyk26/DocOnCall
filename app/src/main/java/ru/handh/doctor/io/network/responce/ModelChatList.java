package ru.handh.doctor.io.network.responce;

import java.util.ArrayList;

/**
 * Created by antonnikitin on 11.10.16.
 */

public class ModelChatList {
    private boolean success;
    private ArrayList<ModelChatList> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ArrayList<ModelChatList> getData() {
        return data;
    }

    public void setData(ArrayList<ModelChatList> data) {
        this.data = data;
    }
}
