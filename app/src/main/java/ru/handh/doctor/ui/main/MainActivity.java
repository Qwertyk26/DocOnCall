package ru.handh.doctor.ui.main;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;

import net.hockeyapp.android.UpdateManager;

import org.apache.log4j.chainsaw.Main;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import ru.handh.doctor.BuildConfig;
import ru.handh.doctor.FluffyGeoService;
import ru.handh.doctor.GlobalConfig;
import ru.handh.doctor.MyApplication;
import ru.handh.doctor.ParentActivity;
import ru.handh.doctor.R;
import ru.handh.doctor.event.SessionButtonVisibilityEvent;
import ru.handh.doctor.event.SessionDurationEvent;
import ru.handh.doctor.event.ShowHideProgressEvent;
import ru.handh.doctor.event.TakePhotoCommandEvent;
import ru.handh.doctor.io.db.DBWork;
import ru.handh.doctor.io.network.ApiInstance;
import ru.handh.doctor.io.network.ReqForCalls;
import ru.handh.doctor.io.network.RestApi;
import ru.handh.doctor.io.network.responce.DefaultResponse;
import ru.handh.doctor.io.network.responce.ModelCallGet;
import ru.handh.doctor.io.network.responce.ModelConfig;
import ru.handh.doctor.io.network.responce.ModelErrors;
import ru.handh.doctor.io.network.responce.ModelLogout;
import ru.handh.doctor.io.network.responce.ModelSessionStatus;
import ru.handh.doctor.io.network.responce.ModelStatus;
import ru.handh.doctor.io.network.responce.calls.DataCall;
import ru.handh.doctor.io.network.responce.calls.ModelCallsReq;
import ru.handh.doctor.io.network.send.CallGetSend;
import ru.handh.doctor.io.network.send.LogoutSend;
import ru.handh.doctor.io.network.send.StatusSend;
import ru.handh.doctor.model.CallNew;
import ru.handh.doctor.model.SessionFinishPost;
import ru.handh.doctor.model.SessionStartPost;
import ru.handh.doctor.ui.FragmentSettings;
import ru.handh.doctor.ui.calls.FragmentCallDetail;
import ru.handh.doctor.ui.calls.FragmentCallsStart;
import ru.handh.doctor.ui.chats.FragmentChats;
import ru.handh.doctor.ui.dialog.SessionDurationDialogFragment;
import ru.handh.doctor.ui.history.FragmentHistoryCalls;
import ru.handh.doctor.utils.Constants;
import ru.handh.doctor.utils.Log;
import ru.handh.doctor.utils.Log4jHelper;
import ru.handh.doctor.utils.SharedPref;
import ru.handh.doctor.utils.Utils;

public class MainActivity extends ParentActivity implements CallsOnResponce {

    public final static String TAG = "MainActivity";
    public static final HashMap<String, String> letterColors = new HashMap<>();
    public final static String PARAM_LOCATION = "location";
    public final static int STATUS_CHANGE_STATUS = 300;
    public final static int STATUS_NEW_GCM_MESSAGE = 200;
    public final static int STATUS_NEW_COORDINATES = 100;
    public final static String PARAM_STATUS = "status";
    public final static String BROADCAST_ACTION = "broadcastNewGCMMessage";
    public final static int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 11111;

    private static int DIALOG_MAX_WIDTH = 600;

    public DBWork dbWork;
    public ClickItemMenu interfaceClickMenu;
    //public ViewFlipper viewFlipperRightMenu;
    public LocationInfo currentLocation;
    public Toolbar toolbar;
    public DrawerLayout drawer;
    public boolean isFirst = true, isStartScreens = true;
    //public SwipeRefreshLayout swipeLayoutNewCalls;
    public RestApi restApi = ApiInstance.defaultService(RestApi.class);

    private boolean doubleBackToExitPressedOnce = false;
    private String menuTag, title;
    private TextView menuCalls, menuHistory, menuSettings, menuAbout, menuHeaderName, menuChats;//, emptyRightMenu;
    private ImageView photoMenu;
    private ProgressBar toolbarProgressBar;
    private ProgressBar uploadProgressBar;
    private Spinner spinner;
    //private RecyclerView rv;
    private AlertDialog aboutDialog;
    private TextView aboutDialogTitle;

    private AlertDialog locationDialog;

    private Button sessionButton;
    private int windowWidth;

    private MenuItem menuStatus, menuSaveSettings;
    private ArrayList<CallNew> applications = new ArrayList<>();
    private StatusSpinnerAdapter adapterStatusDoc;
    //private AdapterRightMenu adapterRightMenu;
    private ReqForCalls reqForCalls;
    private BroadcastReceiver br;
    org.apache.log4j.Logger log;
    String[] filesString;

