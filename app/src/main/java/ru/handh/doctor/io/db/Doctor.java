package ru.handh.doctor.io.db;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sgirn on 11.11.2015.
 */
public class Doctor extends RealmObject {

    @PrimaryKey
    private int doctorId;
    private String name;
    private String surname;
    private String middleName;
    private float rating;
    private String description;
    private boolean docTimeworkId;

    private Address address;
    private RealmList<SpecializationDoc> specializations;
    private RealmList<Image> images;
    private RealmList<Image> images_square;

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDocTimeworkId() {
        return docTimeworkId;
    }

    public void setDocTimeworkId(boolean docTimeworkId) {
        this.docTimeworkId = docTimeworkId;
    }

    public RealmList<SpecializationDoc> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(RealmList<SpecializationDoc> specializations) {
        this.specializations = specializations;
    }

    public RealmList<Image> getImages() {
        return images;
    }

    public void setImages(RealmList<Image> images) {
        this.images = images;
    }

    public RealmList<Image> getImages_square() {
        return images_square;
    }

    public void setImages_square(RealmList<Image> images_square) {
        this.images_square = images_square;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
