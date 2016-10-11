package ru.handh.doctor.io.network.responce.calls;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by samsonov on 19.07.2016.
 */
public class DoctorPayment implements Parcelable {
    private int id;
    private String name;
    private boolean payed;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPayed() {
        return payed;
    }

    public void setPayed(boolean payed) {
        this.payed = payed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeByte(this.payed ? (byte) 1 : (byte) 0);
    }

    public DoctorPayment() {
    }

    protected DoctorPayment(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.payed = in.readByte() != 0;
    }

    public transient static final Parcelable.Creator<DoctorPayment> CREATOR = new Parcelable.Creator<DoctorPayment>() {
        @Override
        public DoctorPayment createFromParcel(Parcel source) {
            return new DoctorPayment(source);
        }

        @Override
        public DoctorPayment[] newArray(int size) {
            return new DoctorPayment[size];
        }
    };
}
