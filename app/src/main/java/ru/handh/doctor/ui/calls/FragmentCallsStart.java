package ru.handh.doctor.ui.calls;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ViewFlipper;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;

import retrofit.Response;
import retrofit.Retrofit;
import ru.handh.doctor.MyApplication;
import ru.handh.doctor.R;
import ru.handh.doctor.io.network.ReqForCalls;
import ru.handh.doctor.io.network.responce.Transfer;
import ru.handh.doctor.io.network.responce.calls.Address;
import ru.handh.doctor.io.network.responce.calls.DataCall;
import ru.handh.doctor.io.network.responce.calls.DoctorPayment;
import ru.handh.doctor.io.network.responce.calls.DoctorService;
import ru.handh.doctor.io.network.responce.calls.DoctorTrafficSource;
import ru.handh.doctor.io.network.responce.calls.ModelCallsReq;
import ru.handh.doctor.model.CallDataForGeoService;
import ru.handh.doctor.ui.ParentFragment;
import ru.handh.doctor.ui.main.CallsOnResponce;
import ru.handh.doctor.ui.main.MainActivity;
import ru.handh.doctor.utils.Constants;
import ru.handh.doctor.utils.Log;
import ru.handh.doctor.utils.SharedPref;
import ru.handh.doctor.utils.Utils;

/**
 * Created by sgirn on 05.10.2015.
 * стартовый фрагмент для главного экрана, содержит список вызовов и детальную ин-фу
 */
public class FragmentCallsStart extends ParentFragment implements FragmentCallList.Callbacks, CallsOnResponce {

    public final static String FRAGMENT_TAG = "FragmentCallsStart";

    private boolean mTwoPane;
    private ArrayList<DataCall> dataCall = new ArrayList<>();
    private ReqForCalls rc;
    private boolean isHaveSelect;
    private boolean loading = false;

    public static SwipeRefreshLayout swipeRefreshLayout;

    public FragmentCallsStart() {
    }

