package ru.handh.doctor.io.network.responce.calls;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by samsonov on 19.07.2016.
 */
public class DoctorDateArrival implements Parcelable {
    private String from;
    private String to;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.from);
        dest.writeString(this.to);
    }

    public DoctorDateArrival() {
    }

    protected DoctorDateArrival(Parcel in) {
        this.from = in.readString();
        this.to = in.readString();
    }

    public transient static final Parcelable.Creator<DoctorDateArrival> CREATOR = new Parcelable.Creator<DoctorDateArrival>() {
        @Override
        public DoctorDateArrival createFromParcel(Parcel source) {
            return new DoctorDateArrival(source);
        }

        @Override
        public DoctorDateArrival[] newArray(int size) {
            return new DoctorDateArrival[size];
        }
    };
}
