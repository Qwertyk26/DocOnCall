package ru.handh.doctor.event;

/**
 * Created by hugochaves on 29.08.2016.
 */
public class SessionDurationEvent {
    private int duration;

    public SessionDurationEvent(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
