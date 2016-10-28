package ru.handh.doctor.io.network.responce.chats;

import java.util.List;

import ru.handh.doctor.io.network.responce.ModelChatList;
import ru.handh.doctor.io.network.responce.ParentModel;

/**
 * Created by antonnikitin on 13.10.16.
 */

public class ModelChatReq extends ParentModel {
    public List<ModelChatList> data;
}
