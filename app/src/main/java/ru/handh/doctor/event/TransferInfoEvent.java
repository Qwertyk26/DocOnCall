package ru.handh.doctor.event;

import ru.handh.doctor.io.network.responce.Transfer;

/**
 * Created by samsonov on 29.07.2016.
 */
public class TransferInfoEvent {
    private Transfer transfer;

    public TransferInfoEvent(Transfer transfer) {
        this.transfer = transfer;
    }

    public Transfer getTransfer() {
        return transfer;
    }

    public void setTransfer(Transfer transfer) {
        this.transfer = transfer;
    }
}
