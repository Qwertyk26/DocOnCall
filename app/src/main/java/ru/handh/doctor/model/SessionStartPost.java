package ru.handh.doctor.model;

/**
 * Created by hugochaves on 29.08.2016.
 */
public class SessionStartPost {
    private String token;
    private String duration;

    public SessionStartPost(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
