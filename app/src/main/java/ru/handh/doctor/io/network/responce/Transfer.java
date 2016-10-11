package ru.handh.doctor.io.network.responce;

import android.os.Parcel;
import android.os.Parcelable;

import retrofit.http.GET;

import static ru.handh.doctor.io.network.ApiInstance.retrofit;

/**
 * Created by hugochaves on 30.07.2016.
 */
public class Transfer implements Parcelable {
    private int id;                 // (integer): Идентификатор трансфера ,
    private int callId;             // (integer): Идентификатор заявки ,
    private int routeId;            // (integer): Идентификатор маршрута, см. GET /application/reference-book (transferRoute) ,
    private String routeValue;      // (string): Маршрута, см. GET /application/reference-book (transferRoute) ,
    private int typeId;             // (integer): Идентификатор типа трансфера, см. GET /application/reference-book (transferType)
    private String typeValue;       // (string) Тип трансфера, см. GET /application/reference-book (transferType) ,
    private float priceFix;         // (float): Стоимость трансфера, в рублях ,
    private float priceParking;     // (float): Стоимость платной парковки, в рублях ,
    private float distance;         // (float): Дистанция, в километрах ,
    private int timeInAWay;         // (integer): Время в пути, в секундах ,
    private String description;     // (string): Описание
    private String token;

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

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getRouteValue() {
        return routeValue;
    }

    public void setRouteValue(String routeValue) {
        this.routeValue = routeValue;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(String typeValue) {
        this.typeValue = typeValue;
    }

    public float getPriceFix() {
        return priceFix;
    }

    public void setPriceFix(float priceFix) {
        this.priceFix = priceFix;
    }

    public float getPriceParking() {
        return priceParking;
    }

    public void setPriceParking(float priceParking) {
        this.priceParking = priceParking;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int getTimeInAWay() {
        return timeInAWay;
    }

    public void setTimeInAWay(int timeInAWay) {
        this.timeInAWay = timeInAWay;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.callId);
        dest.writeInt(this.routeId);
        dest.writeString(this.routeValue);
        dest.writeInt(this.typeId);
        dest.writeString(this.typeValue);
        dest.writeFloat(this.priceFix);
        dest.writeFloat(this.priceParking);
        dest.writeFloat(this.distance);
        dest.writeInt(this.timeInAWay);
        dest.writeString(this.description);
    }

    public Transfer() {
    }

    protected Transfer(Parcel in) {
        this.id = in.readInt();
        this.callId = in.readInt();
        this.routeId = in.readInt();
        this.routeValue = in.readString();
        this.typeId = in.readInt();
        this.typeValue = in.readString();
        this.priceFix = in.readFloat();
        this.priceParking = in.readFloat();
        this.distance = in.readFloat();
        this.timeInAWay = in.readInt();
        this.description = in.readString();
    }

    public transient static final Parcelable.Creator<Transfer> CREATOR = new Parcelable.Creator<Transfer>() {
        @Override
        public Transfer createFromParcel(Parcel source) {
            return new Transfer(source);
        }

        @Override
        public Transfer[] newArray(int size) {
            return new Transfer[size];
        }
    };
}
