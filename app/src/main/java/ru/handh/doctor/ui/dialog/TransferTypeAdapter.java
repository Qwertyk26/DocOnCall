package ru.handh.doctor.ui.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import java.util.EnumSet;
import java.util.List;

import ru.handh.doctor.R;
import ru.handh.doctor.model.Reference;

/**
 * Created by hugochaves on 31.07.2016.
 */
public class TransferTypeAdapter extends ArrayAdapter<Reference>{

    public TransferTypeAdapter(Context context, List<Reference> list) {
        super(context, R.layout.item_type, R.id.text, list);
    }

}
