package ru.handh.doctor.event;

/**
 * Created by samsonov on 30.08.2016.
 */
public class SessionButtonVisibilityEvent {
    private boolean visible;

    public SessionButtonVisibilityEvent(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
