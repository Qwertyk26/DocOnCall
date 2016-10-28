package ru.handh.doctor.io.network;

import android.content.Context;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import ru.handh.doctor.io.network.responce.ModelChatList;
import ru.handh.doctor.io.network.responce.chats.ModelChatReq;
import ru.handh.doctor.ui.chats.FragmentChats;
import ru.handh.doctor.ui.history.FragmentHistoryCalls;
import ru.handh.doctor.ui.main.ChatsOnResponse;
import ru.handh.doctor.ui.main.MainActivity;
import ru.handh.doctor.utils.Log;
import ru.handh.doctor.utils.SharedPref;

/**
 * Created by antonnikitin on 13.10.16.
 */

public class ReqForChats {
    private ChatsOnResponse interfaceResponse;
    private FragmentChats chats;

    public void chatsReq(final ChatsOnResponse interfaceInit, final Context context) {
        interfaceResponse = interfaceInit;
        chats = (FragmentChats) ((MainActivity) context).getSupportFragmentManager()
                .findFragmentByTag(FragmentChats.FRAGMENT_TAG);
        Call<ModelChatList> call = ((MainActivity) context).restApi.getChatList(SharedPref.getTokenApp(context), SharedPref.getTokenUser(context), false);
        call.enqueue(new Callback<ModelChatList>() {
            @Override
            public void onResponse(Response<ModelChatList> response, Retrofit retrofit) {
                chats.showData();
                interfaceInit.chatsResponse(response, retrofit);
            }

            @Override
            public void onFailure(Throwable t) {
                chats.showError();
            }
        });
    }
}
