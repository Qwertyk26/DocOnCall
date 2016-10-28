package ru.handh.doctor.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by samsonov on 02.08.2016.
 */
public class ReferenceResponse {
    private boolean success;
    private Date lastUpdateDate;

    private ArrayList<Reference> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ArrayList<Reference> getData() {
        return data;
    }

    public void setData(ArrayList<Reference> data) {
        this.data = data;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }
}
