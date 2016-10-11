package ru.handh.doctor.model;

/**
 * Created by hugochaves on 08.08.2016.
 */
public class RedirectionPost {
    private String token;
    private RedirectionShort redirection;

    public RedirectionPost(String token, RedirectionShort redirection) {
        this.token = token;
        this.redirection = redirection;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public RedirectionShort getRedirection() {
        return redirection;
    }

    public void setRedirection(RedirectionShort redirection) {
        this.redirection = redirection;
    }
}
