package ru.handh.doctor.io.network.responce.calls;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

/**
 * Created by sgirn on 13.11.2015.
 * модель конкретного вызова
 */
public class DataCall implements Parcelable, Comparable<DataCall> {

    private int idCall;
    private int idPatient;
    private Date time;
    private String patientBirthday;
    private String statusCall;
    private String fio;

    private boolean isCurrent;
    private boolean isSelected;

    private Address address;

    private String servicePrice;
    private List<DoctorService> serviceList;

    private DoctorDateArrival dateArrival;

    private List<String> phoneList;

    private DoctorPayment payment;

    private String patientFio;

    private DoctorTrafficSource trafficSource;

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getIdCall() {
        return idCall;
    }

    public void setIdCall(int idCall) {
        this.idCall = idCall;
    }

    public int getIdPatient() {
        return idPatient;
    }

    public void setIdPatient(int idPatient) {
        this.idPatient = idPatient;
    }


    public String getPatientBirthday() {
        return patientBirthday;
    }

    public void setPatientBirthday(String patientBirthday) {
        this.patientBirthday = patientBirthday;
    }

    public String getStatusCall() {
        return statusCall;
    }

    public void setStatusCall(String statusCall) {
        this.statusCall = statusCall;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setIsCurrent(boolean isCurrent) {
        this.isCurrent = isCurrent;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(String servicePrice) {
        this.servicePrice = servicePrice;
    }

    public List<DoctorService> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<DoctorService> serviceList) {
        this.serviceList = serviceList;
    }

    public DoctorDateArrival getDateArrival() {
        return dateArrival;
    }

    public void setDateArrival(DoctorDateArrival dateArrival) {
        this.dateArrival = dateArrival;
    }

    public List<String> getPhoneList() {
        return phoneList;
    }

    public void setPhoneList(List<String> phoneList) {
        this.phoneList = phoneList;
    }

    public DoctorPayment getPayment() {
        return payment;
    }

    public void setPayment(DoctorPayment payment) {
        this.payment = payment;
    }

    public String getPatientFio() {
        return patientFio;
    }

    public void setPatientFio(String patientFio) {
        this.patientFio = patientFio;
    }

    public DoctorTrafficSource getTrafficSource() {
        return trafficSource;
    }

    public void setTrafficSource(DoctorTrafficSource trafficSource) {
        this.trafficSource = trafficSource;
    }

    public DataCall() {
    }


    @Override
    public int compareTo(DataCall o) {
        return o.getTime().compareTo(getTime());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.idCall);
        dest.writeInt(this.idPatient);
        dest.writeLong(this.time != null ? this.time.getTime() : -1);
        dest.writeString(this.patientBirthday);
        dest.writeString(this.statusCall);
        dest.writeString(this.fio);
        dest.writeByte(this.isCurrent ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.address, flags);
        dest.writeString(this.servicePrice);
        dest.writeTypedList(this.serviceList);
        dest.writeParcelable(this.dateArrival, flags);
        dest.writeStringList(this.phoneList);
        dest.writeParcelable(this.payment, flags);
        dest.writeString(patientFio);
        dest.writeParcelable(trafficSource, flags);
    }

    protected DataCall(Parcel in) {
        this.idCall = in.readInt();
        this.idPatient = in.readInt();
        long tmpTime = in.readLong();
        this.time = tmpTime == -1 ? null : new Date(tmpTime);
        this.patientBirthday = in.readString();
        this.statusCall = in.readString();
        this.fio = in.readString();
        this.isCurrent = in.readByte() != 0;
        this.isSelected = in.readByte() != 0;
        this.address = in.readParcelable(Address.class.getClassLoader());
        this.servicePrice = in.readString();
        this.serviceList = in.createTypedArrayList(DoctorService.CREATOR);
        this.dateArrival = in.readParcelable(DoctorDateArrival.class.getClassLoader());
        this.phoneList = in.createStringArrayList();
        this.payment = in.readParcelable(DoctorPayment.class.getClassLoader());
        this.patientFio = in.readString();
        this.trafficSource = in.readParcelable(DoctorTrafficSource.class.getClassLoader());
    }

    public transient static final Creator<DataCall> CREATOR = new Creator<DataCall>() {
        @Override
        public DataCall createFromParcel(Parcel source) {
            return new DataCall(source);
        }

        @Override
        public DataCall[] newArray(int size) {
            return new DataCall[size];
        }
    };
}
