package ru.handh.doctor.model;

import java.util.List;

import ru.handh.doctor.io.network.responce.DefaultResponse;

/**
 * Created by samsonov on 30.08.2016.
 */
public class SicklistResponce {
    private boolean success;
    //private List<ModelDoctorSickList> data;
    private ModelDoctorSickList data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

//    public List<ModelDoctorSickList> getData() {
//        return data;
//    }
//
//    public void setData(List<ModelDoctorSickList> data) {
//        this.data = data;
//    }

    public ModelDoctorSickList getData() {
        return data;
    }

    public void setData(ModelDoctorSickList data) {
        this.data = data;
    }
}
