package ru.handh.doctor.io.network.responce;

/**
 * Created by sgirn on 16.11.2015.
 */
public class ModelCallGet extends ParentModel{
    public DataLogout data;

    public class DataLogout {
        String statusCall;

        public String getText() {
            return statusCall;
        }

        public void setText(String statusCall) {
            this.statusCall = statusCall;
        }


    }
}
