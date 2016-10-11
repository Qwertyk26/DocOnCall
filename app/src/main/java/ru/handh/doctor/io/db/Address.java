package ru.handh.doctor.io.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by 4x2bit on 06.09.16.
 */
public class Address extends RealmObject {
    @PrimaryKey
    private int id;
    private String address;
    private String city;
    private String street;
    private String house;
    private String structure;
    private String building;
    private String porch;
    private String intercom;
    private String floor;
    private String flat;
    private double latitude;
    private double longitude;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAddress() { return address; }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() { return city; }
    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() { return street; }
    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouse() { return house; }
    public void setHouse(String house) {
        this.house = house;
    }

    public String getStructure() { return structure; }
    public void setStructure(String structure) {
        this.structure = structure;
    }

    public String getBuilding() { return building; }
    public void setBuilding(String building) {
        this.building = building;
    }

    public String getPorch() { return porch; }
    public void setPorch(String porch) {
        this.porch = porch;
    }

    public String getIntercom() { return intercom; }
    public void setIntercom(String intercom) {
        this.intercom = intercom;
    }

    public String getFloor() { return floor; }
    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getFlat() { return flat; }
    public void setFlat(String flat) {
        this.flat = flat;
    }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
