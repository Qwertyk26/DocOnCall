package ru.handh.doctor.io.network.responce.calls;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by samsonov on 19.07.2016.
 */
public class DoctorTrafficSource implements Parcelable {
    private int id;
    private String name;
    private boolean recall;

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

    public boolean isRecall() {
        return recall;
    }

    public void setRecall(boolean recall) {
        this.recall = recall;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeByte((byte) (this.recall ? 1 : 0));
    }

    public DoctorTrafficSource() {
    }

    protected DoctorTrafficSource(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.recall = in.readByte() != 0;
    }

    public transient static final Parcelable.Creator<DoctorTrafficSource> CREATOR = new Parcelable.Creator<DoctorTrafficSource>() {
        @Override
        public DoctorTrafficSource createFromParcel(Parcel source) {
            return new DoctorTrafficSource(source);
        }

        @Override
        public DoctorTrafficSource[] newArray(int size) {
            return new DoctorTrafficSource[size];
        }
    };
}
