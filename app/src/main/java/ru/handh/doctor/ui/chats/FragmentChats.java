package ru.handh.doctor.ui.chats;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;
import ru.handh.doctor.R;
import ru.handh.doctor.io.network.ReqForChats;
import ru.handh.doctor.io.network.RestApi;
import ru.handh.doctor.io.network.responce.ModelChatList;
import ru.handh.doctor.io.network.responce.chats.ModelChatReq;
import ru.handh.doctor.ui.ParentFragment;
import ru.handh.doctor.ui.calls.FragmentCallDetail;
import ru.handh.doctor.ui.history.FragmentHistoryCalls;
import ru.handh.doctor.ui.main.ChatsOnResponse;
import ru.handh.doctor.ui.main.MainActivity;
import ru.handh.doctor.utils.Constants;
import ru.handh.doctor.utils.Log;
import ru.handh.doctor.utils.RecyclerItemClickListener;
import ru.handh.doctor.utils.Utils;

/**
 * Created by antonnikitin on 13.10.16.
 */

public class FragmentChats extends ParentFragment implements ChatsOnResponse {

    public final static String FRAGMENT_TAG = "FragmentChats";
    private boolean mTwoPane;
    @BindView(R.id.rvChats)
    RecyclerView rv;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipeLayout;
    private ReqForChats reqForChats;
    private ArrayList<ModelChatList> modelChatList;
    private Unbinder unbinder;
    @BindView(R.id.empty_Chats) TextView emptyView;

    public FragmentChats() {

    }

    public static FragmentChats newInstance() {
        return new FragmentChats();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).initActionBar(FRAGMENT_TAG, getResources().getString(R.string.chats));
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        unbinder = ButterKnife.bind(this, v);
        viewFlipper = (ViewFlipper) v.findViewById(R.id.viewFlipperChats);
        if (v.findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }
        reqForChats = new ReqForChats();
        reqForChats.chatsReq(this, getActivity());
        v.findViewById(R.id.button_reqError).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reqForChats.chatsReq(FragmentChats.this, getActivity());
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setHasFixedSize(true);
        rv.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), rv, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mTwoPane) {

                } else {

                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reqForChats.chatsReq(FragmentChats.this, getActivity());
            }
        });

        return v;
    }

    @Override
    public void chatsResponse(Response<ModelChatList> response, Retrofit retrofit) {
        if (response.code() == HttpURLConnection.HTTP_OK) {
            modelChatList = response.body().getData();
            if (modelChatList.isEmpty()) {
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
    }
}
