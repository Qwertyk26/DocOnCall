package ru.handh.doctor.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.handh.doctor.R;
import ru.handh.doctor.event.TransferInfoEvent;
import ru.handh.doctor.io.network.responce.Transfer;
import ru.handh.doctor.model.Reference;
import ru.handh.doctor.utils.Log4jHelper;

/**
 * Created by samsonov on 29.07.2016.
 */
public class TransferDialogFragment extends DialogFragment {
    public final static String TAG = "TransferDialogFragment";
    private static final String KEY_TRANSFER = "TRANSFER";
    private static final String KEY_TYPES = "TRANSFER_TYPES";
    org.apache.log4j.Logger log;

    public TransferDialogFragment() {}

    public static TransferDialogFragment newInstance(Transfer transfer, ArrayList<Reference> types) {
        TransferDialogFragment fragment = new TransferDialogFragment();

        if(transfer != null && types != null && types.size() > 0) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(KEY_TRANSFER, transfer);
            bundle.putParcelableArrayList(KEY_TYPES, (ArrayList<? extends Parcelable>) Parcels.wrap(types));
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    public static void initAndShow(FragmentManager fragmentManager, ArrayList<Reference> types) {
        initAndShow(fragmentManager, null, types);
    }

    public static void initAndShow(FragmentManager fragmentManager, Transfer transfer, ArrayList<Reference> types) {
        if(fragmentManager != null && fragmentManager.findFragmentByTag(TransferDialogFragment.class.getName()) == null) {
            TransferDialogFragment t = TransferDialogFragment.newInstance(transfer, types);
            if (t != null) {
                t.show(fragmentManager, TransferDialogFragment.class.getName());
            }
        }
    }

    private Unbinder unbinder;
    @BindView(R.id.transfer_type) Spinner type;
    @BindView(R.id.summ_block) LinearLayout summ_block;
    @BindView(R.id.summ_caption) TextView summCaption;
    @BindView(R.id.summ) EditText summ;
    @BindView(R.id.parking_divider) View parking_divider;
    @BindView(R.id.summ_divider) View summ_divider;
    @BindView(R.id.parking_block) LinearLayout parking_block;
    @BindView(R.id.parking_caption) TextView parkingCaption;
    @BindView(R.id.parking) EditText parking;
    @BindView(R.id.km) TextView km;
    @BindView(R.id.time) TextView time;
    @BindView(R.id.comment) TextView comment;
    @BindView(R.id.save) TextView save;
    @BindView(R.id.cancel) TextView cancel;

    private Transfer transfer;
    private ArrayList<Reference> types;

    private TransferTypeAdapter adapter;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        log = Log4jHelper.getLogger(TAG);
        if(getArguments()!=null) {
            transfer = getArguments().getParcelable(KEY_TRANSFER);
            types = Parcels.unwrap((Parcelable) getArguments().getParcelableArrayList(KEY_TYPES));
        }

        if(savedInstanceState!=null) {
            Transfer transfer = savedInstanceState.getParcelable(KEY_TRANSFER);
            if(transfer!=null) this.transfer = transfer;
            types = (ArrayList<Reference>) Parcels.wrap(savedInstanceState.getParcelableArrayList(KEY_TYPES));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_transfer, null);

        unbinder = ButterKnife.bind(this, rootView);

        adapter = new TransferTypeAdapter(getContext(), types);
        type.setAdapter(adapter);
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
               @Override
               public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                   Reference reference = adapter.getItem(position);
                    log.info(TAG + " item selected " + position);
                   summ_block.setVisibility(reference.isTaxi()?LinearLayout.VISIBLE:LinearLayout.GONE);
                   summ_divider.setVisibility(reference.isTaxi()?View.VISIBLE:View.GONE);

                   parking_block.setVisibility(reference.isParking()?LinearLayout.VISIBLE:LinearLayout.GONE);
                   parking_divider.setVisibility(reference.isParking()?View.VISIBLE:View.GONE);
               }

               @Override
               public void onNothingSelected(AdapterView<?> parent) {
                   summ_block.setVisibility(LinearLayout.GONE);
                   summ_divider.setVisibility(View.GONE);

                   parking_block.setVisibility(LinearLayout.GONE);
                   parking_divider.setVisibility(View.GONE);
               }
        });

        if(transfer!=null) {
            if(transfer.getPriceFix()!=0) summ.setText(String.valueOf((int) transfer.getPriceFix()));
            if(transfer.getPriceParking()!=0) parking.setText(String.valueOf((int) transfer.getPriceFix()));
            km.setText(String.valueOf(transfer.getDistance()));

            String timeString="";
            int sec = transfer.getTimeInAWay();
            if(sec<60) {
                timeString = "менее минуты";
            } else {
                int h = sec/3600;
                if(h>0) timeString+= h+" час ";
                timeString+=((sec-h*3600)/60)+" мин";
            }
            time.setText(timeString);
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                log.info(TAG + " cancel clicked");
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selected = type.getSelectedItemPosition();
                if(selected>=0) {
                    onSubmit(adapter.getItem(selected));
                }
                log.info(TAG + " save clicked");
            }
        });

        AlertDialog dialog = builder.setTitle("Отметка о трансфере")
                .setView(rootView)
                .create();

        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        log.info(TAG + " created");
        return dialog;
    }

    private void onSubmit(Reference reference) {
        if(transfer==null) transfer = new Transfer();

        transfer.setTypeId(reference.getId());
        transfer.setTypeValue(reference.getValue());

        if(reference.isTaxi() && summ.getText().length()>0) transfer.setPriceFix(Float.parseFloat(summ.getText().toString()));
        if(reference.isParking() && parking.getText().length()>0) transfer.setPriceParking(Float.parseFloat(parking.getText().toString()));

        transfer.setDescription(comment.getText().toString());

        EventBus.getDefault().post(new TransferInfoEvent(transfer));
        dismiss();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_TRANSFER, transfer);
        outState.putParcelableArrayList(KEY_TYPES, types);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if(unbinder!=null) unbinder.unbind();

        super.onDismiss(dialog);
    }
}
