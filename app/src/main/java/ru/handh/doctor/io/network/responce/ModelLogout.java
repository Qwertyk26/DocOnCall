package ru.handh.doctor.io.network.responce;

/**
 * Created by sgirn on 11.11.2015.
 */
public class ModelLogout extends ParentModel{
    public DataLogout data;

    public class DataLogout {
        String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }


    }

}
