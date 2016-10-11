package ru.handh.doctor.model;

/**
 * Created by sgirn on 25.11.2015.
 * модель для передечи текущей заявки в сервис
 */
public class CallDataForGeoService {
    private int idCall;
    private double lat;
    private double lon;

    public int getIdCall() {
        return idCall;
    }

    public void setIdCall(int idCall) {
        this.idCall = idCall;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
