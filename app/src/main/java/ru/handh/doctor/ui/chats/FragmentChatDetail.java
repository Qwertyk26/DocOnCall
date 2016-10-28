package ru.handh.doctor.ui.chats;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.handh.doctor.R;
import ru.handh.doctor.ui.ParentFragment;
import ru.handh.doctor.ui.calls.FragmentCallDetail;
import ru.handh.doctor.ui.main.MainActivity;
import ru.handh.doctor.utils.Utils;

/**
 * Created by antonnikitin on 13.10.16.
 */

public class FragmentChatDetail extends ParentFragment {
    private Unbinder unbinder;
    public final static String FRAGMENT_TAG = "FragmentChatDetail";

    public interface InstanceCreatedCallback {
        void onUpdate(FragmentChatDetail fragment);
    }

    public static FragmentChatDetail newInstance() {
        
        Bundle args = new Bundle();
        
        FragmentChatDetail fragment = new FragmentChatDetail();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat_detail, container, false);
        unbinder = ButterKnife.bind(this, v);
        if (!getResources().getBoolean(R.bool.isTablet)) {


        }
        return v;
    }
}
