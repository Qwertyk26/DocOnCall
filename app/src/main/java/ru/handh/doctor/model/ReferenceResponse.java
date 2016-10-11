package ru.handh.doctor.model;

import java.util.List;

/**
 * Created by samsonov on 02.08.2016.
 */
public class ReferenceResponse {
    private boolean success;

    private List<Reference> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Reference> getData() {
        return data;
    }

    public void setData(List<Reference> data) {
        this.data = data;
    }
}
