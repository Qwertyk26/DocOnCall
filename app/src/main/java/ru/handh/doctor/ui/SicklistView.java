package ru.handh.doctor.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.handh.doctor.MyApplication;
import ru.handh.doctor.R;
import ru.handh.doctor.model.ModelDoctorSickList;
import ru.handh.doctor.model.ModelDoctorSickListDate;
import ru.handh.doctor.utils.Utils;

/**
 * Created by samsonov on 30.08.2016.
 */
public class SicklistView extends RelativeLayout {

    @BindView(R.id.sicklist_number) TextView sicklistNumber;
    @BindView(R.id.sicklist_status) TextView sicklistStatus;
    @BindView(R.id.sicklist_duplicate) TextView sicklistDuplicate;
    @BindView(R.id.sicklist_date) TextView sicklistDate;
    @BindView(R.id.sicklist_extra_dates) ViewGroup sicklistExtraDates;
    @BindView(R.id.sicklist_source) TextView sicklistSource;
    @BindView(R.id.sicklist_doctor) TextView sicklistDoctor;
    @BindView(R.id.sicklist_date_layout) View sicklistDateLayout;

    public SicklistView(Context context) {
        super(context);

        init();
    }

    public SicklistView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public SicklistView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_sicklist, this, true);

        ButterKnife.bind(this);
    }
    
    public void fillData(ModelDoctorSickList sickList) {
        sicklistNumber.setText(String.valueOf(sickList.getSl_list_number()));
        sicklistStatus.setText(sickList.getSl_status());
        if(sickList.getSl_status().toLowerCase().equals("продлен")) {
            sicklistStatus.setTextColor(getResources().getColor(R.color.colorAccent));
        }
        sicklistDuplicate.setVisibility(sickList.getSl_list_number_dublicate()>0 ? VISIBLE : GONE);

        if(sickList.getSl_date()!=null && sickList.getSl_date().size()>0) {
            sicklistDateLayout.setVisibility(View.VISIBLE);

            ModelDoctorSickListDate dates = sickList.getSl_date().get(0);
            sicklistDate.setText(Utils.makeDateString(dates.getSl_date_from(), dates.getSl_date_to()));

            for(int i=1;i<sickList.getSl_date().size();i++) {
                ModelDoctorSickListDate modelDate = sickList.getSl_date().get(i);

                View view = LayoutInflater.from(getContext()).inflate(R.layout.item_sicklist_date, sicklistExtraDates, true);

                TextView date = (TextView) view.findViewById(R.id.date);
                date.setText(Utils.makeDateString(modelDate.getSl_date_from(), modelDate.getSl_date_to()));
            }
        } else {
            sicklistDateLayout.setVisibility(View.GONE);
        }

        if(sickList.getSl_organization_name().equals("ООО Новая медицина")) {
            sicklistSource.setText(sickList.getSl_organization_name());
        } else {
            sicklistSource.setText("Другое ЛПУ");
        }

        if(sickList.getSl_doctor_name()!=null) {
            sicklistDoctor.setText(sickList.getSl_doctor_name());
        } else {
            sicklistDoctor.setText("-");
        }
    }
}
