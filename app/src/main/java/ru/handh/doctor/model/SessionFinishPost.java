package ru.handh.doctor.model;

/**
 * Created by hugochaves on 29.08.2016.
 */
public class SessionFinishPost {
    private String token;
    private int ordersCurrentClose;

    public SessionFinishPost(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getOrdersCurrentClose() {
        return ordersCurrentClose;
    }

    public void setOrdersCurrentClose(int ordersCurrentClose ) {
        this.ordersCurrentClose  = ordersCurrentClose;
    }
}
