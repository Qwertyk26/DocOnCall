package ru.handh.doctor.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sgirn on 03.11.2015.
 * модель для новой заявки
 */
public class CallNew implements Parcelable {
    public transient static final Parcelable.Creator<CallNew> CREATOR = new Parcelable.Creator<CallNew>() {
        public CallNew createFromParcel(Parcel source) {
            return new CallNew(source);
        }

        public CallNew[] newArray(int size) {
            return new CallNew[size];
        }
    };
    private String name;
    private boolean status;
    private String image;
    private String number;

    public CallNew() {
    }

    protected CallNew(Parcel in) {
        this.name = in.readString();
        this.status = in.readByte() != 0;
        this.image = in.readString();
        this.number = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeByte(status ? (byte) 1 : (byte) 0);
        dest.writeString(this.image);
        dest.writeString(this.number);
    }
}