    private View.OnClickListener menuClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            FragmentManager transaction = getSupportFragmentManager();
            switch (v.getId()) {
                case R.id.menuCalls_tv:
                    transaction.beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in,
                                    android.R.anim.fade_out)
                            .replace(R.id.container, FragmentCallsStart.newInstance(), FragmentCallsStart.FRAGMENT_TAG).commitAllowingStateLoss();
                    log.info(TAG + " menu calls clicked");
                    break;
                case R.id.menuHistory_tv:
                    transaction.beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in,
                                    android.R.anim.fade_out)
                            .replace(R.id.container, FragmentHistoryCalls.newInstance(), FragmentHistoryCalls.FRAGMENT_TAG).commitAllowingStateLoss();
                    log.info(TAG + " menu history clicked");
                    break;
                case R.id.menuChats_tv:
                    transaction.beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in,
                            android.R.anim.fade_out).replace(R.id.container, FragmentChats.newInstance(), FragmentChats.FRAGMENT_TAG).commitAllowingStateLoss();
                    log.info(TAG + " menu chats clicked");
                    break;
                case R.id.menuSettings_tv:
                    transaction.beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in,
                                    android.R.anim.fade_out)
                            .replace(R.id.container, FragmentSettings.newInstance(), FragmentSettings.FRAGMENT_TAG).commitAllowingStateLoss();
                    log.info(TAG + " menu setting clicked");
                    break;
                case R.id.menuAbout_tv:
                    if(!isAboutDialogShown()) {
                        aboutDialogTitle.setText(R.string.dialog_title);
                        log.info(TAG + " menu about clicked");
                        aboutDialog.show();
                    }
                    break;
                case R.id.menuLogout_tv:
                    log.info(TAG + " menu logout clicked");
                    final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.cancel(0);
                    logoutReq();
                    break;
            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
    };

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log = Log4jHelper.getLogger(TAG);
        setColorForIcons();

        locationDialog = Utils.showLocationDialog(this, true);

        setContentView(R.layout.activity_main);

        //swipeLayoutNewCalls = (SwipeRefreshLayout) findViewById(R.id.swipe_container_new_calls);

        initSessionButton();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Utils.getCustomColor(this, R.color.colorAccent));

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipperMain);
        //viewFlipperRightMenu = (ViewFlipper) findViewById(R.id.viewFlipperRightMenu);
        toolbarProgressBar = (ProgressBar) findViewById(R.id.toolbar_progress_bar);
        uploadProgressBar = (ProgressBar) findViewById(R.id.upload_progress);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            uploadProgressBar.getIndeterminateDrawable().setColorFilter(
                    getResources().getColor(R.color.colorAccent), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        adapterStatusDoc = new StatusSpinnerAdapter(this);
        spinner = (Spinner) findViewById(R.id.spinner_status);
        spinner.setAdapter(adapterStatusDoc);
        spinner.setSelection(SharedPref.getDocStatus(this));
        photoMenu = (ImageView) findViewById(R.id.imageViewPhotoMenu);
        menuHeaderName = (TextView) findViewById(R.id.menuHeaderName);
        menuCalls = (TextView) findViewById(R.id.menuCalls_tv);
        menuHistory = (TextView) findViewById(R.id.menuHistory_tv);
        menuSettings = (TextView) findViewById(R.id.menuSettings_tv);
        menuAbout = (TextView) findViewById(R.id.menuAbout_tv);
        menuChats = (TextView) findViewById(R.id.menuChats_tv);
        TextView menuLogout = (TextView) findViewById(R.id.menuLogout_tv);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        toolbar.setNavigationIcon(Utils.getCustomDrawable(R.drawable.ic_menu, this));

        if (BuildConfig.DEBUG) {
            TextView buildVersion = (TextView) findViewById(R.id.buildVersion);
            buildVersion.setText(BuildConfig.VERSION_NAME);
        }


        menuCalls.setOnClickListener(menuClick);
        menuChats.setOnClickListener(menuClick);
        menuHistory.setOnClickListener(menuClick);
        menuSettings.setOnClickListener(menuClick);
        menuAbout.setOnClickListener(menuClick);
        menuLogout.setOnClickListener(menuClick);

        //rv = (RecyclerView) findViewById(R.id.rvRightMenu);
        //emptyRightMenu = (TextView) findViewById(R.id.tvRightMenuEmpty);
        //rv.setHasFixedSize(true);

        //LinearLayoutManager llm = new LinearLayoutManager(this);
        //.setLayoutManager(llm);

        //RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        //rv.setItemAnimator(itemAnimator);

        if (savedInstanceState == null) {
            menuCalls.performClick();
        } else {
            applications = savedInstanceState.getParcelableArrayList("applications");
            //adapterRightMenu = new AdapterRightMenu(applications, this);
            //rv.setAdapter(adapterRightMenu);
            //if (viewFlipperRightMenu.getDisplayedChild() != Constants.VIEW_CONTENT)
            //    viewFlipperRightMenu.setDisplayedChild(Constants.VIEW_CONTENT);
            isFirst = savedInstanceState.getBoolean("isFirst", true);
        }

        dbWork = new DBWork(this);
        try {
            setDocDataToInterface();
        } catch (Exception e) {
            logoutReq();
        }


        reqForCalls = new ReqForCalls();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (menuStatus != null) {
                    if (savedInstanceState != null) {
                        selectStatus(position);
                    } else {
                        toolbarProgressBar.setVisibility(View.VISIBLE);
                        hideMenuItemStatus();
                        changeDocStatusReq(position + 1);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        findViewById(R.id.button_reqError).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutReq();
            }
        });

