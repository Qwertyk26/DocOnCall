package ru.handh.doctor.model;

import java.util.List;

/**
 * Created by hugochaves on 01.08.2016.
 */
public class ReferenceListResponse {
    private boolean success;
    private List<ReferenceBook> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<ReferenceBook> getData() {
        return data;
    }

    public void setData(List<ReferenceBook> data) {
        this.data = data;
    }
}
