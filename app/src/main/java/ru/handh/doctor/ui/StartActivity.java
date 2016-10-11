package ru.handh.doctor.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.littlefluffytoys.littlefluffylocationlibrary.StartupBroadcastReceiver;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.CrashManagerListener;
import net.hockeyapp.android.utils.Util;

import ru.handh.doctor.VersionChecker;
import ru.handh.doctor.ui.main.MainActivity;
import ru.handh.doctor.utils.Log;
import ru.handh.doctor.utils.SharedPref;
import ru.handh.doctor.utils.Utils;

/**
 * Created by sgirn on 06.10.2015.
 * стартовое активити с выбором экрана
 */
public class StartActivity extends AppCompatActivity {

    private int PERMISSION_REQUEST_CODE = 1;

    private boolean destroyed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        destroyed = false;

        CrashManager.register(this, Util.getAppIdentifier(this), new CrashManagerListener() {
            @Override
            public boolean shouldAutoUploadCrashes() {
                return true;
            }
        });

        new VersionChecker(new VersionChecker.Callback() {
            @Override
            public void onComplete(boolean needUpdate, String newVersion) {
                if(!destroyed) {
                    if(needUpdate) {
                        new AlertDialog.Builder(StartActivity.this)
                                .setTitle("Необходимо обновление")
                                .setMessage("Доступная новая версия приложения. Необходимо установить её из Google Play.")
                                .setPositiveButton("Перейти в Google Play", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String appPackageName = getPackageName();
                                        try {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                        } catch (android.content.ActivityNotFoundException anfe) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                        }
                                        dialog.dismiss();
                                    }
                                })
                                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        finish();
                                    }
                                })
                                .show();
                    } else {
                        if(ContextCompat.checkSelfPermission(StartActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
                            openStartActivity();
                        } else {
                            requestPermission();
                        }
                    }
                }
            }
        }).execute();
    }

    @Override
    protected void onDestroy() {
        destroyed = true;

        super.onDestroy();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_REQUEST_CODE);
    }

    public void openStartActivity() {
        finish();

        initFluffy();

        if (SharedPref.isFirstStartApp(this)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void initFluffy() {
        LocationLibrary.showDebugOutput(true);

        try {
            LocationLibrary.initialiseLibrary(getBaseContext(), "ru.handh.doctor");
        }
        catch (UnsupportedOperationException ex) {
            Log.d("UnsupportedOperationException thrown - the device doesn't have any location providers");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==PERMISSION_REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                openStartActivity();
            } else {
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                    new AlertDialog.Builder(this)
                            .setTitle("Ошибка")
                            .setMessage("Необходимо разрешить доступ к местоположению")
                            .setPositiveButton("Повторить", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermission();
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("Выход", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .show();
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Ошибка")
                            .setMessage("Необходимо разрешить доступ к местоположению. Это можно сделать в настройках приложения")
                            .setNegativeButton("Выход", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .show();
                }
            }
        }
    }
}
