package ru.handh.doctor.model;

/**
 * Created by samsonov on 30.08.2016.
 */
public class ModelDoctorSickListDate {
    private String sl_date_from;// (string, optional): Дата (с) ,
    private String sl_date_to;// (string, optional): Дата (по)

    public String getSl_date_from() {
        return sl_date_from;
    }

    public void setSl_date_from(String sl_date_from) {
        this.sl_date_from = sl_date_from;
    }

    public String getSl_date_to() {
        return sl_date_to;
    }

    public void setSl_date_to(String sl_date_to) {
        this.sl_date_to = sl_date_to;
    }
}
