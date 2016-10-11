package ru.handh.doctor.ui.calls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.handh.doctor.R;
import ru.handh.doctor.io.network.responce.calls.DataCall;
import ru.handh.doctor.utils.Constants;
import ru.handh.doctor.utils.Utils;

/**
 * Created by sgirn on 03.11.2015.
 * адаптер текущих вызовов
 */
public class AdapterCurrentCalls extends ArrayAdapter {

    private Context context;
    private List<DataCall> calls;

    public AdapterCurrentCalls(Context context, List<DataCall> objects) {
        super(context, -1, objects);
        this.context = context;
        this.calls = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        if (rowView == null) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.item_current_calls_new, parent, false);

            ViewHolder viewHolder = new ViewHolder(rowView);
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();


        if (calls.size() == 0) {
            return rowView;
        }

        DataCall call = calls.get(position);

        String name = call.getFio();
        if (call.getStatusCall().equals(Constants.STATUS_CALL_END_CLIENT_E)) {
            name = name + "*";
        }
        holder.fio.setText(name);
        holder.callId.setText(String.format("%1$s %2$d - ", context.getString(R.string.application), call.getIdCall()));

        holder.status.setText(Utils.getShortStatus(call.getStatusCall()).toLowerCase());

        if (!call.getFio().trim().isEmpty()) {
            Utils.changeColor(call.getFio(), holder.icon);
        }

        holder.summ.setText(String.format("%sР", call.getServicePrice()));

        holder.callSource.setText(call.getTrafficSource().getName());

        boolean canMakeTimeString = Utils.canMakeTimeString(call);

        if(canMakeTimeString) {
            holder.time.setVisibility(View.VISIBLE);
            holder.time.setText(Utils.makeTimeString(call));
        } else if(call.getTime()!=null){
            holder.time.setVisibility(View.VISIBLE);
            holder.time.setText(new SimpleDateFormat("HH.mm").format(call.getTime()));
        } else {
            //holder.date.setVisibility(View.GONE);
            holder.time.setVisibility(View.GONE);
        }

        // Create an array of the attributes we want to resolve
        // using values from a theme
        // android.R.attr.selectableItemBackground requires API LEVEL 11
        int[] attrs = new int[]{android.R.attr.selectableItemBackground /* index 0 */};

        // Obtain the styled attributes. 'themedContext' is a context with a
        // theme, typically the current Activity (i.e. 'this')
        TypedArray ta = context.obtainStyledAttributes(attrs);

        // Now get the value of the 'listItemBackground' attribute that was
        // set in the theme used in 'themedContext'. The parameter is the index
        // of the attribute in the 'attrs' array. The returned Drawable
        // is what you are after
        Drawable drawableFromTheme = ta.getDrawable(0 /* index */);

        // Finally free resources used by TypedArray
        ta.recycle();

        if (call.isCurrent()) {
            holder.currentCall.setVisibility(View.VISIBLE);
        } else {
            holder.currentCall.setVisibility(View.INVISIBLE);
        }


        if (call.isSelected()) {
            holder.rootView.setBackgroundColor(Utils.getCustomColor(context, R.color.background2));
        } else {
            holder.rootView.setBackgroundDrawable(drawableFromTheme);
        }

        if (position == calls.size() - 1) {
            holder.divider.setVisibility(View.GONE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }

        return rowView;
    }

    public void setSelectedItem(int item) {

        for (int i = 0; i < calls.size(); i++) {
            if (item == i) {
                calls.get(i).setIsSelected(true);
            } else {
                calls.get(i).setIsSelected(false);
            }
        }

        notifyDataSetChanged();
    }

    public void updateItem(DataCall dataCall) {
        for(int i=0;i< calls.size();i++) {
            if(calls.get(i).getIdCall()==dataCall.getIdCall()) {
                calls.set(i, dataCall);
                notifyDataSetChanged();
                break;
            }
        }
    }

    static class ViewHolder {
        View rootView;
        @BindView(R.id.icon) TextView icon;
        @BindView(R.id.fio) TextView fio;
        @BindView(R.id.call_id) TextView callId;
        @BindView(R.id.status) TextView status;
        @BindView(R.id.summ) TextView summ;
        @BindView(R.id.call_source) TextView callSource;
        @BindView(R.id.time) TextView time;
        @BindView(R.id.divider) View divider;
        @BindView(R.id.current_call) View currentCall;

        public ViewHolder(View view) {
            rootView = view;
            ButterKnife.bind(this, view);
        }
    }

}