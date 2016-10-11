package ru.handh.doctor.io.network.responce;

/**
 * Created by sgirn on 19.11.2015.
 */
public class ModelErrors extends ParentModel{
    public DataError data;


    public class DataError {
        public Errors errors;


    }

    public class Errors {
        public int code;
        public String message;
    }

}
