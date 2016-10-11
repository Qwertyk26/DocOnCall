package ru.handh.doctor.io.network.responce.calls;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by samsonov on 19.07.2016.
 */
public class DoctorService implements Parcelable {
    private int id;
    private String name;
    private String price;
    private String iblockCode;

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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getIblockCode() {
        return iblockCode;
    }

    public void setIblockCode(String iblockCode) {
        this.iblockCode = iblockCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.price);
        dest.writeString(this.iblockCode);
    }

    public DoctorService() {
    }

    protected DoctorService(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.price = in.readString();
        this.iblockCode = in.readString();
    }

    public transient static final Creator<DoctorService> CREATOR = new Creator<DoctorService>() {
        @Override
        public DoctorService createFromParcel(Parcel source) {
            return new DoctorService(source);
        }

        @Override
        public DoctorService[] newArray(int size) {
            return new DoctorService[size];
        }
    };
}
