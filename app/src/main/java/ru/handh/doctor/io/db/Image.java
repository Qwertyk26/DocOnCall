package ru.handh.doctor.io.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sgirn on 11.11.2015.
 */
public class Image extends RealmObject {
    @PrimaryKey
    private int id;
    private int idCall;
    private String size;
    private String path;
    private String name;
    private boolean isUploaded;
    private String userToken;


    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCall() {
        return idCall;
    }

    public void setIdCall(int idCall) {
        this.idCall = idCall;
    }
}
