package ru.handh.doctor.io.network.responce;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by antonnikitin on 11.10.16.
 */

public class ModelMessageData {
    private int chatId;
    private Date dateInsert;
    private Date dateUpdate;
    private int orderId;
    private String status;
    private ArrayList<ModelMessageList> messageList;

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public Date getDateInsert() {
        return dateInsert;
    }

    public void setDateInsert(Date dateInsert) {
        this.dateInsert = dateInsert;
    }

    public Date getDateUpdate() {
        return dateUpdate;
    }

    public void setDateUpdate(Date dateUpdate) {
        this.dateUpdate = dateUpdate;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<ModelMessageList> getMessageList() {
        return messageList;
    }

    public void setMessageList(ArrayList<ModelMessageList> messageList) {
        this.messageList = messageList;
    }
}