    public static FragmentCallsStart newInstance() {
        return new FragmentCallsStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rooView = inflater.inflate(R.layout.fragment_list_calls, null);
        swipeRefreshLayout = (SwipeRefreshLayout) rooView.findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        viewFlipper = (ViewFlipper) rooView.findViewById(R.id.viewFlipperCalls);
        if (rooView.findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, list items should be given the
            // 'activated' state when touched.

        }

        rc = new ReqForCalls();


        if(MyApplication.isSessionStateReceived) {
            if (savedInstanceState == null) {
                refresh();
            } else {
                dataCall = savedInstanceState.getParcelableArrayList("ActivCalls");
                FragmentCallList fragmentCallList = (FragmentCallList) getChildFragmentManager().findFragmentById(R.id.item_list);
                fragmentCallList.setActivateOnItemClick(true, dataCall);
                showData();
            }
        } else {
            showEmpty();
        }

        ((MainActivity) getActivity()).initActionBar(FRAGMENT_TAG, getResources().getString(R.string.calls));

        rooView.findViewById(R.id.button_reqError).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rc.callsReq(Constants.REQ_CALLS_ACTIVE, FragmentCallsStart.this, getActivity());
                ((MainActivity) getActivity()).changeDocStatusReq(1);
            }
        });

        //EventBus.getDefault().post(new SessionButtonVisibilityEvent(true));

        return rooView;
    }

    @Override
    public void onDestroy() {
        //EventBus.getDefault().post(new SessionButtonVisibilityEvent(false));

        super.onDestroy();
    }

    public void showEmpty() {
        FragmentCallList fragmentCallList = (FragmentCallList) getChildFragmentManager().findFragmentById(R.id.item_list);
        fragmentCallList.setActivateOnItemClick(true, null);
        showData();
    }

    /**
     * Callback method from {@link FragmentCallList.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        int selectedID = Integer.valueOf(id) - 1;

        if(!loading) {
            loading = true;

            if (mTwoPane) {
                // In two-pane mode, show the detail view in this activity by
                // adding or replacing the detail fragment using a
                // fragment transaction

                FragmentCallDetail.InstanceCreatedCallback callback = new FragmentCallDetail.InstanceCreatedCallback() {
                    @Override
                    public void onUpdate(FragmentCallDetail fragment) {
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment).addToBackStack("FragmentCallDetail").commitAllowingStateLoss();
                        loading = false;
                    }
                };

                if (dataCall.size() != 0) {
                    FragmentCallDetail.newInstance(dataCall.get(selectedID), isHaveSelect, callback);
                } else {
                    FragmentCallDetail.newInstance(null, isHaveSelect, callback);
                }
            } else {
                // In single-pane mode, simply start the detail activity
                // for the selected item ID.
//            Intent detailIntent = new Intent(getActivity(), ActivityCallDetail.class);
//            detailIntent.putExtra("dataCall", dataCall.get(selectedID));
//            detailIntent.putExtra("isHaveSelect", isHaveSelect);
//            detailIntent.putExtra("location", ((MainActivity) getActivity()).currentLocation);
//            startActivity(detailIntent);
                if(selectedID<dataCall.size()) FragmentCallDetail.newInstance(dataCall.get(selectedID), isHaveSelect, new FragmentCallDetail.InstanceCreatedCallback() {
                    @Override
                    public void onUpdate(FragmentCallDetail fragment) {
                        if(getFragmentManager()!=null) getFragmentManager().beginTransaction()
                                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in,
                                        android.R.anim.fade_out)
                                .add(R.id.container, fragment).addToBackStack(FragmentCallDetail.FRAGMENT_TAG).commit();
                        loading = false;
                    }
                });
                else {
                    loading = false;
                }
            }
        }
    }


    private void refresh() {
        if(swipeRefreshLayout!=null) swipeRefreshLayout.setRefreshing(true);
        if(getActivity()!=null) ((MainActivity) getActivity()).loadNewCalls();
    }

    public void loadActiveOnlyCalls() {
        rc.callsReq(Constants.REQ_CALLS_ACTIVE, this, getActivity());
    }

    @Override
    public void callResponse(Response<ModelCallsReq> response, Retrofit retrofit) {
        if(swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
        if (response.code() == HttpURLConnection.HTTP_OK) {
           ArrayList<DataCall> dataCalls = new ArrayList<>();
            DataCall _dDataCall = new DataCall();
            _dDataCall.setIdCall(413752);
            _dDataCall.setIdPatient(413750);
            _dDataCall.setIdPatient(8701);
            _dDataCall.setFio("Иванов Иван Иванович");
            _dDataCall.setPatientBirthday("25.06.2014");
            _dDataCall.setStatusCall(Constants.STATUS_CALL_ARRIVED_I);
            Address address = new Address();
            address.setLatitude(Double.parseDouble("55.776406"));
            address.setLongitude(Double.parseDouble("37.676915"));
            address.setCity("Москва");
            DoctorTrafficSource doctorTrafficSource = new DoctorTrafficSource();
            doctorTrafficSource.setName("Скандинавский центр здоровья");
            _dDataCall.setTrafficSource(doctorTrafficSource);
            _dDataCall.setAddress(address);
            DoctorPayment doctorPayment = new DoctorPayment();
            doctorPayment.setId(1);
            doctorPayment.setName("Наличные");
            doctorPayment.setPayed(true);
            _dDataCall.setPayment(doctorPayment);
            ArrayList<DoctorService> serviceArrayList = new ArrayList<>();
            DoctorService doctorService = new DoctorService();
            doctorService.setName("Вызов врача");
            doctorService.setPrice("2000.00");
            doctorService.setIblockCode("analysis");
            doctorService.setId(1);
            serviceArrayList.add(doctorService);
            _dDataCall.setServiceList(serviceArrayList);
            dataCalls.add(_dDataCall);
            dataCall = (ArrayList<DataCall>) response.body().data;

            isHaveSelect = false;

            for (int i = 0; i < dataCall.size(); i++) {
                if (dataCall.get(i).getStatusCall().equals(Constants.STATUS_CALL_START_C) ||
                        dataCall.get(i).getStatusCall().equals(Constants.STATUS_CALL_ARRIVED_I) ||
                        dataCall.get(i).getStatusCall().equals(Constants.STATUS_CALL_COMPLETE_D)
                        ) {
                    isHaveSelect = true;
                    dataCall.get(i).setIsCurrent(true);

                    if (dataCall.get(i).getStatusCall().equals(Constants.STATUS_CALL_START_C)) {
                        CallDataForGeoService cdm = new CallDataForGeoService();
                        cdm.setIdCall(dataCall.get(i).getIdCall());
                        cdm.setLat(dataCall.get(i).getAddress().getLatitude());
                        cdm.setLon(dataCall.get(i).getAddress().getLongitude());

                        SharedPref.setCallDataMini(cdm, getActivity());
                        ((MainActivity) getActivity()).startLocationService();
                    }
                    break;
                }

            }

            FragmentCallList fragmentCallList = (FragmentCallList) getChildFragmentManager().findFragmentById(R.id.item_list);
            if (dataCall != null)
                fragmentCallList.setActivateOnItemClick(true, dataCall);

        } else {
            Utils.showErrorMessage(response, getActivity(), retrofit);
        }

//        if (((MainActivity) getActivity()).isFirst) {
//            ((MainActivity) getActivity()).isFirst = false;
//            ((MainActivity) getActivity()).loadNewCalls();
//        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList("ActivCalls", dataCall);
        super.onSaveInstanceState(savedInstanceState);
    }

}