package ru.handh.doctor.model;

import android.os.Parcel;

/**
 * Created by hugochaves on 15.08.2016.
 */
public class DoctorReference extends Reference {

    public DoctorReference(Reference reference) {
        setId(reference.getId());
        setActive(reference.isActive());
        setCustomProperties(reference.getCustomProperties());
        setSort(reference.getSort());
        setValue(reference.getValue());
    }

    @Override
    public String toString() {
        return getValue().equals("-") ? "специалист" : getValue();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public DoctorReference() {
    }

    protected DoctorReference(Parcel in) {
        super(in);
    }

    public transient static final Creator<DoctorReference> CREATOR = new Creator<DoctorReference>() {
        @Override
        public DoctorReference createFromParcel(Parcel source) {
            return new DoctorReference(source);
        }

        @Override
        public DoctorReference[] newArray(int size) {
            return new DoctorReference[size];
        }
    };
}
