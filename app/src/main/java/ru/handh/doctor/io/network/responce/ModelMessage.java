package ru.handh.doctor.io.network.responce;

/**
 * Created by antonnikitin on 11.10.16.
 */

public class ModelMessage {
    private boolean success;
    private ModelMessageData data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ModelMessageData getData() {
        return data;
    }

    public void setData(ModelMessageData data) {
        this.data = data;
    }
}
