package ru.handh.doctor.io.network.responce;

import java.util.ArrayList;

/**
 * Created by hugochaves on 31.07.2016.
 */
public class DefaultResponse {
    private boolean success;
    private ArrayList<Error> data;

    public ArrayList<Error> getData() {
        return data;
    }

    public void setData(ArrayList<Error> data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static class Error {
        private int code;
        private String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
