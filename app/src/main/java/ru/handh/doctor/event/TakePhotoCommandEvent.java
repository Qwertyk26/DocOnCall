package ru.handh.doctor.event;

/**
 * Created by drybochkin on 09.10.2016.
 */

public class TakePhotoCommandEvent {
    private String command;

    public TakePhotoCommandEvent(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setXommand(String command) {
        this.command = command;
    }
}
