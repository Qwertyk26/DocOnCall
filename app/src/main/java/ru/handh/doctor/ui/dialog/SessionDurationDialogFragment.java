package ru.handh.doctor.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.greenrobot.eventbus.EventBus;

import ru.handh.doctor.R;
import ru.handh.doctor.event.SessionDurationEvent;
import ru.handh.doctor.utils.Log4jHelper;
import ru.handh.doctor.utils.Utils;

/**
 * Created by hugochaves on 29.08.2016.
 */
public class SessionDurationDialogFragment extends DialogFragment {
    public final static String TAG = "SessionDurationDialogFragment";
    public static final int[] DURATIONS = {2, 4, 6, 8, 10, 12, 14};
    private static int SESSION_DIALOG_MAX_WIDTH = 350;
    org.apache.log4j.Logger log;
    private RadioGroup radioGroup;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        log = Log4jHelper.getLogger(TAG);
        SESSION_DIALOG_MAX_WIDTH = (int) Utils.pxFromDp(getContext(), 350);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_session_duration, null);

        radioGroup = (RadioGroup) rootView.findViewById(R.id.radio_group);

        for(int d : DURATIONS) {
            RadioButton b = new RadioButton(getContext());
            b.setText(d+" "+Utils.getHourString(d));
            b.setTextSize(16);
            b.setPadding(0, 4, 0, 4);
            b.setId(d);
            radioGroup.addView(b);
        }

        builder.setTitle("Начало рабочей смены")
                .setPositiveButton("Выбрать", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(radioGroup.getCheckedRadioButtonId()>0) {
                            EventBus.getDefault().post(new SessionDurationEvent(radioGroup.getCheckedRadioButtonId()));
                        }
                        dismiss();
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setView(rootView);
        log.info(TAG + " created");

        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(getResources().getDisplayMetrics().widthPixels*0.8f>SESSION_DIALOG_MAX_WIDTH) getDialog().getWindow().setLayout(SESSION_DIALOG_MAX_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onStart() {
        super.onStart();

        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#757575"));
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
    }
}
