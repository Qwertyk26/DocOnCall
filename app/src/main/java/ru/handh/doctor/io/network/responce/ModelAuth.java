package ru.handh.doctor.io.network.responce;

/**
 * Created by sgirn on 06.10.2015.
 */
public class ModelAuth extends ParentModel{
    public ModeAuthData data;

    public class ModeAuthData {
        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        private String timestamp;

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }
}
