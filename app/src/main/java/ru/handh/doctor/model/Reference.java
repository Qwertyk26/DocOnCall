package ru.handh.doctor.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Set;

/**
 * Created by samsonov on 02.08.2016.
 */
public class Reference implements Parcelable {
    private int id;
    private String value;
    private int sort;
    private boolean active;
    private CustomProperties customProperties;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public CustomProperties getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(CustomProperties customProperties) {
        this.customProperties = customProperties;
    }


    @Override
    public String toString() {
        return value;
    }

    public Reference() {
    }


    public boolean isTaxi() {
        return id == 87 || id == 91 || id ==  92 || id == 93;
    }

    public boolean isParking() {
        return id == 88 || id == 94;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.value);
        dest.writeInt(this.sort);
        dest.writeByte(this.active ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.customProperties, flags);
    }

    protected Reference(Parcel in) {
        this.id = in.readInt();
        this.value = in.readString();
        this.sort = in.readInt();
        this.active = in.readByte() != 0;
        this.customProperties = in.readParcelable(CustomProperties.class.getClassLoader());
    }

}
