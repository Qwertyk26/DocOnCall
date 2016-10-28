package ru.handh.doctor.ui;

import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import ru.handh.doctor.R;
import ru.handh.doctor.io.db.Address;
import ru.handh.doctor.io.db.Doctor;
import ru.handh.doctor.io.network.responce.ModelDoctor;
import ru.handh.doctor.io.network.send.SettingsNameSend;
import ru.handh.doctor.ui.controls.CustomMapView;
import ru.handh.doctor.ui.main.MainActivity;
import ru.handh.doctor.utils.Log4jHelper;
import ru.handh.doctor.utils.SharedPref;
import ru.handh.doctor.utils.Utils;

/**
 * Created by sgirn on 06.10.2015.
 * фрагмент настроек
 */
public class FragmentSettings extends ParentFragment implements MainActivity.ClickItemMenu {

    private Handler handler = new Handler(Looper.myLooper());

    public final static String FRAGMENT_TAG = "FragmentSettings";
    org.apache.log4j.Logger log;
    private final Pattern sPattern
            = Pattern.compile("^([a-z,A-Z,а-я,А-Я,\\s,\\-,']{0,25})?([0-9]?)?$");
    private EditText name, middleName, surname, nameEdit, middleNameEdit, surnameEdit, city, street, house;
    private TextView addressTitle, addressValue;
    private Button editButton, myLocationButton, saveButton, cancelButton;
    private View editButtonsLayout, editFieldsLayout, infoLayout;
    private CustomMapView mapView;
    private GoogleMap map;

    private Marker positionMarker;
    private boolean isFirstLoad = true;
    private boolean isEditing = false;
    private ImageView ivPhoto;

    private Doctor doctor;
    private ModelDoctor.Address address;

