package ru.handh.doctor.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hugochaves on 08.08.2016.
 */
public class RedirectionShort implements Parcelable {
    private int id; // Идентификатор рекомендации, если не будет указана при обновлении - будет создана новая рекомендация ,
    private int callId; // Идентификатор заявки
    private int specialistId; // Идентификатор рекомендованного специалиста, см. GET /application/reference-book (redirectionSpecialistAdult|redirectionSpecialistChild)
    private int consentToRecordId; // Идентификатор согласия на запись к специалисту, см. GET /application/reference-book (redirectionConsentToRecord)

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


    public int getConsentToRecordId() {
        return consentToRecordId;
    }

    public void setConsentToRecordId(int consentToRecordId) {
        this.consentToRecordId = consentToRecordId;
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
        dest.writeInt(this.consentToRecordId);
    }

    public RedirectionShort() {
    }

    protected RedirectionShort(Parcel in) {
        this.id = in.readInt();
        this.callId = in.readInt();
        this.specialistId = in.readInt();
        this.consentToRecordId = in.readInt();
    }

    public transient static final Creator<RedirectionShort> CREATOR = new Creator<RedirectionShort>() {
        @Override
        public RedirectionShort createFromParcel(Parcel source) {
            return new RedirectionShort(source);
        }

        @Override
        public RedirectionShort[] newArray(int size) {
            return new RedirectionShort[size];
        }
    };
}
