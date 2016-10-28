package ru.handh.doctor.event;

/**
 * Created by antonnikitin on 20.10.16.
 */

public class ShowHideProgressEvent {
    private String command;

    public ShowHideProgressEvent(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setXommand(String command) {
        this.command = command;
    }
}
