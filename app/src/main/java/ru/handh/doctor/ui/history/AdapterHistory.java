package ru.handh.doctor.ui.history;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.handh.doctor.R;
import ru.handh.doctor.io.network.responce.calls.DataCall;
import ru.handh.doctor.utils.Utils;

/**
 * Created by sgirn on 06.11.2015.
 * адаптер истории вызовов
 */
public class AdapterHistory extends RecyclerView.Adapter<AdapterHistory.ViewHolder> {

    private List<DataCall> records;
    private Context context;

    public AdapterHistory(List<DataCall> records, Context context) {
        this.records = records;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_history, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AdapterHistory.ViewHolder holder, final int position) {
        final DataCall record = records.get(position);
        holder.name.setText(record.getFio());
        holder.number.setText(context.getString(R.string.call) + " " + record.getIdCall());


        StringBuilder addressStr = new StringBuilder();
        addressStr.append(context.getString(R.string.street)).append(" ").append(record.getAddress().getStreet());
        addressStr.append(context.getString(R.string.pointHouse)).append(" ").append(record.getAddress().getHouse());
        addressStr.append(context.getString(R.string.pointFlat)).append(" ").append(record.getAddress().getFlat());

        holder.address.setText(addressStr.toString());


        SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm", new Locale("ru"));
        String hour = String.valueOf(formatTime.format(record.getTime()));

        holder.time.setText(hour);
        Utils.changeColor(record.getFio(), holder.image);


        if (position == records.size() - 1) {
            holder.divider.setVisibility(View.GONE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }


        if (isShowDayTitle(position, holder.dayHistory)) {
            holder.dayHistory.setVisibility(View.VISIBLE);
        } else {
            holder.dayHistory.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return records == null ? 0 : records.size();
    }

    private boolean isShowDayTitle(int position, TextView tv) {

        //приходящая дата
        Date time = records.get(position).getTime();
        Calendar selecttDate = Calendar.getInstance();
        selecttDate.setTime(time);
        resetHour(selecttDate);

        //сегодня
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        resetHour(c);

        String today = "";
        if (c.getTimeInMillis() == selecttDate.getTimeInMillis()) {
            today = context.getString(R.string.today) + " ";
        }

        //вчера
        c.add(Calendar.DATE, -1);
        if (c.getTimeInMillis() == selecttDate.getTimeInMillis()) {
            today = context.getString(R.string.yestarday) + " ";
        }


        SimpleDateFormat formatTime = new SimpleDateFormat("dd.MM.yyyy", new Locale("ru"));
        String hour = String.valueOf(formatTime.format(time));

        SimpleDateFormat formatWeek = new SimpleDateFormat("EEEE", new Locale("ru"));
        String dayWeek = String.valueOf(formatWeek.format(time));

        String dateWright = today + dayWeek + ", " + hour;

        if (position == 0) {
            tv.setText(dateWright);
            return true;
        }

        //приходящая дата - 1
        Calendar selectDateLast = Calendar.getInstance();
        selectDateLast.setTime(records.get(position - 1).getTime());
        resetHour(selectDateLast);

        if (selecttDate.getTimeInMillis() < selectDateLast.getTimeInMillis()) {
            tv.setText(dateWright);
            return true;
        }


        return false;
    }

    private void resetHour(Calendar c) {
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView image;
        private TextView number;
        private TextView address;
        private TextView time;
        private TextView dayHistory;
        private View divider;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_nameHistory);
            number = (TextView) itemView.findViewById(R.id.tv_applicationHistory);
            address = (TextView) itemView.findViewById(R.id.tv_addressHistory);
            time = (TextView) itemView.findViewById(R.id.tv_timeHistory);
            dayHistory = (TextView) itemView.findViewById(R.id.tv_DayHistory);
            image = (TextView) itemView.findViewById(R.id.tv_iconContactHistory);
            divider = itemView.findViewById(R.id.dividerHistory);
        }
    }
}
