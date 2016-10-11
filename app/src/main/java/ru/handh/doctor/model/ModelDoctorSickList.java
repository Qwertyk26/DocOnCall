package ru.handh.doctor.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samsonov on 30.08.2016.
 */
public class ModelDoctorSickList implements Parcelable {
    private int sl_list_number; //(integer, optional): Номер ЛН ,
    private String sl_status; //(string, optional): Статус бланка ЛН ,
    private String sl_organization_name; //(string, optional): Название организации, выдывшей ЛН
    private String sl_doctor_name; //(string, optional): ФИО доктора ,
    private int sl_type; //(integer, optional): Тип ЛН ,
    private int sl_list_number_dublicate; //(integer, optional): Номер ЛН дубликата ,
    private List<ModelDoctorSickListDate> sl_date;//(Array[ModelDoctorSickListDate], optional): Список услуг

    public int getSl_list_number() {
        return sl_list_number;
    }

    public void setSl_list_number(int sl_list_number) {
        this.sl_list_number = sl_list_number;
    }

    public String getSl_status() {
        return sl_status;
    }

    public void setSl_status(String sl_status) {
        this.sl_status = sl_status;
    }

    public String getSl_organization_name() {
        return sl_organization_name;
    }

    public void setSl_organization_name(String sl_organization_name) {
        this.sl_organization_name = sl_organization_name;
    }

    public String getSl_doctor_name() {
        return sl_doctor_name;
    }

    public void setSl_doctor_name(String sl_doctor_name) {
        this.sl_doctor_name = sl_doctor_name;
    }

    public int getSl_type() {
        return sl_type;
    }

    public void setSl_type(int sl_type) {
        this.sl_type = sl_type;
    }

    public int getSl_list_number_dublicate() {
        return sl_list_number_dublicate;
    }

    public void setSl_list_number_dublicate(int sl_list_number_dublicate) {
        this.sl_list_number_dublicate = sl_list_number_dublicate;
    }

    public List<ModelDoctorSickListDate> getSl_date() {
        return sl_date;
    }

    public void setSl_date(List<ModelDoctorSickListDate> sl_date) {
        this.sl_date = sl_date;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.sl_list_number);
        dest.writeString(this.sl_status);
        dest.writeString(this.sl_organization_name);
        dest.writeString(this.sl_doctor_name);
        dest.writeInt(this.sl_type);
        dest.writeInt(this.sl_list_number_dublicate);
        dest.writeList(this.sl_date);
    }

    public ModelDoctorSickList() {
    }

    protected ModelDoctorSickList(Parcel in) {
        this.sl_list_number = in.readInt();
        this.sl_status = in.readString();
        this.sl_organization_name = in.readString();
        this.sl_doctor_name = in.readString();
        this.sl_type = in.readInt();
        this.sl_list_number_dublicate = in.readInt();
        this.sl_date = new ArrayList<>();
        in.readList(this.sl_date, ModelDoctorSickListDate.class.getClassLoader());
    }

    public transient static final Parcelable.Creator<ModelDoctorSickList> CREATOR = new Parcelable.Creator<ModelDoctorSickList>() {
        @Override
        public ModelDoctorSickList createFromParcel(Parcel source) {
            return new ModelDoctorSickList(source);
        }

        @Override
        public ModelDoctorSickList[] newArray(int size) {
            return new ModelDoctorSickList[size];
        }
    };
}
