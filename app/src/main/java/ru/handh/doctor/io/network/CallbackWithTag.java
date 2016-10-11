package ru.handh.doctor.io.network;

import retrofit.Callback;

/**
 * Created by drybochkin on 10.10.2016.
 */

public interface CallbackWithTag<T> extends Callback<T> {
    public int getTag();
    public void setTag(int tag);
}
