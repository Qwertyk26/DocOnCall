package ru.handh.doctor.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ViewFlipper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.net.HttpURLConnection;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import ru.handh.doctor.MyApplication;
import ru.handh.doctor.ParentActivity;
import ru.handh.doctor.R;
import ru.handh.doctor.gcm.QuickstartPreferences;
import ru.handh.doctor.gcm.RegistrationIntentService;
import ru.handh.doctor.io.db.DBWork;
import ru.handh.doctor.io.network.ApiInstance;
import ru.handh.doctor.io.network.RestApi;
import ru.handh.doctor.io.network.responce.ModelAuth;
import ru.handh.doctor.io.network.responce.ModelDoctor;
import ru.handh.doctor.io.network.send.AuthSend;
import ru.handh.doctor.io.network.send.DoctorSend;
import ru.handh.doctor.ui.main.MainActivity;
import ru.handh.doctor.utils.Constants;
import ru.handh.doctor.utils.Log4jHelper;
import ru.handh.doctor.utils.SharedPref;
import ru.handh.doctor.utils.Utils;

/**
 * Created by sgirn on 06.10.2015.
 * экран авторизации
 */
public class LoginActivity extends ParentActivity {
    public final static String TAG = "LoginActivity";
    static final String STATE_GCM = "gcmToken";
    static final String STATE_APP_TOKEN = "appTOken";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final int REQ_TOKEN = 0;
    private final int REQ_DOC_DATA = 1;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private int numberReq;
    private EditText login, password;
    private String appToken;
    private RestApi restApi = ApiInstance.defaultService(RestApi.class);
    private String tokenGCM = "";
    private DBWork dbWork;
    org.apache.log4j.Logger log;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbWork = new DBWork(LoginActivity.this);

        setContentView(R.layout.activity_login);

        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipperLogin);
        login = (EditText) findViewById(R.id.loginEmail);
        password = (EditText) findViewById(R.id.loginPassword);
        Button errorButton = (Button) findViewById(R.id.button_reqError);
        Button enter = (Button) findViewById(R.id.loginEnter_button);

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion == Build.VERSION_CODES.JELLY_BEAN_MR1) {
            login.setGravity(Gravity.START);
            password.setGravity(Gravity.START);
        }

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log.info(TAG + " enter button clicked");
                if (login.getText().toString().isEmpty()) {
                    login.setError(getString(R.string.writeLogin));
                    return;
                }
                if (password.getText().toString().isEmpty()) {
                    password.setError(getString(R.string.writePass));
                    return;
                }

                loadDoctorData(login.getText().toString(), password.getText().toString());
            }
        });

        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log.info(TAG + " error button clicked");
                if (numberReq == REQ_TOKEN) {
                    loadTokenApp(false);
                } else if (numberReq == REQ_DOC_DATA) {
                    loadDoctorData(login.getText().toString(), password.getText().toString());
                }
            }
        });

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setTokenGCM(context);
            }
        };

        String token = SharedPref.getTokenApp(LoginActivity.this);
        String appVersion = SharedPref.getAppVersion(LoginActivity.this);

        if(token != null && appVersion == null){
            SharedPref.setAppVersion(Utils.getPackageVersion(LoginActivity.this), LoginActivity.this);
        }

        if (savedInstanceState == null) {
            loadTokenApp(false);
        } else if (savedInstanceState.getBoolean("isBadToken", false)) {
            loadTokenApp(true);
        } else {
            tokenGCM = savedInstanceState.getString(STATE_GCM);
            appToken = savedInstanceState.getString(STATE_APP_TOKEN);

            showData();
        }
        log = Log4jHelper.getLogger(TAG);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString(STATE_GCM, tokenGCM);
        savedInstanceState.putString(STATE_APP_TOKEN, appToken);

        super.onSaveInstanceState(savedInstanceState);
    }


    private void getTokenGCM() {
        if (checkPlayServices()) {
            startService(new Intent(LoginActivity.this, RegistrationIntentService.class));
        }
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        dbWork.closeDB();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbWork.closeDB();
    }

    private void setTokenGCM(Context context) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        tokenGCM = sharedPreferences.getString(QuickstartPreferences.STRING_TOKEN_TO_SERVER, "");

    }


    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
//                Log.d(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * запрос на получение токена приложения
     */
    private void loadTokenApp(final boolean isBadToken) {
        showProgress();
        AuthSend authSend = new AuthSend(getString(R.string.appLogin), getString(R.string.appPassword));
        Call<ModelAuth> call = restApi.getAuth(authSend);
        call.enqueue(new Callback<ModelAuth>() {
            @Override
            public void onResponse(Response<ModelAuth> response, Retrofit retrofit) {
                android.util.Log.d("DOC", "response.code() = " + response.code());
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    appToken = response.body().data.getToken();

                    SharedPref.setTokenApp(appToken, LoginActivity.this);
                    if (isBadToken) {

                        setTokenGCM(LoginActivity.this);

                        loadDoctorData(SharedPref.getLogin(LoginActivity.this), SharedPref.getPass(LoginActivity.this));
                    } else {
                        showData();

                        if (tokenGCM.equals("")) {
                            getTokenGCM();
                        }
                    }

                } else {
                    Utils.showErrorMessage(response, LoginActivity.this, retrofit);
                    numberReq = REQ_TOKEN;
                    showError();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                numberReq = REQ_TOKEN;
                showError();
            }
        });
    }

    /**
     * запрос на получение данных о докторе + непосредственно логин
     */
    private void loadDoctorData(final String login, final String pass) {

        showProgress();

        if (tokenGCM.equals("")) {
            getTokenGCM();
        }

        if (tokenGCM.equals(""))
            tokenGCM = "fake";

//        DoctorSend docSend = new DoctorSend("9376737555", "12345678", tokenGCM, this);//test
        DoctorSend docSend = new DoctorSend(login, pass, tokenGCM, this);
        Call<ModelDoctor> call = restApi.getDoctor(appToken, docSend);
        call.enqueue(new Callback<ModelDoctor>() {
            @Override
            public void onResponse(Response<ModelDoctor> response, Retrofit retrofit) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    int i = 0;
                    boolean isNurse = false;
                    if (response.body().data.doctor != null && response.body().data.doctor.getSpecializations() != null) {
                        while (!isNurse && i < response.body().data.doctor.getSpecializations().size()) {
                            isNurse = (response.body().data.doctor.getSpecializations().get(i).getId() == Constants.NURSE_ID);
                            i++;
                        }
                    }
                    SharedPref.isNurse(isNurse, LoginActivity.this);
                    dbWork.doctorWriteDatabase(response);
                    SharedPref.setTokenUser(response.body().data.getToken(), LoginActivity.this);
                    SharedPref.setIsFirstStartApp(false, LoginActivity.this);
                    SharedPref.setLoginPass(login, pass, LoginActivity.this);


                    MyApplication.isSessionOpened = false;

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Utils.showErrorMessage(response, LoginActivity.this, retrofit);
                    showData();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                numberReq = REQ_DOC_DATA;
                showError();
            }
        });
    }
}
