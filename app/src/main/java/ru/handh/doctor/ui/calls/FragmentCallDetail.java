package ru.handh.doctor.ui.calls;


import android.app.Activity;
import android.content.ActivityNotFoundException;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import ru.handh.doctor.GeoService;
import ru.handh.doctor.R;
import ru.handh.doctor.event.ForceCordsSendEvent;
import ru.handh.doctor.event.RedirectionInfoEvent;
import ru.handh.doctor.event.TakePhotoCommandEvent;
import ru.handh.doctor.event.TransferInfoEvent;
import ru.handh.doctor.gcm.DeleteNotUploadedImagesService;
import ru.handh.doctor.io.network.ApiInstance;
import ru.handh.doctor.io.network.CallUpdateChecker;
import ru.handh.doctor.io.network.responce.ModelCallStatus;
import ru.handh.doctor.io.network.responce.DefaultResponse;
import ru.handh.doctor.io.network.responce.Transfer;
import ru.handh.doctor.io.network.responce.calls.DataCall;
import ru.handh.doctor.io.network.responce.calls.DoctorService;
import ru.handh.doctor.io.network.send.CallStatusSend;
import ru.handh.doctor.model.RedirectionPost;
import ru.handh.doctor.model.RedirectionShort;
import ru.handh.doctor.model.Reference;
import ru.handh.doctor.model.ReferenceResponse;
import ru.handh.doctor.model.TransferListResponse;
import ru.handh.doctor.model.TransferPost;
import ru.handh.doctor.ui.ParentFragment;
import ru.handh.doctor.ui.dialog.RedirectionDialogFragment;
import ru.handh.doctor.ui.dialog.TakePhotoDialogFragment;
import ru.handh.doctor.ui.dialog.TransferDialogFragment;
import ru.handh.doctor.ui.main.MainActivity;
import ru.handh.doctor.utils.Constants;
import ru.handh.doctor.utils.SharedPref;
import ru.handh.doctor.utils.Utils;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link FragmentCallsStart}
 * in two-pane mode (on tablets) or a {@link ActivityCallDetail}
 * on handsets.
 */
public class FragmentCallDetail extends ParentFragment {


    public interface InstanceCreatedCallback {
        void onUpdate(FragmentCallDetail fragment);
    }
    public final static String FRAGMENT_TAG = "FragmentCallDetail";

    public DataCall call;
    private boolean isHaveSelect;
    private String sendStatus;
    private String lastStatus = "";
    private Button statusChangeButton;
    private MainActivity ma = (MainActivity) getActivity();
    private AlertDialog locationDialog;
    private TakePhotoDialogFragment takePhotoDialogFragment;
    Intent deleteNotUploadedImages;

