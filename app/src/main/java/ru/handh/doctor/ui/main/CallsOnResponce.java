package ru.handh.doctor.ui.main;

import retrofit.Response;
import retrofit.Retrofit;
import ru.handh.doctor.io.network.responce.calls.ModelCallsReq;

/**
 * Created by sgirn on 16.11.2015.
 * интерфейс для запросов со списками вызовов (новые, текущие, история)
 */
public interface CallsOnResponce {
    void callResponse(Response<ModelCallsReq> response, Retrofit retrofit);
}
