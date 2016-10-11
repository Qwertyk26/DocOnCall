package ru.handh.doctor.ui.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import ru.handh.doctor.R;
import ru.handh.doctor.model.CallNew;
import ru.handh.doctor.utils.Utils;

/**
 * Created by sgirn on 03.11.2015.
 * адаптер правого меню
 */
public class AdapterRightMenu extends RecyclerView.Adapter<AdapterRightMenu.ViewHolder> {

    private static final String ALLOW = "1";
    private static final String NOT = "0";
    private List<CallNew> records;
    private Context context;

    public AdapterRightMenu(List<CallNew> records, Context context) {
        this.records = records;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_right_menu, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AdapterRightMenu.ViewHolder holder, final int position) {
        final CallNew record = records.get(position);
        holder.name.setText(record.getName());
        holder.number.setText("№ " + record.getNumber());
        holder.status.setText(context.getString(R.string.newCall));


        holder.allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAt(position, true);
            }
        });


        holder.not.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAt(position, false);
            }
        });


        holder.not.setEnabled(false);// отключет кнопку отмены вызова

        Utils.changeColor(record.getName(), holder.image);


        if (position == records.size() - 1) {
            holder.divider.setVisibility(View.GONE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public void removeAt(int position, boolean isGet) {

//        if (isGet) {
//            ((MainActivity) context).callGetReq(Integer.valueOf(records.get(position).getNumber()), ALLOW);
//        } else {
//            ((MainActivity) context).callGetReq(Integer.valueOf(records.get(position).getNumber()), NOT);
//        }
    }

    public void dellReq(int position) {

        records.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, records.size());
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView image;
        private TextView number;
        private TextView status;
        private Button allow;
        private Button not;
        private View divider;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_nameRight);
            number = (TextView) itemView.findViewById(R.id.tv_numberRight);
            status = (TextView) itemView.findViewById(R.id.tv_typeRight);
            allow = (Button) itemView.findViewById(R.id.button_allowRightMenu);
            not = (Button) itemView.findViewById(R.id.button_notRightMenu);
            image = (TextView) itemView.findViewById(R.id.tv_iconContact);
            divider = itemView.findViewById(R.id.dividetRightMenu);
        }
    }


}
