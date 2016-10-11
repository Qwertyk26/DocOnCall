package ru.handh.doctor.io.network.responce;

/**
 * Created by sgirn on 17.11.2015.
 */
public class ModelCallStatus extends ParentModel{
    public DataLogout data;

    public class DataLogout {
        String currentStatus;

        public String getText() {
            return currentStatus;
        }

        public void setText(String currentStatus) {
            this.currentStatus = currentStatus;
        }


    }
}
