package ru.handh.doctor.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.handh.doctor.R;
import ru.handh.doctor.event.RedirectionInfoEvent;
import ru.handh.doctor.model.Reference;
import ru.handh.doctor.utils.Log4jHelper;

/**
 * Created by samsonov on 04.08.2016.
 */
public class RedirectionDialogFragment extends DialogFragment {

    public final static String TAG = "RedirectionDialogFragment";
    private static final String KEY_DOCTORS = "DOCTORS";
    private static final String KEY_CONSENTS = "CONSENTS";
    org.apache.log4j.Logger log;

    public static RedirectionDialogFragment newInstance(ArrayList<Reference> doctorList, ArrayList<Reference> consentList) {
        RedirectionDialogFragment fragment = new RedirectionDialogFragment();

        if (doctorList != null && doctorList.size() > 0 && consentList != null && consentList.size() > 0) {
            Bundle args = new Bundle();
            args.putParcelableArrayList(KEY_DOCTORS, doctorList);
            args.putParcelableArrayList(KEY_CONSENTS, consentList);

            fragment.setArguments(args);
        }
        fragment.setCancelable(false);
        return fragment;
    }

    public static void initAndShow(FragmentManager fragmentManager, ArrayList<Reference> doctorList, ArrayList<Reference> consentList) {
        if(fragmentManager != null && fragmentManager.findFragmentByTag(RedirectionDialogFragment.class.getName()) == null) {
            RedirectionDialogFragment r = RedirectionDialogFragment.newInstance(doctorList, consentList);
            if (r != null) {
                r.show(fragmentManager, RedirectionDialogFragment.class.getName());
            }
        }
    }

    private ArrayList<Reference> doctors;
    private ArrayList<Reference> consents;

    private Unbinder unbinder;

    @BindView(R.id.list_view) ListView listView;
    @BindView(R.id.cancel) View cancel;
    @BindView(R.id.save) View save;

    private List<Pair<Reference, Reference>> resultList = new ArrayList<>();

    private DoctorSpinnerAdapter doctorSpinner;

    private RedirectionAdapter adapter;
    
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        log = Log4jHelper.getLogger(TAG);
        Bundle b = savedInstanceState;
        if(b==null) b = getArguments();
        if(b!=null) {
            doctors = b.getParcelableArrayList(KEY_DOCTORS);
            consents = b.getParcelableArrayList(KEY_CONSENTS);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_redirection, null);

        unbinder = ButterKnife.bind(this, rootView);

        doctorSpinner = new DoctorSpinnerAdapter(getContext());

        resultList.clear();
        adapter = new RedirectionAdapter();

        addNewRow();
        addNewRow();
        addNewRow();

        listView.setAdapter(adapter);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new RedirectionInfoEvent(resultList));
                dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        builder.setTitle("Перенаправления")
                .setView(rootView);
        log.info(TAG + " created");
        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_DOCTORS, doctors);
        outState.putParcelableArrayList(KEY_CONSENTS, consents);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if(unbinder!=null) unbinder.unbind();

        super.onDismiss(dialog);
    }

    private void addNewRow() {
        resultList.add(new Pair<>(doctors.get(0), consents.get(0)));
        adapter.notifyDataSetChanged();
    }

    private class RedirectionAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int pPosition, View convertView, ViewGroup parent) {
            if(convertView==null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_redirection, parent, false);
            }

            final Spinner doctor = (Spinner) convertView.findViewById(R.id.doctor);
            doctor.setAdapter(doctorSpinner);
            final Spinner consent = (Spinner) convertView.findViewById(R.id.consent);

            Pair<Reference, Reference> pair = resultList.get(pPosition);

            doctor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Reference doctorRef = doctors.get(position);
                    log.info(TAG + " spinner item selected " + position);
                    boolean writable = doctorRef.getCustomProperties() == null || doctorRef.getCustomProperties().isWritable();
                    List<Reference> references = new ArrayList<>();
                    for(Reference reference : consents) {
                        if (writable == (reference.getCustomProperties()== null || reference.getCustomProperties().isWritable())) {
                            references.add(reference);
                        }
                    }

                    consent.setAdapter(new ConsentSpinnerAdapter(getContext(), references));

                    int contains = -1;
                    Reference contRef = resultList.get(pPosition).second;
                    for(int i=0;i<consent.getCount();i++) {
                        if(contRef.getId()==((Reference) consent.getItemAtPosition(i)).getId()) {
                            contains = i;
                            break;
                        }
                    }

                    if(contains>=0) {
                        consent.setSelection(contains);
                    } else {
                        if(consent.getSelectedItemPosition() >= 0) {
                            Reference conRef;
                            if(consent.getSelectedItemPosition() < consent.getCount()) {
                                conRef = (Reference) consent.getAdapter().getItem(consent.getSelectedItemPosition());
                            } else {
                                conRef = (Reference) consent.getAdapter().getItem(0);
                            }
                            Pair<Reference, Reference> newPair = new Pair<>(doctorRef,  conRef);
                            resultList.set(pPosition, newPair);
                        }
                    }

                    checkRowNeeded();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            consent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    log.info(TAG + " spinner item selected " + position);
                    Pair<Reference, Reference> newPair = new Pair<>(doctors.get(doctor.getSelectedItemPosition()), (Reference) consent.getAdapter().getItem(consent.getSelectedItemPosition()));
                    resultList.set(pPosition, newPair);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            doctor.setSelection(doctors.indexOf(pair.first));

            return convertView;
        }

        private void checkRowNeeded() {
            if(!resultList.get(resultList.size()-1).first.getValue().equals("-")) {
                addNewRow();
            }
        }
    }

    private class DoctorSpinnerAdapter extends ArrayAdapter<Reference> {
        public DoctorSpinnerAdapter(Context context) {
            super(context, R.layout.item_type, R.id.text, doctors);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Reference reference = getItem(position);
            if (convertView == null) {
                convertView  = LayoutInflater.from(getContext()).inflate(R.layout.item_type, null);
                ((TextView)convertView.findViewById(R.id.text)).setText(reference.getValue());
            }
            return convertView;
        }
    }

    private class ConsentSpinnerAdapter extends ArrayAdapter<Reference> {
        public ConsentSpinnerAdapter(Context context, List<Reference> references) {
            super(context, R.layout.item_type, R.id.text, references);
        }
    }
}
