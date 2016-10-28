package ru.handh.doctor.model;

import android.os.Parcel;
import android.os.Parcelable;



/**
 * Created by samsonov on 09.08.2016.
 */
public class CustomProperties implements Parcelable {
    private boolean writable;

    public boolean isWritable() {
        return writable;
    }

    public void setWritable(boolean writable) {
        this.writable = writable;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.writable ? (byte) 1 : (byte) 0);
    }

    public CustomProperties() {
    }

    protected CustomProperties(Parcel in) {
        this.writable = in.readByte() != 0;
    }

    public transient static final Parcelable.Creator<CustomProperties> CREATOR = new Parcelable.Creator<CustomProperties>() {
        @Override
        public CustomProperties createFromParcel(Parcel source) {
            return new CustomProperties(source);
        }

        @Override
        public CustomProperties[] newArray(int size) {
            return new CustomProperties[size];
        }
    };
}
