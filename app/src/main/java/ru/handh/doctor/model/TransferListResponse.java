package ru.handh.doctor.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import ru.handh.doctor.io.network.responce.Transfer;

/**
 * Created by samsonov on 01.08.2016.
 */
public class TransferListResponse implements Parcelable {
    private boolean success;
    private List<Transfer> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Transfer> getTransfers() {
        return data;
    }

    public void setTransfers(List<Transfer> data) {
        this.data = data;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.success ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.data);
    }

    public TransferListResponse() {
    }

    protected TransferListResponse(Parcel in) {
        this.success = in.readByte() != 0;
        this.data = in.createTypedArrayList(Transfer.CREATOR);
    }

    public transient static final Parcelable.Creator<TransferListResponse> CREATOR = new Parcelable.Creator<TransferListResponse>() {
        @Override
        public TransferListResponse createFromParcel(Parcel source) {
            return new TransferListResponse(source);
        }

        @Override
        public TransferListResponse[] newArray(int size) {
            return new TransferListResponse[size];
        }
    };
}
