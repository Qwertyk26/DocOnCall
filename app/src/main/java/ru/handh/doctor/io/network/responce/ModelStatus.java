package ru.handh.doctor.io.network.responce;

/**
 * Created by sgirn on 12.11.2015.
 */
public class ModelStatus extends ParentModel{

    public DataDocStatus data;

    public class DataDocStatus {
        String doctorStatus;

        public String getDoctorStatus() {
            return doctorStatus;
        }

        public void setDoctorStatus(String doctorStatus) {
            this.doctorStatus = doctorStatus;
        }


    }
}