//        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean(Constants.EXTRAS_PUSH, false)) {
//            drawer.openDrawer(GravityCompat.END);
//            getIntent().putExtra(Constants.EXTRAS_PUSH, false);
//        }

//        swipeLayoutNewCalls.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                 loadNewCalls();
//            }
//        });

        showData();

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(PARAM_STATUS, 0);

                switch (status) {
                    case STATUS_NEW_GCM_MESSAGE:
                        loadNewCalls();
                        //drawer.openDrawer(GravityCompat.END);
                        break;
                    case STATUS_CHANGE_STATUS:
                        loadNewCalls();
                        break;
                    case STATUS_NEW_COORDINATES:
                        currentLocation = (LocationInfo) intent.getSerializableExtra(PARAM_LOCATION);
                        if (currentLocation == null || currentLocation.lastLat == 0) {
                            locationDialog = Utils.showLocationDialog(MainActivity.this, true);
                            finish();
                        }
                        break;

                }
            }


        };
        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(br, intFilt);

        loadConfig();

        UpdateManager.register(this);

        //showEmptyNew();
        EventBus.getDefault().register(this);
    }

    private void loadConfig() {
        Call<ModelConfig> configCall = restApi.getConfig(SharedPref.getTokenApp(MainActivity.this));
        configCall.enqueue(new Callback<ModelConfig>() {
            @Override
            public void onResponse(Response<ModelConfig> response, Retrofit retrofit) {
                if(response.isSuccess()) {
                    GlobalConfig.onConfigLoaded(response.body());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void initSessionButton() {
        windowWidth = getResources().getDisplayMetrics().widthPixels;
        DIALOG_MAX_WIDTH = (int) Utils.pxFromDp(this, 600);

        sessionButton = (Button) findViewById(R.id.session_button);
        sessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log.info(TAG + " session button clicked");
                if(MyApplication.isSessionOpened) {
                    final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Завершение рабочей смены")
                            .setMessage("Вы уверены, что хотите завершить рабочую смену?")
                            .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finishSession(false);
                                    log.info(TAG + " session dialog dismiss clicked");
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    log.info(TAG + " session dialog dismiss clicked");
                                    dialog.dismiss();
                                }
                            })
                            .create();
                            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface pD) {
                                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#757575"));
                                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                                }
                            });
                            dialog.show();
                    if(windowWidth*0.8f>DIALOG_MAX_WIDTH) dialog.getWindow().setLayout(DIALOG_MAX_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);

                } else {
                    final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Начало рабочей смены")
                            .setMessage("Вы точно хотите начать рабочую смену?\nПосле подтверждения начала смены система автоматически начнет назначать на Вас доступные вызовы")
                            .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(dbWork.isHourly()) {
                                        showSessionDurationDialog();
                                        log.info(TAG + " session start clicked");
                                    } else {
                                        startSession(0);
                                    }
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    log.info(TAG + " session dismiss clicked");
                                }
                            })
                            .create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface pD) {
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#757575"));
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                        }
                    });
                    dialog.show();
                    if(windowWidth*0.8f>DIALOG_MAX_WIDTH) dialog.getWindow().setLayout(DIALOG_MAX_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
            }
        });

        getSessionStatus();

        EventBus.getDefault().register(sessionEventsListener);
    }

    private void startSession(int duration) {
        SessionStartPost sessionStartPost = new SessionStartPost(SharedPref.getTokenUser(this));
        if(duration!=0) sessionStartPost.setDuration(String.valueOf(duration));
        Call<DefaultResponse> startSession = ApiInstance.restApi.sessionStart(SharedPref.getTokenApp(this), sessionStartPost);
        startSession.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Response<DefaultResponse> response, Retrofit retrofit) {
                MyApplication.isSessionStateReceived = true;
                if(response.isSuccess()) {
                    changeSessionState(true);
                } else {
                    parseSessionError(Utils.parseError(response, retrofit));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void parseSessionError(ModelErrors error) {
        ModelErrors.Errors e = error.data.errors;
        switch (e.code) {
            case 4046:
                showDialog(e.message+" Начать рабочую смену можно не ранее, чем за 30 минут до ее начала");
                break;
            case 4047:
                new AlertDialog.Builder(this)
                        .setTitle("Завершение рабочей смены")
                        .setMessage(getString(R.string.session_cancel_orders_message))
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finishSession(true);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            case 4048:
                if(dbWork.isHourly()) {
                    showSessionDurationDialog();
                } else {
                    Toast.makeText(this, "Ошибка 4048", Toast.LENGTH_SHORT).show();
                    log.info(TAG + " session error 4048");
                }
                break;
            case 4049:
                Toast.makeText(this, "Смена уже открыта", Toast.LENGTH_SHORT).show();
                log.info(TAG + " session error 4049");
                changeSessionState(true);
                break;
            case 4050:
                log.info(TAG + " session error 4050");
                Toast.makeText(this, "Нет открытых смен", Toast.LENGTH_SHORT).show();
                changeSessionState(false);
                break;
            default:
                Toast.makeText(this, e.code+" "+e.message, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void finishSession(boolean closeCurrentOrders) {
        SessionFinishPost post = new SessionFinishPost(SharedPref.getTokenUser(this));
        post.setOrdersCurrentClose(closeCurrentOrders ? 1 : 0);
        Call<DefaultResponse> finishCall = ApiInstance.restApi.sessionFinish(SharedPref.getTokenApp(this), post);
        finishCall.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Response<DefaultResponse> response, Retrofit retrofit) {
                MyApplication.isSessionStateReceived = true;
                if(response.isSuccess()) {
                    changeSessionState(false);
                } else {
                    parseSessionError(Utils.parseError(response, retrofit));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void changeSessionState(boolean started) {
        MyApplication.isSessionOpened = started;

        updateSessionButtonState();

        loadNewCalls();
    }

    private void updateSessionButtonState() {
        if(MyApplication.isSessionOpened) {
            sessionButton.setBackgroundResource(R.drawable.button_secondary);
            sessionButton.setTextColor(Color.parseColor("#757575"));
            sessionButton.setText("Завершить смену");
        } else {
            sessionButton.setBackgroundResource(R.drawable.button_green);
            sessionButton.setTextColor(Color.WHITE);
            sessionButton.setText("Начать смену");
        }
    }

    private void showSessionDurationDialog() {
        final DialogFragment dialogFragment = new SessionDurationDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), SessionDurationDialogFragment.class.getName());
        log.info(TAG + " showSessionDurationDialog");
    }

    public void showDialog(String text) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(text)
                .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
        if(windowWidth*0.8f>DIALOG_MAX_WIDTH) dialog.getWindow().setLayout(DIALOG_MAX_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String currentVersion = Utils.getPackageVersion(this);
        String savedVersion = SharedPref.getAppVersion(this);
        if(!currentVersion.equalsIgnoreCase(savedVersion) && !isAboutDialogShown()) {
            SharedPref.setAppVersion(currentVersion, this);
            aboutDialogTitle.setText(R.string.dialog_title_new);
            aboutDialog.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //startService(new Intent(this, GeoService.class));
                    startService(new Intent(this, FluffyGeoService.class));
                } else {
                    Toast.makeText(MainActivity.this, R.string.geoPermissionDeni, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void navigationClickOpen() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
                log.info(TAG + " drawer menu clicked");
            }
        });
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        unregisterReceiver(br);
        dbWork.closeDB();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(sessionEventsListener);

        super.onDestroy();

        if(locationDialog!=null) locationDialog.dismiss();

        UpdateManager.unregister();
        unregisterReceiver(br);
        dbWork.closeDB();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
//        else if (drawer.isDrawerOpen(GravityCompat.END)) {
//            drawer.closeDrawer(GravityCompat.END);
//        }
        else {
            if (isStartScreens) {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, R.string.doubleClickBack, Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isFirst", isFirst);
        outState.putParcelableArrayList("applications", applications);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menuStatus = menu.findItem(R.id.action_status);
        //menuNotification = menu.findItem(R.id.action_notification);
        menuSaveSettings = menu.findItem(R.id.action_saveSettings);
        showMenuItems(menuTag);
        return true;
    }

    private void showMenuItems(String tag) {
        toolbar.setTitle(title);

        if (tag.equals(FragmentCallsStart.FRAGMENT_TAG)) {
            changeIconEmpty();
            EventBus.getDefault().post(new SessionButtonVisibilityEvent(true));
            menuStatus.setVisible(false);
            //menuNotification.setVisible(true);
            menuSaveSettings.setVisible(false);
            //drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END);

        } else {
            EventBus.getDefault().post(new SessionButtonVisibilityEvent(false));
            //menuNotification.setVisible(false);
            menuStatus.setVisible(false);
            menuSaveSettings.setVisible(false);
            //drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);
        }

        switch (tag) {
            case FragmentCallsStart.FRAGMENT_TAG:
                menuCalls.setBackgroundResource(R.drawable.selected_background);
                menuHistory.setBackgroundResource(R.drawable.click_background);
                menuSettings.setBackgroundResource(R.drawable.click_background);
                menuChats.setBackgroundResource(R.drawable.click_background);
                break;
            case FragmentChats.FRAGMENT_TAG:
                menuCalls.setBackgroundResource(R.drawable.click_background);
                menuHistory.setBackgroundResource(R.drawable.click_background);
                menuSettings.setBackgroundResource(R.drawable.click_background);
                menuChats.setBackgroundResource(R.drawable.selected_background);
                break;
            case FragmentHistoryCalls.FRAGMENT_TAG:
                menuCalls.setBackgroundResource(R.drawable.click_background);
                menuHistory.setBackgroundResource(R.drawable.selected_background);
                menuSettings.setBackgroundResource(R.drawable.click_background);
                menuChats.setBackgroundResource(R.drawable.click_background);
                break;
            case FragmentSettings.FRAGMENT_TAG:
                menuCalls.setBackgroundResource(R.drawable.click_background);
                menuHistory.setBackgroundResource(R.drawable.click_background);
                menuSettings.setBackgroundResource(R.drawable.selected_background);
                menuChats.setBackgroundResource(R.drawable.click_background);
                break;
            case FragmentCallDetail.FRAGMENT_TAG:
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
                break;
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ShowHideProgressEvent e) {
        if (e.getCommand() == "SHOW") {
            uploadProgressBar.setVisibility(View.VISIBLE);
        } else if (e.getCommand() == "HIDE") {
            uploadProgressBar.setVisibility(View.GONE);
        }
    }
//    public void showUploadProgress() {
//        uploadProgressBar.setVisibility(View.VISIBLE);
//    }
//    public void hideUploadProgress() {
//        uploadProgressBar.setVisibility(View.GONE);
//    }
    private void changeIconEmpty() {
//        if (applications.size() == 0) {
//            //emptyRightMenu.setVisibility(View.VISIBLE);
//            if (menuNotification != null)
//                menuNotification.setIcon(Utils.getCustomDrawable(R.drawable.ic_notification, this));
//        } else {
//            //emptyRightMenu.setVisibility(View.GONE);
//            if (menuNotification != null)
//                menuNotification.setIcon(Utils.getCustomDrawable(R.drawable.ic_notification_new, this));
//        }
    }

    private boolean isAboutDialogShown()
    {
        if(aboutDialog == null) {
            log.info(TAG + " about dialog showing ");
            aboutDialog = new AlertDialog.Builder(this).create();
            View view = getLayoutInflater().inflate(R.layout.about_view, null);
            aboutDialogTitle = (TextView) view.findViewById(R.id.aboutDialogTitle);
            TextView versionTextView = (TextView) view.findViewById(R.id.versionText);
            WebView webView = (WebView) view.findViewById(R.id.aboutWebView);
            Button okButton = (Button) view.findViewById(R.id.ok_button);
            Button sendReport = (Button) view.findViewById(R.id.sendReport);

            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    aboutDialog.dismiss();
                }
            });
            sendReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String path = Environment.getExternalStorageDirectory().toString() + "/logs";
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                            Locale.getDefault()).format(new Date());
                    File attachmentFile = Utils.zipFolder(path, Environment.getExternalStorageDirectory().toString() + "/" + timeStamp + "_doc_logs.zip");
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("text/plain");
                    String emailSubjectDate = new SimpleDateFormat("dd.MM.yyyy",
                            Locale.getDefault()).format(new Date());
                    emailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{Constants.EMAIL_TO_SEND_REPORT});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, String.format(getString(R.string.email_subject), emailSubjectDate));
                    emailIntent.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.email_body), SharedPref.getLogin(MainActivity.this), Build.MODEL, Build.VERSION.RELEASE, SharedPref.getAppVersion(MainActivity.this)));
                    Uri fileUri = FileProvider.getUriForFile(MainActivity.this, "ru.handh.doctor.provider", attachmentFile);
                    emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                    startActivity(Intent.createChooser(emailIntent, "Send Email"));
                }
            });

            webView.setWebChromeClient(new WebChromeClient());

            webView.loadDataWithBaseURL("", getAboutData(), "text/html", "UTF-8", "");

            versionTextView.setText(getResources().getText(R.string.dialog_version) + " " + Utils.getPackageVersion(this));
            aboutDialog.setView(view);

            return false;
        }
        return aboutDialog.isShowing();
    }

    private String getAboutData()
    {
        String result = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("About.html")));

            String line;
            while ((line = reader.readLine()) != null) {
                result = result + line;
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        return result;
    }


    private void selectStatus(int status) {
        switch (status) {
            case Constants.STATUS_DOC_FREE:
                startLocationService();
                menuStatus.setIcon(Utils.getCustomDrawable(R.drawable.status_free, this));
                break;
            case Constants.STATUS_DOC_BUSY:
                menuStatus.setIcon(Utils.getCustomDrawable(R.drawable.status_busy, this));
                //stopService(new Intent(this, GeoService.class));
                stopService(new Intent(this, FluffyGeoService.class));
                break;
            case Constants.STATUS_DOC_LANCH:
                menuStatus.setIcon(Utils.getCustomDrawable(R.drawable.status_lanch, this));
                //stopService(new Intent(this, GeoService.class));
                stopService(new Intent(this, FluffyGeoService.class));
                break;
            case Constants.STATUS_DOC_OFFLINE:
                menuStatus.setIcon(Utils.getCustomDrawable(R.drawable.ic_back, this));
                //stopService(new Intent(this, GeoService.class));
                stopService(new Intent(this, FluffyGeoService.class));
                break;
        }

        adapterStatusDoc.changeListForSpinner(status);
        adapterStatusDoc.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_status:
                spinner.performClick();
                return true;
            //case R.id.action_notification:
                //drawer.openDrawer(GravityCompat.END);
            //    return true;
            case R.id.action_saveSettings:
                interfaceClickMenu.clickSaveSettings();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initActionBar(String menuTag, String title) {
        this.menuTag = menuTag;
        this.title = title;

        if (menuStatus != null) {
            showMenuItems(menuTag);
        }
    }

    public void hideMenuItemStatus() {
        if (menuStatus != null) {
            menuStatus.setVisible(false);
        }
    }

    public void showMenuItemStatus() {
        if (menuStatus != null) {
            menuStatus.setVisible(false);
        }
    }

    public void showMenuItems() {
        EventBus.getDefault().post(new SessionButtonVisibilityEvent(true));
        if (menuStatus != null) {
            menuStatus.setVisible(false);
            //menuNotification.setVisible(true);
            menuSaveSettings.setVisible(false);
        }
    }

    public void enableOpenRightMenu() {
        //drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END);
    }

    public void enableOpenLeftMenu() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
    }


    private void setColorForIcons() {
        letterColors.put("А", "#ffcdd2");
        letterColors.put("Б", "#ff9e80");
        letterColors.put("В", "#e57373");
        letterColors.put("Г", "#ff5252");
        letterColors.put("Д", "#e91e63");
        letterColors.put("Е", "#ff80ab");
        letterColors.put("Ё", "#ff4081");
        letterColors.put("Ж", "#e1bee7");
        letterColors.put("З", "#ba68c8");
        letterColors.put("И", "#9fa8da");
        letterColors.put("Й", "#5c6bc0");
        letterColors.put("К", "#42a5f5");
        letterColors.put("Л", "#80cbc4");
        letterColors.put("М", "#ffd180");
        letterColors.put("Н", "#81c784");
        letterColors.put("О", "#ffa000");
        letterColors.put("П", "#c5e1a5");
        letterColors.put("Р", "#bdbdbd");
        letterColors.put("С", "#8c9eff");
        letterColors.put("Т", "#ef9a9a");
        letterColors.put("У", "#b388ff");
        letterColors.put("Ф", "#7c4dff");
        letterColors.put("Х", "#90caf9");
        letterColors.put("Ц", "#80deea");
        letterColors.put("Ч", "#cddc39");
        letterColors.put("Ш", "#fdd835");
        letterColors.put("Щ", "#616161");
        letterColors.put("Ы", "#546e7a");
        letterColors.put("Э", "#546e7a");
        letterColors.put("Ю", "#26c6da");
        letterColors.put("Я", "#ff80ab");
    }

    /**
     * выход из приложения
     */
    private void logoutReq() {
        showProgress();
        LogoutSend logoutSend = new LogoutSend(SharedPref.getTokenUser(MainActivity.this));
        Log.d("LogoutToken", SharedPref.getTokenApp(MainActivity.this));
        Call<ModelLogout> call = restApi.getLogout(SharedPref.getTokenApp(MainActivity.this), logoutSend);
        call.enqueue(new Callback<ModelLogout>() {
            @Override
            public void onResponse(Response<ModelLogout> response, Retrofit retrofit) {

                if (response.code() == HttpURLConnection.HTTP_OK) {

                    Utils.logout(MainActivity.this, false);

                } else {
                    Utils.showErrorMessage(response, MainActivity.this, retrofit);
                    showData();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                showError();
            }
        });
    }

    /**
     * изменение статуса врача
     */
    public void changeDocStatusReq(final int dotStatus) {
        StatusSend statusSend = new StatusSend(SharedPref.getTokenUser(MainActivity.this), String.valueOf(dotStatus));
        Call<ModelStatus> call = restApi.getStatus(SharedPref.getTokenApp(MainActivity.this), statusSend);
        call.enqueue(new Callback<ModelStatus>() {
            @Override
            public void onResponse(Response<ModelStatus> response, Retrofit retrofit) {

                if (response.code() == HttpURLConnection.HTTP_OK) {
                    int status = Integer.valueOf(response.body().data.getDoctorStatus()) - 1;

                    selectStatus(status);
                    SharedPref.setDocStatus(status, MainActivity.this);
//                    adapterStatusDoc.incData();

                } else {
                    Utils.showErrorMessage(response, MainActivity.this, retrofit);
                    startLocationService();
                }

                toolbarProgressBar.setVisibility(View.GONE);
                showMenuItemStatus();
            }

            @Override
            public void onFailure(Throwable t) {
                toolbarProgressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, R.string.statusNotChanged, Toast.LENGTH_SHORT).show();
                showMenuItemStatus();
//                adapterStatusDoc.decData();

                selectStatus(Constants.STATUS_DOC_OFFLINE);
            }
        });
    }

    /**
     * взятие/отмена новой заявки
     */
