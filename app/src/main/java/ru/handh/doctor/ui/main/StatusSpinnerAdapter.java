package ru.handh.doctor.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.handh.doctor.R;
import ru.handh.doctor.utils.Utils;

/**
 * Created by sgirn on 02.11.2015.
 * адаптре статусов врача в меню
 */
public class StatusSpinnerAdapter extends ArrayAdapter {

    private Context context;
    private List<Boolean> values;

    public StatusSpinnerAdapter(Context context) {
        super(context, -1);
        this.context = context;
        addAll(initList());
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        return getDropDownView(position, convertView, parent);
    }


    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_status_spinner, parent, false);

        TextView title = (TextView) rowView.findViewById(R.id.tv_statusSpinner);
        ImageView image = (ImageView) rowView.findViewById(R.id.iv_iconStatus);
        ImageView selected = (ImageView) rowView.findViewById(R.id.iv_selectedItem);
        View divider = rowView.findViewById(R.id.status_divider);

        switch (position) {
            case 0:
                title.setText(context.getString(R.string.statusFree));
                image.setImageDrawable(Utils.getCustomDrawable(R.drawable.status_free, context));
                break;
            case 1:
                title.setText(context.getString(R.string.statusBusy));
                image.setImageDrawable(Utils.getCustomDrawable(R.drawable.status_busy, context));
                break;
            case 2:
                title.setText(context.getString(R.string.statusLunch));
                image.setImageDrawable(Utils.getCustomDrawable(R.drawable.status_lanch, context));
                if (values.size() <= 3)
                    divider.setVisibility(View.GONE);
                break;
            case 3:
                title.setText(R.string.offline);
                image.setImageDrawable(Utils.getCustomDrawable(R.drawable.ic_back, context));
                divider.setVisibility(View.GONE);
                break;
        }

        if (values.get(position)) {
            selected.setVisibility(View.VISIBLE);
        } else {
            selected.setVisibility(View.GONE);
        }

        return rowView;
    }

    public void changeListForSpinner(int position) {
        for (int i = 0; i < values.size(); i++) {
            if (i == position) {
                values.set(i, true);
            } else {
                values.set(i, false);
            }
        }
    }

    private List<Boolean> initList() {
        values = new ArrayList<>();
        values.add(true);
        values.add(false);
        values.add(false);
        return values;
    }

    public void incData() {
        if (values.size() == 4) {
            values.remove(values.size() - 1);
            notifyDataSetChanged();
        }
    }

    public void decData() {
        if (values.size() == 3) {
            values.add(true);
        }
    }
}