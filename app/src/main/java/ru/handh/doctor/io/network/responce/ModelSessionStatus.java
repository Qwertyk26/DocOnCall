package ru.handh.doctor.io.network.responce;

/**
 * Created by drybochkin on 15.09.2016.
 */
public class ModelSessionStatus {
        private boolean success;
        public DataSessionStatus data;

        public boolean isSuccess() {
        return success;
    }

        public void setSuccess(boolean success) {
        this.success = success;
    }

        public class DataSessionStatus {
            boolean scheduleOpen;

            public boolean getScheduleOpen() {
                return scheduleOpen;
            }

            public void setScheduleOpen(boolean scheduleOpen) {
                this.scheduleOpen = scheduleOpen;
            }


        }
    }
