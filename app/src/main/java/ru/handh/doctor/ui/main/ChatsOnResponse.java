package ru.handh.doctor.ui.main;

import retrofit.Response;
import retrofit.Retrofit;
import ru.handh.doctor.io.network.responce.ModelChatList;
import ru.handh.doctor.io.network.responce.chats.ModelChatReq;


/**
 * Created by antonnikitin on 13.10.16.
 */

public interface ChatsOnResponse {
    void chatsResponse(Response<ModelChatList> response, Retrofit retrofit);
}
