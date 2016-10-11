package ru.handh.doctor.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hugochaves on 08.08.2016.
 */
public class Redirection implements Parcelable {
    private int id; // Идентификатор рекомендации, если не будет указана при обновлении - будет создана новая рекомендация ,
    private int callId; // Идентификатор заявки
    private int specialistId; // Идентификатор рекомендованного специалиста, см. GET /application/reference-book (redirectionSpecialistAdult|redirectionSpecialistChild)
    private String specialistValue; // Наименование рекомендованного специалиста
    private int consentToRecordId; // Идентификатор согласия на запись к специалисту, см. GET /application/reference-book (redirectionConsentToRecord)
    private String consentToRecordValue; // Наименование согласия на запись

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCallId() {
        return callId;
    }

    public void setCallId(int callId) {
        this.callId = callId;
    }

    public int getSpecialistId() {
        return specialistId;
    }

    public void setSpecialistId(int specialistId) {
        this.specialistId = specialistId;
    }

    public String getSpecialistValue() {
        return specialistValue;
    }

    public void setSpecialistValue(String specialistValue) {
        this.specialistValue = specialistValue;
    }

    public int getConsentToRecordId() {
        return consentToRecordId;
    }

    public void setConsentToRecordId(int consentToRecordId) {
        this.consentToRecordId = consentToRecordId;
    }

    public String getConsentToRecordValue() {
        return consentToRecordValue;
    }

    public void setConsentToRecordValue(String consentToRecordValue) {
        this.consentToRecordValue = consentToRecordValue;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.callId);
        dest.writeInt(this.specialistId);
        dest.writeString(this.specialistValue);
        dest.writeInt(this.consentToRecordId);
        dest.writeString(this.consentToRecordValue);
    }

    public Redirection() {
    }

    protected Redirection(Parcel in) {
        this.id = in.readInt();
        this.callId = in.readInt();
        this.specialistId = in.readInt();
        this.specialistValue = in.readString();
        this.consentToRecordId = in.readInt();
        this.consentToRecordValue = in.readString();
    }

    public transient static final Parcelable.Creator<Redirection> CREATOR = new Parcelable.Creator<Redirection>() {
        @Override
        public Redirection createFromParcel(Parcel source) {
            return new Redirection(source);
        }

        @Override
        public Redirection[] newArray(int size) {
            return new Redirection[size];
        }
    };
}