    private View.OnClickListener buttonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            double latitude = call.getAddress().getLatitude();
            double longitude = call.getAddress().getLongitude();
            switch (v.getId()) {
                case R.id.closeCall_button:
                    if(lastStatus.equals(Constants.STATUS_CALL_ASSIGNED_M)) {
                        EventBus.getDefault().post(new ForceCordsSendEvent());
                    }

//                    if(lastStatus.equals(Constants.STATUS_CALL_START_C)) {
//                        openTransferDialog(false, null);
//                    } else
                    if(!SharedPref.getIsNurse(getContext()) && lastStatus.equals(Constants.STATUS_CALL_COMPLETE_D)) {
                        openRedirectionDialog(false);
                    } else if (SharedPref.getIsNurse(getContext()) && lastStatus.equals(Constants.STATUS_CALL_ARRIVED_I)) {
                        takePhotoDialogFragment = TakePhotoDialogFragment.newInstance(call.getIdCall(), SharedPref.getTokenUser(getContext()));
                        takePhotoDialogFragment.setTargetFragment(FragmentCallDetail.this, 1);
                        takePhotoDialogFragment.show(getFragmentManager(), "TakePhoto");
                    } else {
                        changeStatus();
                    }

                    break;

                case R.id.showMap_button:
                    String label = getActivity().getString(R.string.sick);
                    String uriBegin = "geo:" + latitude + "," + longitude;
                    String query = latitude + "," + longitude + "(" + label + ")";
                    String encodedQuery = Uri.encode(query);
                    String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                    Uri uri = Uri.parse(uriString);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    try {
                        getActivity().startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(ma, "У вас нет приложения-карт", Toast.LENGTH_SHORT).show();
                    }

                    break;

                case R.id.getDirection_button:

                    double latCurrent = 0;
                    double lonCurrent = 0;

                    if (((MainActivity) getActivity()).currentLocation != null) {
                        latCurrent = ((MainActivity) getActivity()).currentLocation.lastLat;
                        lonCurrent = ((MainActivity) getActivity()).currentLocation.lastLong;
                    }

                    if (latCurrent == 0) {
                        locationDialog = Utils.showLocationDialog(getActivity(), false);
                    } else {
                        Intent route = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?saddr="
                                        + latCurrent
                                        + ", " + lonCurrent
                                        + "&daddr=" + latitude + "," + longitude));
                        startActivity(route);
                    }

                    break;
            }
        }
    };
    private View.OnClickListener takePhotoClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (SharedPref.getIsNurse(getContext())) {
                takePhotoDialogFragment = TakePhotoDialogFragment.newInstance(call.getIdCall(), SharedPref.getTokenUser(getContext()));
                takePhotoDialogFragment.setTargetFragment(FragmentCallDetail.this, 1);
                takePhotoDialogFragment.show(getFragmentManager(), "TakePhoto");
            }
        }
    };
    public void changeStatus() {
        if (lastStatus.equals("")) {
            lastStatus = call.getStatusCall();
        }
//полный путь B - M - С - I - D - E - L -
        switch (lastStatus) {
            case Constants.STATUS_CALL_ASSIGNED_M:
                sendStatus = Constants.STATUS_CALL_START_C;
                Toast.makeText(ma, "Выезжаю на вызов", Toast.LENGTH_SHORT).show();
                break;
            case Constants.STATUS_CALL_START_C:
                sendStatus = Constants.STATUS_CALL_ARRIVED_I;
                Toast.makeText(ma, "Подъезжаю к больному", Toast.LENGTH_SHORT).show();
                break;
            case Constants.STATUS_CALL_ARRIVED_I:
                sendStatus = Constants.STATUS_CALL_COMPLETE_D;
                Toast.makeText(ma, "Приехал", Toast.LENGTH_SHORT).show();
                //if (SharedPref.getIsNurse(getContext())) {
                //    takePhoto.setVisibility(View.GONE);
                //}
                break;
            case Constants.STATUS_CALL_COMPLETE_D:
                sendStatus = Constants.STATUS_CALL_END_CLIENT_E;
                Toast.makeText(ma, "Завершил вызов", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(ma, "Отправился к больному", Toast.LENGTH_SHORT).show();
        }
        callStatusReq(sendStatus);
    }

    public FragmentCallDetail() {

    }

    private static FragmentCallDetail createFragment(DataCall data, boolean isHaveSelect) {
        FragmentCallDetail myFragment = new FragmentCallDetail();
        Bundle args = new Bundle();
        args.putParcelable("data", data);
        args.putBoolean("isHaveSelect", isHaveSelect);
        myFragment.setArguments(args);
        return myFragment;
    }

    public static void newInstance(DataCall data, final boolean isHaveSelect, final InstanceCreatedCallback callback) {
        if(data==null) {
            callback.onUpdate(createFragment(null, isHaveSelect));
        } else {
            CallUpdateChecker.checkIfUpdated(data, new CallUpdateChecker.UpdateCallback() {
                @Override
                public void onUpdate(DataCall call, boolean updated) {
                    callback.onUpdate(createFragment(call, isHaveSelect));
                }
            }, false);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        call = getArguments().getParcelable("data");
        isHaveSelect = getArguments().getBoolean("isHaveSelect");
        //for (int i = 0; i < call.getServiceList().size(); i++) {
        //    Log.d("Service", call.getServiceList().get(i).getIblockCode());
        //}
    }

    @BindView(R.id.call_id) TextView callId;
    @BindView(R.id.callClientName) TextView name;
    @BindView(R.id.callClientBirthday) TextView birthday;
    @BindView(R.id.city) TextView city;
    @BindView(R.id.street) TextView street;
    @BindView(R.id.house) TextView house;
    @BindView(R.id.str) TextView str;
    @BindView(R.id.floor) TextView floor;
    @BindView(R.id.korpus) TextView korpus;
    @BindView(R.id.podezd) TextView podezd;
    @BindView(R.id.kvartira) TextView kvartira;
    @BindView(R.id.callClientIntercom) TextView intercom;
    @BindView(R.id.time_to_wait_layout) ViewGroup timeToWaitLayout;
    @BindView(R.id.time_to_wait) TextView timeToWait;
    @BindView(R.id.contact_name) TextView contactName;
    @BindView(R.id.phones_layout) ViewGroup phonesLayout;
    @BindView(R.id.service_items) ViewGroup serviceItems;
    @BindView(R.id.procedure_items) ViewGroup procedureItems;
    @BindView(R.id.analyse_items) ViewGroup analyseItems;
    @BindView(R.id.other_items) ViewGroup otherItems;
    @BindView(R.id.payment_type_layout) ViewGroup paymentTypeLayout;
    @BindView(R.id.payment_type) TextView paymentType;
    @BindView(R.id.call_source_layout) ViewGroup callSourceLayout;
    @BindView(R.id.call_source) TextView callSource;
    @BindView(R.id.repeated) ViewGroup repeated;
    @BindView(R.id.payed) View payed;

    @BindView(R.id.rlCurrentCall) ViewGroup rlCurrentCall;
    @BindView(R.id.tv_writePatientCard) TextView writePatientCard;

    @BindView(R.id.sicklist_layout) ViewGroup sicklistLayout;
    @BindView(R.id.scroll_view) ScrollView scrollView;
    @BindView(R.id.take_photo_btn) ImageButton takePhoto;
    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_call_detail, container, false);

        LinearLayout rlAll = (LinearLayout) rootView.findViewById(R.id.rlDetailCurrentCall);
        viewFlipper = (ViewFlipper) rootView.findViewById(R.id.viewFlipperCallDetail);
        if (call == null) {
            viewFlipper.setDisplayedChild(Constants.VIEW_CONTENT);
            rlAll.setVisibility(View.GONE);
            return rootView;
        } else {
            rlAll.setVisibility(View.VISIBLE);
        }

        ButterKnife.setDebug(true);
        unbinder = ButterKnife.bind(this, rootView);

        statusChangeButton = (Button) rootView.findViewById(R.id.closeCall_button);

        updateFields();

        statusChangeButton.setOnClickListener(buttonClick);
        rootView.findViewById(R.id.showMap_button).setOnClickListener(buttonClick);
        rootView.findViewById(R.id.getDirection_button).setOnClickListener(buttonClick);
        takePhoto.setVisibility(View.GONE);
        takePhoto.setOnClickListener(takePhotoClick);
        showData();

        rootView.findViewById(R.id.button_reqError).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callStatusReq(sendStatus);
            }
        });

        if (!getResources().getBoolean(R.bool.isTablet)) {

            ((MainActivity) getActivity()).initActionBar(FRAGMENT_TAG, String.valueOf(call.getIdCall()));
            ((MainActivity) getActivity()).isStartScreens = false;

            ((MainActivity) getActivity()).toolbar.setNavigationIcon(Utils.getCustomDrawable(R.drawable.ic_back, getActivity()));
            ((MainActivity) getActivity()).toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getFragmentManager().popBackStack();
                }
            });
        }

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        FragmentCallsStart.swipeRefreshLayout.setEnabled(false);
                        break;
                    case MotionEvent.ACTION_UP:
                        FragmentCallsStart.swipeRefreshLayout.setEnabled(true);
                        break;
                }
                return false;
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
        if (context instanceof Activity){
            ma = (MainActivity) context;
        }
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);
        ma = null;
        super.onDetach();
    }

    private void updateServices(ViewGroup groupItems, List<DoctorService> servises) {
        if(servises != null && servises.size() > 0) {
            LayoutInflater.from(getContext()).inflate(R.layout.item_divider, groupItems, true);
            int itemCount = 0;
            boolean isService = false;
            for (DoctorService service : servises) {
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.item_doctor_service, groupItems, false);
                LinearLayout procedureLayout = ButterKnife.findById(view, R.id.procedureLayout);
                TextView price = ButterKnife.findById(view, R.id.price);
                TextView name = ButterKnife.findById(view, R.id.name);
                TextView iblockCode = ButterKnife.findById(view, R.id.iblockCode);
                if (service.getIblockCode().contains("services")) {
                    iblockCode.setText(getResources().getString(R.string.services));
                    name.setText(service.getName());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        name.setTextColor(ContextCompat.getColor(getActivity(), R.color.selected));
                        price.setTextColor(ContextCompat.getColor(getActivity(), R.color.selected));
                    } else {
                        name.setTextColor(getResources().getColor(R.color.selected));
                        price.setTextColor(getResources().getColor(R.color.selected));
                    }
                    price.setText(service.getPrice() + "Р");
                    isService = true;
                }
                else if (service.getIblockCode().contains("procedures")) {
                    iblockCode.setText(getResources().getString(R.string.procedures));
                    name.setText(service.getName());
                    price.setText(service.getPrice() + "Р");
                }
                else if (service.getIblockCode().contains("analysis")) {
                    iblockCode.setText(getResources().getString(R.string.analysis));
                    name.setText(service.getName());
                    price.setText(service.getPrice() + "Р");
                }
                else {
                    iblockCode.setText(getResources().getString(R.string.general));
                    name.setText(service.getName());
                    price.setText(service.getPrice() + "Р");
                }
                if (itemCount > 0 || service.getIblockCode().contains("services")) {
                    procedureLayout.removeView(iblockCode);
                    //iblockCode.setVisibility(View.INVISIBLE);
                }
                groupItems.addView(view, groupItems.getChildCount());
                itemCount++;
            }

            if(!call.getPayment().isPayed() && isService) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.item_total, groupItems, false);

                TextView name = ButterKnife.findById(view, R.id.name);
                name.setText("К оплате");
                TextView price = ButterKnife.findById(view, R.id.price);
                price.setText(call.getServicePrice() + "Р");

                groupItems.addView(view, groupItems.getChildCount());
            }
        }
    }

    private void updateFields() {
        if(call==null || callId==null) return;

        callId.setText(String.format(getString(R.string.call_id_format), call.getIdCall()));
        name.setText(call.getPatientFio());
        birthday.setText(getActivity().getString(R.string.birthday) + " " + call.getPatientBirthday());

        if(call.getAddress()!=null) {
            String sCity = call.getAddress().getCity();
            city.setText(sCity==null || sCity.isEmpty() ? "-" : sCity);
            String sStreet = call.getAddress().getStreet();
            street.setText(sStreet==null || sStreet.isEmpty() ? "-" : sStreet);
            String sHouse = call.getAddress().getHouse();
            house.setText(sHouse==null || sHouse.isEmpty() ? "-" : sHouse);
            String sStr = call.getAddress().getStructure();
            str.setText(sStr==null || sStr.isEmpty() ? "-" : sStr);
            String sFloor = call.getAddress().getFloor();
            floor.setText(sFloor==null || sFloor.isEmpty() ? "-" : sFloor);
            String sKorpus = call.getAddress().getBuilding();
            korpus.setText(sKorpus==null || sKorpus.isEmpty() ? "-" : sKorpus);
            String sPodezd = call.getAddress().getPorch();
            podezd.setText(sPodezd==null || sPodezd.isEmpty() ? "-" : sPodezd);
            String sKv = call.getAddress().getFlat();
            kvartira.setText(sKv==null || sKv.isEmpty() ? "-" : sKv);
            String sIntercom = call.getAddress().getIntercom();
            intercom.setText(String.format("Код домофона: %s", sIntercom == null || sIntercom.isEmpty() ? "-" : sIntercom));
        }

        if(Utils.canMakeTimeString(call)) {
            timeToWaitLayout.setVisibility(View.VISIBLE);

            timeToWait.setText(Utils.makeTimeString(call));
        } else {
            timeToWaitLayout.setVisibility(View.GONE);
        }

        contactName.setText(call.getFio());

        phonesLayout.removeAllViews();
        if(call.getPhoneList()!=null && call.getPhoneList().size()>0) {
            for(String phone : call.getPhoneList()) {
                TextView phoneView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_phone, phonesLayout, false);
                phoneView.setText(phone);
                phoneView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            CharSequence phone = ((TextView) v).getText();
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                            Toast.makeText(ma, "Ваше устройство не может звонить", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                phonesLayout.addView(phoneView);
            }
        }

        if(call.getPayment().isPayed()) {
            payed.setVisibility(View.VISIBLE);
            paymentTypeLayout.setVisibility(View.GONE);
        } else {
            payed.setVisibility(View.GONE);
            paymentTypeLayout.setVisibility(View.VISIBLE);
        }

        procedureItems.removeAllViews();
        serviceItems.removeAllViews();
        analyseItems.removeAllViews();
        otherItems.removeAllViews();
        if(call.getServiceList()!=null) {
            ArrayList<DoctorService> procedureServices = new ArrayList<DoctorService>();
            ArrayList<DoctorService> serviceServices = new ArrayList<DoctorService>();
            ArrayList<DoctorService> analyseServices = new ArrayList<DoctorService>();
            ArrayList<DoctorService> otherServices = new ArrayList<DoctorService>();
            for (DoctorService service : call.getServiceList()) {
                if (service.getIblockCode().contains("services")) {
                    serviceServices.add(service);
                } else if (service.getIblockCode().contains("procedures")) {
                    procedureServices.add(service);
                } else if (service.getIblockCode().contains("analysis")) {
                    analyseServices.add(service);
                } else {
                    otherServices.add(service);
                }
            }

            updateServices(procedureItems, procedureServices);
            updateServices(serviceItems, serviceServices);
            updateServices(analyseItems, analyseServices);
            updateServices(otherItems, otherServices);
        }

        /*
        procedureItems.removeAllViews();
        serviceItems.removeAllViews();
        analyseItems.removeAllViews();
        otherItems.removeAllViews();
        if(call.getServiceList()!=null) {
            LayoutInflater.from(getContext()).inflate(R.layout.item_divider, procedureItems, true);
            LayoutInflater.from(getContext()).inflate(R.layout.item_divider, serviceItems, true);
            LayoutInflater.from(getContext()).inflate(R.layout.item_divider, analyseItems, true);
            LayoutInflater.from(getContext()).inflate(R.layout.item_divider, otherItems, true);

            StringBuilder buf = new StringBuilder();

            for (DoctorService service : call.getServiceList()) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.item_doctor_service, paymentItems, false);
                LinearLayout procedureLayout = ButterKnife.findById(view, R.id.procedureLayout);
                TextView price = ButterKnife.findById(view, R.id.price);
                TextView name = ButterKnife.findById(view, R.id.name);
                TextView iblockCode = ButterKnife.findById(view, R.id.iblockCode);
                if (service.getIblockCode().contains("services")) {
                    iblockCode.setText(getResources().getString(R.string.services));
                    name.setText(service.getName());
                    price.setText(service.getPrice() + "Р");
                }
                else if (service.getIblockCode().contains("procedures")) {
                    iblockCode.setText(getResources().getString(R.string.analysis));
                    name.setText(service.getName());
                    price.setText("");
                }
                else if (service.getIblockCode().contains("analysis")) {
                    iblockCode.setText(getResources().getString(R.string.analysis));
                    name.setText(service.getName());
                    price.setText("");
                }
                else if (!service.getIblockCode().contains("services") || !service.getIblockCode().contains("procedures") || !service.getIblockCode().contains("analysis")) {
                    iblockCode.setText(getResources().getString(R.string.general));
                    name.setText(service.getName());
                    price.setText("");
                } else {
                    procedureLayout.setVisibility(View.GONE);
                }
                paymentItems.addView(view, paymentItems.getChildCount());
            }

            if(!call.getPayment().isPayed()) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.item_total, paymentItems, false);

                TextView name = ButterKnife.findById(view, R.id.name);
                name.setText("К оплате");
                TextView price = ButterKnife.findById(view, R.id.price);
                price.setText(call.getServicePrice() + "Р");

                paymentItems.addView(view, paymentItems.getChildCount());
            }
        }*/


        paymentType.setText(call.getPayment().getName());
        
        String s = call.getTrafficSource().getName();
        if(s==null || s.isEmpty()) {
            callSourceLayout.setVisibility(View.GONE);
        } else {
            callSourceLayout.setVisibility(View.VISIBLE);
            callSource.setText(call.getTrafficSource().getName());
        }

        repeated.setVisibility(call.getTrafficSource().isRecall() ? View.VISIBLE : View.GONE);

        //полный путь B - M - С - I - D - E - L -
        // B - Врач назначен
        // M - врач принял заявку
        // C - Врач в пути
        // I - Врач подъезжает
        // D - Врач прибыл к клиенту
        // E - Выполнен
        // L - Заполнена анкета

        if (!call.isCurrent()) {
            rlCurrentCall.setVisibility(View.GONE);
        } else {
            setTextButton(call.getStatusCall());
        }

        if (call.getStatusCall().equals(Constants.STATUS_CALL_END_CLIENT_E)) {
            rlCurrentCall.setVisibility(View.GONE);
            writePatientCard.setVisibility(View.VISIBLE);
        }

        if (!isHaveSelect && call.getStatusCall().equals(Constants.STATUS_CALL_ASSIGNED_M)) {
            rlCurrentCall.setVisibility(View.VISIBLE);
            statusChangeButton.setText(R.string.getCall);
        }


        /*Call<SicklistResponce> sicklistCall = ApiInstance.restApi.getSickList(SharedPref.getTokenApp(getContext()), SharedPref.getTokenUser(getContext()), call.getIdPatient());
        sicklistCall.enqueue(new Callback<SicklistResponce>() {
            @Override
            public void onResponse(Response<SicklistResponce> response, Retrofit retrofit) {
                if (sicklistLayout != null) {
                    sicklistLayout.removeAllViews();
                    if (response.isSuccess() && response.body() != null) {
                        //for(ModelDoctorSickList sickList : response.body().getData()) {
                        SicklistView sicklistView = new SicklistView(getContext());
                        sicklistView.fillData(response.body().getData());
                        sicklistLayout.addView(sicklistView);
                        //}
                    } else {
                        Utils.showToastError(response, getContext(), retrofit);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                sicklistLayout.removeAllViews();
            }
        });*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (deleteNotUploadedImages != null){
            ma.stopService(deleteNotUploadedImages);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(locationDialog!=null) locationDialog.dismiss();
        if(unbinder!=null) unbinder.unbind();

        if (!getResources().getBoolean(R.bool.isTablet)) {
            ((MainActivity) getActivity()).isStartScreens = true;
            ((MainActivity) getActivity()).showMenuItems();
            ((MainActivity) getActivity()).enableOpenRightMenu();
            ((MainActivity) getActivity()).enableOpenLeftMenu();
            ((MainActivity) getActivity()).toolbar.setNavigationIcon(Utils.getCustomDrawable(R.drawable.ic_menu, getActivity()));
            ((MainActivity) getActivity()).toolbar.setTitle(getResources().getString(R.string.calls));
            ((MainActivity) getActivity()).navigationClickOpen();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        checkForUpdate(false);
    }

    /**
     * изменение статуса вызова
     */
    private void callStatusReq(final String callStatus) {
        viewFlipper.setDisplayedChild(Constants.VIEW_PROGRESS);

        CallStatusSend statusSend = new CallStatusSend(SharedPref.getTokenUser(getActivity()), call.getIdCall(), callStatus);
        Call<ModelCallStatus> callRetrofit = ((MainActivity) getActivity()).restApi.getCallStatus(SharedPref.getTokenApp(getActivity()), statusSend);
        callRetrofit.enqueue(new Callback<ModelCallStatus>() {
            @Override
            public void onResponse(Response<ModelCallStatus> response, Retrofit retrofit) {
                //полный путь B - M - С - I - D - E - L -
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    if(lastStatus.equals(Constants.STATUS_CALL_START_C)) {
                        EventBus.getDefault().post(new ForceCordsSendEvent());
                        openTransferDialog(false, null);
                    } else if (lastStatus.equals(Constants.STATUS_CALL_COMPLETE_D)) {
                        deleteNotUploadedImages = new Intent(ma, DeleteNotUploadedImagesService.class);
                        deleteNotUploadedImages.putExtra("idCall", call.getIdCall());
                        ma.startService(deleteNotUploadedImages);
                    }
                    setTextButton(callStatus);
                    lastStatus = callStatus;
                    if (getActivity() != null) {
                        ((MainActivity) getActivity()).loadNewCalls();
                    }
                } else {
                    Utils.showErrorMessage(response, getActivity(), retrofit);
                }
                checkForUpdate(true);

                viewFlipper.setDisplayedChild(Constants.VIEW_CONTENT);
            }

            @Override
            public void onFailure(Throwable t) {
                checkForUpdate(true);

                viewFlipper.setDisplayedChild(Constants.VIEW_CONTENT);
                Toast.makeText(ma, R.string.statusNotChanged, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkForUpdate(boolean force) {
        if(call!=null) {
            CallUpdateChecker.checkIfUpdated(call, new CallUpdateChecker.UpdateCallback() {

                @Override
                public void onUpdate(DataCall pCall, boolean updated) {
                    call = pCall;
                    lastStatus = call.getStatusCall();
                    updateFields();
                }
            }, force);
        }
    }


    private void setTextButton(String callStatus) {
        switch (callStatus) {
            case Constants.STATUS_CALL_ASSIGNED_M:
                statusChangeButton.setText(R.string.getCall);
                break;
            case Constants.STATUS_CALL_START_C:
                // если текущий С статус, то если не ужел запрос автоматичски, показывать кнопку
                // по умолчанию кнопка скрыта, расоментить в шаред преф
//                    if (SharedPref.isSendingIStatus(getActivity())) {
//                        statusChangeButton.setVisibility(View.GONE);
//                    } else {
                statusChangeButton.setText(R.string.Iarrived);
//                    }
                break;
            case Constants.STATUS_CALL_ARRIVED_I:
                statusChangeButton.setText(R.string.Arrived);
                Intent intent = new Intent(GeoService.BROADCAST_GEO);
                intent.putExtra(MainActivity.PARAM_STATUS, GeoService.RADIUS_END);
                if(getContext() != null) {
                    getContext().sendBroadcast(intent);
                    if (SharedPref.getIsNurse(getContext())) {
                        takePhoto.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case Constants.STATUS_CALL_COMPLETE_D:
                statusChangeButton.setText(R.string.Complete);
                if (SharedPref.getIsNurse(getContext())) {
                    takePhoto.setVisibility(View.VISIBLE);
                }
                break;
            case Constants.STATUS_CALL_END_CLIENT_E:
                statusChangeButton.setVisibility(View.GONE);
                break;
        }
    }

    private void openTransferDialog(final boolean error, final Transfer pTransfer) {
        showProgress();
        Call<ReferenceResponse> transferTypeCall = ApiInstance.restApi.getReference(SharedPref.getTokenApp(getContext()), "transferType");
        transferTypeCall.enqueue(new Callback<ReferenceResponse>() {
            @Override
            public void onResponse(final Response<ReferenceResponse> refResponse, Retrofit retrofit) {
                if(!refResponse.isSuccess()) {
                    showData();
                    Toast.makeText(ma, "Ошибка при получении трансфера. Повторите позднее", Toast.LENGTH_SHORT).show();
                } else {
                    if(error) {
                        showData();
                        Toast.makeText(ma, "Ошибка при отправке трансфера. Повторите позднее", Toast.LENGTH_SHORT).show();
                    }
                    Call<TransferListResponse> transferList = ApiInstance.restApi.getTransferList(SharedPref.getTokenApp(getContext()), SharedPref.getTokenUser(getContext()), call.getIdCall());
                    transferList.enqueue(new Callback<TransferListResponse>() {
                        @Override
                        public void onResponse(Response<TransferListResponse> response, Retrofit retrofit) {

                            if(response.isSuccess() && response.body().getTransfers() != null && response.body().getTransfers().size() > 0) {
                                Transfer t = response.body().getTransfers().get(0);
                                if (t == null) {
                                    t = new Transfer();
                                    t.setCallId(call.getIdCall());
                                }
                                if(pTransfer != null) {
                                    t.setPriceFix(pTransfer.getPriceFix());
                                    t.setPriceParking(pTransfer.getPriceParking());
                                }

                                ArrayList<Reference> list = new ArrayList<>();
                                for(Reference reference : refResponse.body().getData()) {
                                    if(reference.isActive()) {
                                        list.add(reference);
                                    }
                                }
                                TransferDialogFragment.initAndShow(getFragmentManager(), t, list);
                                showData();
                            } else {
                                showData();
                                Toast.makeText(ma, "Ошибка при получении трансфера. Повторите позднее", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            showData();
                            t.printStackTrace();
                            Toast.makeText(ma, "Ошибка при получении трансфера. Повторите позднее", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                showData();
                Toast.makeText(ma, "Ошибка при получении трансфера. Повторите позднее", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void openRedirectionDialog(boolean error) {
        String code;
        if(Utils.isAdult(call)) {
            code = "redirectionSpecialistAdult";
        } else {
            code = "redirectionSpecialistChild";
        }
        showProgress();
        Call<ReferenceResponse> doctorsCall = ApiInstance.restApi.getReference(SharedPref.getTokenApp(getContext()), code);
        doctorsCall.enqueue(new Callback<ReferenceResponse>() {
            @Override
            public void onResponse(Response<ReferenceResponse> response, Retrofit retrofit) {
                if(response.isSuccess()) {
                    final ArrayList<Reference> doctors = new ArrayList<>(response.body().getData());
                    Call<ReferenceResponse> consentCall = ApiInstance.restApi.getReference(SharedPref.getTokenApp(getContext()), "redirectionConsentToRecord");
                    consentCall.enqueue(new Callback<ReferenceResponse>() {
                        @Override
                        public void onResponse(Response<ReferenceResponse> response, Retrofit retrofit) {
                            if(response.isSuccess()) {
                                ArrayList<Reference> consents = new ArrayList<>(response.body().getData());
                                RedirectionDialogFragment.initAndShow(getFragmentManager(), doctors, consents);
                            } else {
                                Toast.makeText(ma, "Ошибка при получении списка перенаправлений. Повторите позднее", Toast.LENGTH_SHORT).show();
                            }
                            showData();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            showData();
                            t.printStackTrace();
                            Toast.makeText(ma, "Ошибка при получении списка перенаправлений. Повторите позднее", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    showData();
                    Toast.makeText(ma, "Ошибка при получении списка перенаправлений. Повторите позднее", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                showData();
                t.printStackTrace();
                Toast.makeText(ma, "Ошибка при получении списка перенаправлений. Повторите позднее", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final TransferInfoEvent e) {
        showProgress();
        Call<ReferenceResponse> routeTypeCall = ApiInstance.restApi.getReference(SharedPref.getTokenApp(getContext()), "transferRoute");
        routeTypeCall.enqueue(new Callback<ReferenceResponse>() {
            @Override
            public void onResponse(Response<ReferenceResponse> response, Retrofit retrofit) {
                if(!response.isSuccess()) {
                    Toast.makeText(ma, "Данные трансфера могли не сохраниться, пожалуйста, проверьте в ЭКВ", Toast.LENGTH_SHORT).show();
                    //openTransferDialog(true, e.getTransfer());
                } else {
                    Reference r = response.body().getData().get(0);
                    e.getTransfer().setRouteId(r.getId());
                    e.getTransfer().setRouteValue(r.getValue());

                    e.getTransfer().setToken(SharedPref.getTokenUser(getContext()));
                    final TransferPost p = new TransferPost(SharedPref.getTokenUser(getContext()), e.getTransfer());
                    Call<TransferPost> call = ApiInstance.restApi.postStatus(SharedPref.getTokenApp(getContext()), p);
                    call.enqueue(new Callback<TransferPost>() {
                        @Override
                        public void onResponse(Response<TransferPost> response, Retrofit retrofit) {
                            showData();
                            if(!response.isSuccess()) {
                                Toast.makeText(ma, "Данные трансфера могли не сохраниться, пожалуйста, проверьте в ЭКВ", Toast.LENGTH_SHORT).show();
                                //openTransferDialog(true, p.getTransfer());
                            } else {
                                Toast.makeText(ma, "Данные трансфера успешно сохранены", Toast.LENGTH_SHORT).show();
                                //changeStatus();
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            t.printStackTrace();
                            showData();
                            Toast.makeText(ma, "Данные трансфера могли не сохраниться, пожалуйста, проверьте в ЭКВ", Toast.LENGTH_SHORT).show();
                            //openTransferDialog(true, p.getTransfer());
                        }
                    });
                }
            }

            @Override
            public void onFailure(Throwable t) {
                showData();
                t.printStackTrace();
                Toast.makeText(ma, "Данные трансфера могли не сохраниться, пожалуйста, проверьте в ЭКВ", Toast.LENGTH_SHORT).show();
                //openTransferDialog(true, e.getTransfer());
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TakePhotoCommandEvent e) {
        if (lastStatus.equals("")) {
            lastStatus = call.getStatusCall();
        }
        switch(lastStatus) {
            case Constants.STATUS_CALL_ARRIVED_I:
                if (e.getCommand() == "CONTINUE") {
                    changeStatus();
                } else if (e.getCommand() == "SKIP") {
                    changeStatus();
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RedirectionInfoEvent e) {
        List<RedirectionShort> redirections = new ArrayList<>();
        for(Pair<Reference, Reference> pair : e.getResultList()) {
            if(pair.first.getValue().equals("-")) continue;
            RedirectionShort redirection = new RedirectionShort();
            redirection.setCallId(call.getIdCall());
            redirection.setSpecialistId(pair.first.getId());
            redirection.setConsentToRecordId(pair.second.getId());
            redirections.add(redirection);
        }
        sendRedirections(redirections);
    }

    private void sendRedirections(final List<RedirectionShort> redirections) {
        viewFlipper.setDisplayedChild(Constants.VIEW_PROGRESS);
        if(redirections.size()==0) {
            changeStatus();
        } else {
            RedirectionPost post = new RedirectionPost(SharedPref.getTokenUser(getContext()), redirections.get(0));
            Call<DefaultResponse> redirectionPost = ApiInstance.restApi.postRedirection(SharedPref.getTokenApp(getContext()), post);
            redirectionPost.enqueue(new Callback<DefaultResponse>() {
                @Override
                public void onResponse(Response<DefaultResponse> response, Retrofit retrofit) {
                    if(response.isSuccess()) {
                        redirections.remove(0);
                        sendRedirections(redirections);
                    } else {
                        redirections.remove(0);
                        sendRedirections(redirections);
                        //Toast.makeText(getContext(), "Ошибка при отправке списка перенаправлений. Повторите позднее", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    redirections.remove(0);
                    sendRedirections(redirections);
                    t.printStackTrace();
                    //Toast.makeText(getContext(), "Ошибка при отправке списка перенаправлений. Повторите позднее", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
