package ru.handh.doctor.ui.history;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;

import retrofit.Response;
import retrofit.Retrofit;
import ru.handh.doctor.R;
import ru.handh.doctor.io.network.responce.calls.DataCall;
import ru.handh.doctor.io.network.responce.calls.ModelCallsReq;
import ru.handh.doctor.io.network.ReqForCalls;
import ru.handh.doctor.ui.ParentFragment;
import ru.handh.doctor.ui.main.CallsOnResponce;
import ru.handh.doctor.ui.main.MainActivity;
import ru.handh.doctor.utils.Constants;
import ru.handh.doctor.utils.Utils;

/**
 * Created by sgirn on 06.10.2015.
 * история вызовов
 */
public class FragmentHistoryCalls extends ParentFragment implements CallsOnResponce {

    public final static String FRAGMENT_TAG = "FragmentHistoryCalls";
    private RecyclerView rv;
    private ReqForCalls reqForCalls;
    private SwipeRefreshLayout swipeLayout;
    private ArrayList<DataCall> historyList;
    private TextView emptyView;

    public FragmentHistoryCalls() {
    }

    public static FragmentHistoryCalls newInstance() {
        return new FragmentHistoryCalls();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) getActivity()).initActionBar(FRAGMENT_TAG, getResources().getString(R.string.history));

        View v = inflater.inflate(R.layout.fragment_history, container, false);
        viewFlipper = (ViewFlipper) v.findViewById(R.id.viewFlipperHistory);
        rv = (RecyclerView) v.findViewById(R.id.rvHistory);
        emptyView = (TextView) v.findViewById(R.id.empty_viewHistory);

        rv.setHasFixedSize(true);

        swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container_history);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        rv.setItemAnimator(itemAnimator);

        reqForCalls = new ReqForCalls();


        v.findViewById(R.id.button_reqError).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reqForCalls.callsReq(Constants.REQ_CALLS_HISTORY, FragmentHistoryCalls.this, getActivity());
            }
        });

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reqForCalls.callsReq(Constants.REQ_CALLS_HISTORY, FragmentHistoryCalls.this, getActivity());
            }
        });

        if (savedInstanceState == null) {
            reqForCalls.callsReq(Constants.REQ_CALLS_HISTORY, this, getActivity());
        } else {
            historyList = savedInstanceState.getParcelableArrayList("historyList");
            AdapterHistory adapter = new AdapterHistory(historyList, getActivity());
            rv.setAdapter(adapter);
            showData();
        }

        return v;
    }


    @Override
    public void callResponse(Response<ModelCallsReq> response, Retrofit retrofit) {
        if (response.code() == HttpURLConnection.HTTP_OK) {

            historyList = (ArrayList<DataCall>) response.body().data;
            Collections.sort(historyList);

            AdapterHistory adapter = new AdapterHistory(historyList, getActivity());
            rv.setAdapter(adapter);


            if (historyList.isEmpty()) {
                rv.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
            else {
                rv.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }

        } else {
            Utils.showErrorMessage(response, getActivity(), retrofit);
        }
        swipeLayout.setRefreshing(false);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList("historyList", historyList);

        super.onSaveInstanceState(savedInstanceState);
    }
}
