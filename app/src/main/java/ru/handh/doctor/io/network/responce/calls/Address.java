package ru.handh.doctor.io.network.responce.calls;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sgirn on 13.11.2015.
 */
public class Address implements Parcelable {

    private String street;
    private String house;
    private String building;
    private String porch;
    private String floor;
    private String flat;
    private String structure;
    private String intercom;
    private String phone;
    private double latitude;
    private double longitude;
    private String city;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getPorch() {
        return porch;
    }

    public void setPorch(String porch) {
        this.porch = porch;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getFlat() {
        return flat;
    }

    public void setFlat(String flat) {
        this.flat = flat;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) { this.city = city; }

    public String getIntercom() {
        return intercom;
    }

    public void setIntercom(String intercom) {
        this.intercom = intercom;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.street);
        dest.writeString(this.house);
        dest.writeString(this.building);
        dest.writeString(this.porch);
        dest.writeString(this.floor);
        dest.writeString(this.flat);
        dest.writeString(this.structure);
        dest.writeString(this.intercom);
        dest.writeString(this.phone);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.city);
    }

    public Address() {
    }

    protected Address(Parcel in) {
        this.street = in.readString();
        this.house = in.readString();
        this.building = in.readString();
        this.porch = in.readString();
        this.floor = in.readString();
        this.flat = in.readString();
        this.structure = in.readString();
        this.intercom = in.readString();
        this.phone = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.city = in.readString();
    }

    public transient static final Creator<Address> CREATOR = new Creator<Address>() {
        public Address createFromParcel(Parcel source) {
            return new Address(source);
        }

        public Address[] newArray(int size) {
            return new Address[size];
        }
    };
}