    private MainActivity mainActivity;
    private InputMethodManager imm;
    private View.OnClickListener startEditing = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            enableEditing();
            log.info(FRAGMENT_TAG + " enable editing clicked");
        }
    };
    private View.OnClickListener cancelEditing = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            undoEditing();
            log.info(FRAGMENT_TAG + " undo editing clicked");
        }
    };

    private View.OnClickListener saveEditing = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveInfo();
            log.info(FRAGMENT_TAG + " save info clicked");
        }
    };


    public FragmentSettings() {
    }

    public static FragmentSettings newInstance() {
        return new FragmentSettings();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, null);

        ivPhoto = (ImageView) rootView.findViewById(R.id.settingPhoto_iv);
        name = (EditText) rootView.findViewById(R.id.settingsName);
        middleName = (EditText) rootView.findViewById(R.id.settingsMiddleName);
        surname = (EditText) rootView.findViewById(R.id.settingsSurname);
        nameEdit = (EditText) rootView.findViewById(R.id.settingsNameEdit);
        middleNameEdit = (EditText) rootView.findViewById(R.id.settingsMiddleNameEdit);
        surnameEdit = (EditText) rootView.findViewById(R.id.settingsSurnameEdit);
        city = (EditText) rootView.findViewById(R.id.settingsCity);
        street = (EditText) rootView.findViewById(R.id.settingsStreet);
        house = (EditText) rootView.findViewById(R.id.settingsHouse);
        addressTitle = (TextView) rootView.findViewById(R.id.settingsAddressTitle);
        addressValue = (TextView) rootView.findViewById(R.id.settingsAddressValue);
        editButton = (Button) rootView.findViewById(R.id.edit_button);
        saveButton = (Button) rootView.findViewById(R.id.profile_save_button);
        myLocationButton = (Button) rootView.findViewById(R.id.my_location_button);
        cancelButton = (Button) rootView.findViewById(R.id.profile_cancel_button);
        editButtonsLayout = rootView.findViewById(R.id.edit_buttons_layout);
        infoLayout = rootView.findViewById(R.id.info_layout);
        editFieldsLayout = rootView.findViewById(R.id.edit_fields_layout);
        mapView = (CustomMapView) rootView.findViewById(R.id.mapView);
        viewFlipper = (ViewFlipper) rootView.findViewById(R.id.viewFlipperSettings);
        log = Log4jHelper.getLogger(FRAGMENT_TAG);
        isEditing = savedInstanceState != null && savedInstanceState.getBoolean("isEditing");

        setUpMap(savedInstanceState);

        editButton.setOnClickListener(startEditing);
        cancelButton.setOnClickListener(cancelEditing);
        saveButton.setOnClickListener(saveEditing);

        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positionMarker != null && map != null) {
                    Location location = map.getMyLocation();
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    positionMarker.setPosition(latLng);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
                    map.animateCamera(cameraUpdate);
                    log.info(FRAGMENT_TAG + " mylocationButton clicked");
                    getAddress(positionMarker.getPosition().latitude, positionMarker.getPosition().longitude);
                }
            }
        });

        nameEdit.addTextChangedListener(new TextWatcher() {

            private CharSequence mText;

            private boolean isValid(CharSequence s) {
                return sPattern.matcher(s).matches();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                mText = isValid(s) ? s.toString() : "";
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isValid(s)) {
                    name.setText(mText);
                }
                mText = null;
            }
        });

        middleNameEdit.addTextChangedListener(new TextWatcher() {

            private CharSequence mText;

            private boolean isValid(CharSequence s) {
                return sPattern.matcher(s).matches();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                mText = isValid(s) ? s.toString() : "";
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isValid(s)) {
                    surname.setText(mText);
                }
                mText = null;
            }
        });

        surnameEdit.addTextChangedListener(new TextWatcher() {

            private CharSequence mText;

            private boolean isValid(CharSequence s) {
                return sPattern.matcher(s).matches();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                mText = isValid(s) ? s.toString() : "";
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isValid(s)) {
                    surname.setText(mText);
                }
                mText = null;
            }
        });


        mainActivity = ((MainActivity) getActivity());

        mainActivity.initActionBar(FRAGMENT_TAG, getResources().getString(R.string.settings));

        if (savedInstanceState != null && savedInstanceState.getBoolean("isEditing")) {

            enableEditing();

            if(address==null) address = new ModelDoctor().new Address();
            address.setCity(savedInstanceState.getString("city"));
            address.setStreet(savedInstanceState.getString("street"));
            address.setHouse(savedInstanceState.getString("house"));
            address.setLongitude(savedInstanceState.getDouble("lng"));
            address.setLatitude(savedInstanceState.getDouble("lat"));

            name.setText(savedInstanceState.getString("name"));
            middleName.setText(savedInstanceState.getString("middleName"));
            surname.setText(savedInstanceState.getString("surname"));
            nameEdit.setText(savedInstanceState.getString("name"));
            middleNameEdit.setText(savedInstanceState.getString("middleName"));
            surnameEdit.setText(savedInstanceState.getString("surname"));

            setAddressValueString();
        }
        initDoctorInfo(savedInstanceState);

        showData();

        rootView.findViewById(R.id.button_reqError).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //changeNameReq();
            }
        });


        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion == Build.VERSION_CODES.JELLY_BEAN_MR1) {
            name.setGravity(Gravity.START);
            surname.setGravity(Gravity.START);

        }
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (isFirstLoad) {
                    if (hasFocus) {

                        imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);
                    }
                    isFirstLoad = false;
                }

            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isEditing", isEditing);
        if (isEditing) {
            outState.putString("name", nameEdit.getText().toString());
            outState.putString("middleName", middleNameEdit.getText().toString());
            outState.putString("surname", surnameEdit.getText().toString());
            outState.putString("city", city.getText().toString());
            outState.putString("street", street.getText().toString());
            outState.putString("house", house.getText().toString());
            outState.putDouble("lat", positionMarker.getPosition().latitude);
            outState.putDouble("lng", positionMarker.getPosition().longitude);
            outState.putFloat("mapZoom", map.getCameraPosition().zoom);
        }
    }

    private void initDoctorInfo(Bundle savedInstanceState) {

        mainActivity.dbWork.setDocDataToInterface(ivPhoto, name, surname, middleName);

        doctor = mainActivity.dbWork.getDoctor();

        nameEdit.setText(doctor.getName());
        middleNameEdit.setText(doctor.getMiddleName());
        surnameEdit.setText(doctor.getSurname());

        address = new ModelDoctor().new Address();
        Address doctorAddress = doctor.getAddress();

        if (doctorAddress != null) {
            address.setId(doctorAddress.getId());
            address.setCity(doctorAddress.getCity());
            address.setStreet(doctorAddress.getStreet());
            address.setHouse(doctorAddress.getHouse());

            address.setBuilding(doctorAddress.getBuilding());
            address.setFlat(doctorAddress.getFlat());
            address.setFloor(doctorAddress.getFloor());
            address.setIntercom(doctorAddress.getIntercom());
            address.setPorch(doctorAddress.getPorch());
            address.setStructure(doctorAddress.getStructure());

            address.setLongitude(doctorAddress.getLongitude());
            address.setLatitude(doctorAddress.getLatitude());
        }

        if (address == null ||
                (address.getCity() == null &&
                        address.getStreet() == null &&
                        address.getHouse() == null &&
                        address.getLatitude() == 0 &&
                        address.getLongitude() == 0)) {
            addressValue.setText(R.string.address_not_set);

            map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    setupMarker(location.getLatitude(), location.getLongitude());
                    map.setOnMyLocationChangeListener(null);
                }});
            }
            else {
            String addressString = setAddressValueString();

            if (address.getLatitude() == 0 && address.getLongitude() == 0 && !addressString.isEmpty()) {
                getCoordinates(addressString);
            }

            if (address.getLongitude() != 0 && address.getLatitude() != 0) {
                setupMarker(address.getLatitude(), address.getLongitude());
                if (address.getCity() == null ||
                        address.getStreet() == null ||
                        address.getHouse() == null) {
                    getAddress(address.getLatitude(), address.getLongitude());
                }
            }
        }
    }

    @NonNull
    private String setAddressValueString() {
        String addressString = "";
        if (address.getCity() != null) {
            city.setText(address.getCity());
            addressString += address.getCity();
        }
        if (address.getStreet() != null) {
            street.setText(address.getStreet());
            addressString += ", " + address.getStreet();
        }
        if (address.getHouse() != null) {
            house.setText(address.getHouse());
            addressString += ", д." + address.getHouse();
        }

        addressValue.setText(addressString);
        return addressString;
    }

    private void setUpMap(Bundle savedInstanceState) {

        mapView.onCreate(savedInstanceState);
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        MapsInitializer.initialize(this.getActivity());

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (isEditing && positionMarker != null) {
                    positionMarker.setPosition(latLng);
                    getAddress(latLng.latitude, latLng.longitude);
                }
            }
        });

        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                getAddress(marker.getPosition().latitude, marker.getPosition().longitude);
                log.info(FRAGMENT_TAG + " marker map end draging");
            }
        });
    }

    private void setupMarker(double latitude, double longitude) {

        LatLng position = new LatLng(latitude, longitude);
        boolean moveCamera = !isEditing;
        if (positionMarker == null) {
            positionMarker = map.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .position(position)
                    .draggable(isEditing));
        } else {
            moveCamera = !isEditing && !positionMarker.getPosition().equals(position);
            if (moveCamera) {
                positionMarker.setPosition(position);
            }
            positionMarker.setDraggable(isEditing);
        }
        if (moveCamera) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, 16);
            map.animateCamera(cameraUpdate);
        }
    }

    private void enableEditing() {
        isEditing = true;
        editButton.setVisibility(View.GONE);
        editButtonsLayout.setVisibility(View.VISIBLE);
        if (positionMarker != null)
            positionMarker.setDraggable(true);
        editFieldsLayout.setVisibility(View.VISIBLE);
        infoLayout.setVisibility(View.GONE);
    }

    private void undoEditing() {
        isEditing = false;
        editButton.setVisibility(View.VISIBLE);
        editButtonsLayout.setVisibility(View.GONE);
        if (positionMarker != null)
            positionMarker.setDraggable(false);
        editFieldsLayout.setVisibility(View.GONE);
        infoLayout.setVisibility(View.VISIBLE);
        initDoctorInfo(null);
    }

    private void saveInfo() {
        isEditing = false;
        editButton.setVisibility(View.VISIBLE);
        editButtonsLayout.setVisibility(View.GONE);
        if (positionMarker != null)
            positionMarker.setDraggable(false);
        editFieldsLayout.setVisibility(View.GONE);
        infoLayout.setVisibility(View.VISIBLE);
        address.setCity(city.getText().toString());
        address.setStreet(street.getText().toString());
        address.setHouse(house.getText().toString());
        changeProfileRequest();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity.dbWork.setDocDataToInterface(ivPhoto, null, null, null);

    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void clickSaveSettings() {
        //changeNameReq();
    }

    private void changeProfileRequest() {
        showProgress();

        String nameAll = name.getText().toString().trim();
        String nameS;
        String secondName;
        if (nameAll.contains(" ")) {
            nameS = nameAll.substring(0, nameAll.indexOf(" "));
            secondName = nameAll.substring(nameAll.indexOf(" "), nameAll.length());
        } else {
            nameS = nameAll.substring(0, nameAll.length());
            secondName = "";
        }

        SettingsNameSend settingsNameSend = new SettingsNameSend(SharedPref.getTokenUser(getActivity()),
                nameS,
                surname.getText().toString(),
                secondName, address);


        Call<ModelDoctor> call = mainActivity.restApi.getSettings(SharedPref.getTokenApp(getActivity()), settingsNameSend);
        call.enqueue(new Callback<ModelDoctor>() {
            @Override
            public void onResponse(Response<ModelDoctor> response, Retrofit retrofit) {

                showData();

                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mainActivity.dbWork.doctorWriteDatabase(response);
                    mainActivity.setDocDataToInterface();
                    initDoctorInfo(null);

                    Toast.makeText(getActivity(), R.string.dateCange, Toast.LENGTH_SHORT).show();
                } else {
                    Utils.showErrorMessage(response, getActivity(), retrofit);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                showError();
            }
        });
    }

    private void getAddress(final double lat, final double lng) {
        OkHttpClient client = new OkHttpClient();

        String addressRequest = String
                .format(Locale.ENGLISH, "http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language="
                        + "ru", lat, lng);

        Request request = new Request.Builder()
                .url(addressRequest)
                .build();


        client.newCall(request).enqueue(new com.squareup.okhttp.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                if (response.isSuccessful()) {
                    String body = response.body().string();
                    address = new ModelDoctor().new Address();
                    try {
                        JSONObject jsonObject = new JSONObject(body);
                        JSONArray components = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("address_components");
                        for (int i = 0; i < components.length(); i++) {
                            JSONObject component = components.getJSONObject(i);
                            JSONArray types = component.getJSONArray("types");
                            ArrayList<String> stringTypes = new ArrayList<String>(types.length());

                            for (int j = 0; j < types.length(); j++) {
                                stringTypes.add(types.getString(j));
                            }

                            if (stringTypes.contains("street_number"))
                                address.setHouse(component.getString("short_name"));
                            if (stringTypes.contains("route"))
                                address.setStreet(component.getString("short_name"));
                            if (stringTypes.contains("locality") && stringTypes.contains("political"))
                                address.setCity(component.getString("short_name"));
                        }
                        address.setLatitude(lat);
                        address.setLongitude(lng);
                        updateUserAddressFields();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void getCoordinates(final String addressString) {
        OkHttpClient client = new OkHttpClient();

        String addressRequest = null;
        try {
            addressRequest = "http://maps.google.com/maps/api/geocode/json?address="
                    + URLEncoder.encode(addressString, "UTF-8") + "&ka&sensor=false";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (addressRequest == null)
            return;

        Request request = new Request.Builder()
                .url(addressRequest)
                .build();


        client.newCall(request).enqueue(new com.squareup.okhttp.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {

                        String body = response.body().string();
                        JSONObject jsonObject = new JSONObject(body);

                        final double lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                                .getJSONObject("geometry").getJSONObject("location")
                                .getDouble("lng");

                        final double lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                                .getJSONObject("geometry").getJSONObject("location")
                                .getDouble("lat");
                        address.setLatitude(lat);
                        address.setLongitude(lng);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (!isEditing && positionMarker != null) {
                                    LatLng latLng = new LatLng(lat, lng);
                                    positionMarker.setPosition(latLng);
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void updateUserAddressFields() {
        if (address == null)
            return;
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                city.setText(address.getCity());
                street.setText(address.getStreet());
                house.setText(address.getHouse());
                setAddressValueString();
            }
        });
    }
}