//    public void callGetReq(int idCall, String isGet) {
////        if (viewFlipperRightMenu.getDisplayedChild() != Constants.VIEW_PROGRESS)
////            viewFlipperRightMenu.setDisplayedChild(Constants.VIEW_PROGRESS);
//
//        CallGetSend callGetSend = new CallGetSend(SharedPref.getTokenUser(MainActivity.this), idCall, isGet);
//        Call<ModelCallGet> call = restApi.getCallGet(SharedPref.getTokenApp(MainActivity.this), callGetSend);
//        call.enqueue(new Callback<ModelCallGet>() {
//            @Override
//            public void onResponse(Response<ModelCallGet> response, Retrofit retrofit) {
//
//                if (response.code() == HttpURLConnection.HTTP_OK) {
//                    //if (viewFlipperRightMenu.getDisplayedChild() != Constants.VIEW_CONTENT)
//                    //    viewFlipperRightMenu.setDisplayedChild(Constants.VIEW_CONTENT);
//
//                    //adapterRightMenu.dellReq(position);
//
//                    // 1 новый запрос на новые вызовы
//                    loadNewCalls();
//
//                    // 2 новый запрос на актуальные вызовы
//                    loadActivCalls();
//
//                } else {
//                    Utils.showErrorMessage(response, MainActivity.this, retrofit);
//                    //if (viewFlipperRightMenu.getDisplayedChild() != Constants.VIEW_CONTENT)
//                    //    viewFlipperRightMenu.setDisplayedChild(Constants.VIEW_CONTENT);
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                Toast.makeText(MainActivity.this, R.string.notgetFalsCAll, Toast.LENGTH_LONG).show();
//                //if (viewFlipperRightMenu.getDisplayedChild() != Constants.VIEW_CONTENT)
//                //    viewFlipperRightMenu.setDisplayedChild(Constants.VIEW_CONTENT);
//            }
//        });
//    }

    public void loadNewCalls() {
        //if(swipeLayoutNewCalls!=null) swipeLayoutNewCalls.setRefreshing(false);
        if(MyApplication.isSessionStateReceived) {
            reqForCalls.callsReq(Constants.REQ_CALLS_NEW, MainActivity.this, MainActivity.this);
        } else {
            //showEmptyNew();
        }
    }

    private void loadMainList() {
        FragmentCallsStart fragmentStart = (FragmentCallsStart) getSupportFragmentManager().findFragmentByTag(FragmentCallsStart.FRAGMENT_TAG);
        if (fragmentStart != null) fragmentStart.loadActiveOnlyCalls();
    }

    @Override
    /** результат загрузки новых заявок*/
    public void callResponse(Response<ModelCallsReq> response, Retrofit retrofit) {
        if (response.code() == HttpURLConnection.HTTP_OK) {
            List<DataCall> newCalls = response.body().data;

            applications.clear();
            for (int i = 0; i < newCalls.size(); i++) {
                CallNew na = new CallNew();
                na.setName(newCalls.get(i).getFio());
                na.setNumber(String.valueOf(newCalls.get(i).getIdCall()));
                applications.add(na);
            }
            changeIconEmpty();

            acceptNewCalls();
            //adapterRightMenu = new AdapterRightMenu(applications, this);
            //rv.setAdapter(adapterRightMenu);
        } else {
            Utils.showErrorMessage(response, MainActivity.this, retrofit);
        }
    }

    private void acceptNewCalls() {
        if(applications!=null && applications.size()>0) {
            CallNew callNew = applications.get(0);

            CallGetSend callGetSend = new CallGetSend(SharedPref.getTokenUser(MainActivity.this), Integer.valueOf(callNew.getNumber()), "1");
            Call<ModelCallGet> call = restApi.getCallGet(SharedPref.getTokenApp(MainActivity.this), callGetSend);
            call.enqueue(new Callback<ModelCallGet>() {
                @Override
                public void onResponse(Response<ModelCallGet> response, Retrofit retrofit) {
                    if (response.code() == HttpURLConnection.HTTP_OK) {
                        if (applications != null && applications.size() > 0) {
                            applications.remove(0);
                            acceptNewCalls();
                        }
                    } else {
                        Utils.showErrorMessage(response, MainActivity.this, retrofit);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(MainActivity.this, R.string.notgetFalsCAll, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            loadMainList();
        }
    }

//    private void showEmptyNew() {
//        viewFlipperRightMenu.setDisplayedChild(Constants.VIEW_CONTENT);
//        adapterRightMenu = new AdapterRightMenu(new ArrayList<CallNew>(), this);
//        rv.setAdapter(adapterRightMenu);
//        if(swipeLayoutNewCalls!=null) swipeLayoutNewCalls.setRefreshing(false);
//    }

    public void setDocDataToInterface() {
        dbWork.setDocDataToInterface(photoMenu, menuHeaderName, null, null);
    }

    public void startLocationService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final List<String> permissionsList = new ArrayList<>();
            addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION);

            if (permissionsList.size() > 0) {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            } else {
                //startService(new Intent(this, GeoService.class));
                startService(new Intent(this, FluffyGeoService.class));
            }
        } else {
            //startService(new Intent(this, GeoService.class));
            startService(new Intent(this, FluffyGeoService.class));
        }
    }

    public interface ClickItemMenu {
        void clickSaveSettings();
    }

    private Object sessionEventsListener = new Object() {
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEvent(SessionDurationEvent e) {
            startSession(e.getDuration()*60);
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEvent(SessionButtonVisibilityEvent e) {
            if(sessionButton!=null) sessionButton.setVisibility(e.isVisible() ? View.VISIBLE : View.GONE);
        }
    };

    /**
     * запрос на получение статуса смены
     */
    private void getSessionStatus() {
        Call<ModelSessionStatus> call = ApiInstance.defaultService(RestApi.class).getSessionStatus(SharedPref.getTokenApp(MainActivity.this), SharedPref.getTokenUser(MainActivity.this));
        call.enqueue(new Callback<ModelSessionStatus>() {
            @Override
            public void onResponse(Response<ModelSessionStatus> response, Retrofit retrofit) {
                if (response.isSuccess() && response.body() != null) {
                    MyApplication.isSessionStateReceived = true;
                    changeSessionState(response.body().data.getScheduleOpen());
                } else {
                    MyApplication.isSessionStateReceived = false;
                    changeSessionState(false);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                MyApplication.isSessionStateReceived = false;
                changeSessionState(false);
            }
        });
    }
    public void showUploadProgress() {
        uploadProgressBar.setVisibility(View.VISIBLE);
    }
    public void hideUploadProgress() {
        uploadProgressBar.setVisibility(View.GONE);
    }
}