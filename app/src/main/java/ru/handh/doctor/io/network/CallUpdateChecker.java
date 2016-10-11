package ru.handh.doctor.io.network;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import ru.handh.doctor.MyApplication;
import ru.handh.doctor.io.network.responce.ModelLastUpdated;
import ru.handh.doctor.io.network.responce.calls.DataCall;
import ru.handh.doctor.io.network.responce.calls.ModelCallsReq;
import ru.handh.doctor.io.network.send.CallsSend;
import ru.handh.doctor.utils.SharedPref;
import ru.handh.doctor.utils.Utils;

/**
 * Created by samsonov on 14.07.2016.
 */
public class CallUpdateChecker {

    public static final String TAG = "CALL_UPDATE";

    public interface UpdateCallback {
        void onUpdate(DataCall call, boolean updated);
    }

    private static final Map<Integer, String> dataMap = new HashMap<>();

    public static void checkIfUpdated(DataCall dataCall, final UpdateCallback callback, boolean force) {
        checkIfUpdated(dataCall, callback, true, false, force);
    }

    public static void checkIfUpdated(final DataCall pDataCall, final UpdateCallback callback, final boolean isActive, final boolean isNew, final boolean force) {
        if(pDataCall==null) {
            callback.onUpdate(null, false);
            return;
        }
        Call<ModelLastUpdated> call = ApiInstance.restApi.getLastModified(SharedPref.getTokenApp(MyApplication.appContext), String.valueOf(pDataCall.getIdCall()));
        call.enqueue(new Callback<ModelLastUpdated>() {
            @Override
            public void onResponse(Response<ModelLastUpdated> response, Retrofit retrofit) {
                if (response.body() != null && response.body().getData() != null && response.body().getData().size() > 0) {
                    final String data = response.body().getData().get(0);
                    String mapData = dataMap.get(pDataCall.getIdCall());
                    boolean needsUpdate = data != null && mapData != null && !data.equals(mapData);
                    Log.d(TAG, "onResponse: CHECK SUCCESSFUL. needs update = " + needsUpdate);
                    if (needsUpdate || force) {
                        CallsSend callsSend = new CallsSend(SharedPref.getTokenUser(MyApplication.appContext), isActive, isNew);
                        Call<ModelCallsReq> calls = ApiInstance.restApi.getCalls(SharedPref.getTokenApp(MyApplication.appContext), callsSend);
                        calls.enqueue(new Callback<ModelCallsReq>() {
                            @Override
                            public void onResponse(Response<ModelCallsReq> response, Retrofit retrofit) {
                                Log.d(TAG, "onResponse: UPDATE RESPONSE SUCCESSFULL");
                                Utils.setCurrentOnLoad(response.body().data);
                                for (DataCall dataCall : response.body().data) {
                                    if (pDataCall.getIdCall() == dataCall.getIdCall()) {
                                        Log.d(TAG, "onResponse: ID MATCHES. UPDATE SUCCESSFUL");
                                        dataMap.put(pDataCall.getIdCall(), data);
                                        callback.onUpdate(dataCall, true);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                t.printStackTrace();
                                Log.d(TAG, "onFailure: UPDATE FAILED");
                                callback.onUpdate(pDataCall, false);
                            }
                        });
                    } else {
                        callback.onUpdate(pDataCall, false);
                        dataMap.put(pDataCall.getIdCall(), data);
                    }
                } else {
                    callback.onUpdate(pDataCall, false);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                callback.onUpdate(pDataCall, false);
                Log.d(TAG, "onFailure: CHECK FAILED");
            }
        });
        Log.d(TAG, "checkIfUpdated: CHECK STARTED");
    }
}
