package ru.handh.doctor.event;

import ru.handh.doctor.io.network.responce.calls.DataCall;

/**
 * Created by hugochaves on 15.08.2016.
 */
public class CallUpdateEvent {
    private DataCall dataCall;

    public CallUpdateEvent(DataCall dataCall) {
        this.dataCall = dataCall;
    }

    public DataCall getDataCall() {
        return dataCall;
    }

    public void setDataCall(DataCall dataCall) {
        this.dataCall = dataCall;
    }
}
