package ru.handh.doctor.io.network;

import android.content.Context;
import android.widget.Toast;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;
import ru.handh.doctor.R;
import ru.handh.doctor.io.network.responce.calls.ModelCallsReq;
import ru.handh.doctor.io.network.send.CallsSend;
import ru.handh.doctor.ui.calls.FragmentCallsStart;
import ru.handh.doctor.ui.history.FragmentHistoryCalls;
import ru.handh.doctor.ui.main.CallsOnResponce;
import ru.handh.doctor.ui.main.MainActivity;
import ru.handh.doctor.utils.Constants;
import ru.handh.doctor.utils.SharedPref;

/**
 * Created by sgirn on 16.11.2015.
 * загрузка всех типов заявок
 */
public class ReqForCalls {

    private CallsOnResponce interfaceResponce;
    private FragmentHistoryCalls history;
    private FragmentCallsStart activCalls;

    public void callsReq(final int statusReq, CallsOnResponce interfaceInit, final Context context) {
        interfaceResponce = interfaceInit;

        Boolean isActive = false;
        Boolean isNew = false;

        switch (statusReq) {
            case Constants.REQ_CALLS_NEW:
//                if (((MainActivity) context).viewFlipperRightMenu.getDisplayedChild() != Constants.VIEW_PROGRESS)
//                    ((MainActivity) context).viewFlipperRightMenu.setDisplayedChild(Constants.VIEW_PROGRESS);
                isActive = true;
                isNew = true;
                break;
            case Constants.REQ_CALLS_ACTIVE:
                activCalls = (FragmentCallsStart) ((MainActivity) context).getSupportFragmentManager()
                        .findFragmentByTag(FragmentCallsStart.FRAGMENT_TAG);
                activCalls.showProgress();
                isActive = true;
                isNew = false;
                break;
            case Constants.REQ_CALLS_HISTORY:
                history = (FragmentHistoryCalls) ((MainActivity) context).getSupportFragmentManager()
                        .findFragmentByTag(FragmentHistoryCalls.FRAGMENT_TAG);
                history.showProgress();
                isActive = false;
                isNew = false;
                break;
        }

        CallsSend callsSend = new CallsSend(SharedPref.getTokenUser(context), isActive, isNew);
        Call<ModelCallsReq> call = ((MainActivity) context).restApi.getCalls(SharedPref.getTokenApp(context), callsSend);
        call.enqueue(new Callback<ModelCallsReq>() {
            @Override
            public void onResponse(retrofit.Response<ModelCallsReq> response, Retrofit retrofit) {
                switch (statusReq) {
                    case Constants.REQ_CALLS_NEW:
//                        if (((MainActivity) context).viewFlipperRightMenu.getDisplayedChild() != Constants.VIEW_CONTENT)
//                            ((MainActivity) context).viewFlipperRightMenu.setDisplayedChild(Constants.VIEW_CONTENT);
                        break;
                    case Constants.REQ_CALLS_ACTIVE:
                        activCalls.showData();
                        break;
                    case Constants.REQ_CALLS_HISTORY:
                        history.showData();
                        break;
                }
                interfaceResponce.callResponse(response, retrofit);
            }

            @Override
            public void onFailure(Throwable t) {

                switch (statusReq) {
                    case Constants.REQ_CALLS_NEW:
//                        if (((MainActivity) context).viewFlipperRightMenu.getDisplayedChild() != Constants.VIEW_CONTENT)
//                            ((MainActivity) context).viewFlipperRightMenu.setDisplayedChild(Constants.VIEW_CONTENT);
                        //((MainActivity) context).swipeLayoutNewCalls.setRefreshing(false);
                        Toast.makeText(context, R.string.notGetNewCall, Toast.LENGTH_LONG).show();
                        break;
                    case Constants.REQ_CALLS_ACTIVE:
                        activCalls.showError();
                        break;
                    case Constants.REQ_CALLS_HISTORY:
                        history.showError();
                        break;
                }
            }
        });
    }
}
