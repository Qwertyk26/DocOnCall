package ru.handh.doctor.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.ResponseBody;

import org.joda.time.LocalDate;
import org.joda.time.Years;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import retrofit.Converter;
import retrofit.Response;
import retrofit.Retrofit;
import ru.handh.doctor.FluffyGeoService;
import ru.handh.doctor.R;
import ru.handh.doctor.gcm.QuickstartPreferences;
import ru.handh.doctor.io.db.Doctor;
import ru.handh.doctor.io.network.responce.ModelErrors;
import ru.handh.doctor.io.network.responce.calls.DataCall;
import ru.handh.doctor.ui.LoginActivity;
import ru.handh.doctor.ui.main.MainActivity;

/**
 * Created by sgirn on 11.11.2015.
 * вспомогательные методы
 */
public class Utils {

    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static void changeColor(String name, TextView image) {
        name = name.trim();
        if (name.isEmpty()) {
            return;
        }
        String letter = name.substring(0, 1);
        image.setText(letter);

        Drawable background = image.getBackground();
        if (background instanceof ShapeDrawable) {
        } else if (background instanceof GradientDrawable) {
            GradientDrawable gradientDrawable = (GradientDrawable) background;
            try {
                String letterS = MainActivity.letterColors.get(letter.toUpperCase());

                if (letterS != null)
                    gradientDrawable.setColor(Color.parseColor(letterS));
                else {
                    gradientDrawable.setColor(getCustomColor(image.getContext(), R.color.colorAccent));
                }

            } catch (IllegalArgumentException e) {
                Log.e(" " + e);
            }

        }
    }

    public static Drawable getCustomDrawable(int resourceID, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getResources().getDrawable(resourceID, context.getTheme());
        } else {
            return context.getResources().getDrawable(resourceID);
        }
    }

    public static int getCustomColor(Context context, int id) {

        if (Build.VERSION.SDK_INT >= 23) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }

    public static void showErrorMessage(Response response, Activity context, Retrofit retrofit) {
        if (response != null && !response.isSuccess() && response.errorBody() != null) {
            Converter<ResponseBody, ModelErrors> errorConverter =
                    retrofit.responseConverter(ModelErrors.class, new Annotation[0]);
            try {
                ModelErrors error = errorConverter.convert(response.errorBody());
                if (error.data != null) {
                    Toast.makeText(context, error.data.errors.message, Toast.LENGTH_LONG).show();

                    //300 обновлять токен приложения
                    //104 обновлять токен пользователя

                    switch (error.data.errors.code) {
                        case HttpURLConnection.HTTP_MULT_CHOICE:
                            logout(context, true);
                            break;

                        case 104:
                            logout(context, true);
                            break;

                        case 106:
                            logout(context, true);
//                            logout(context, false);
                            break;
                    }

                }


            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Произошла неизвестная ошибка", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static ModelErrors parseError(Response response, Retrofit retrofit) {
        if (response != null && !response.isSuccess() && response.errorBody() != null) {
            Converter<ResponseBody, ModelErrors> errorConverter =
                    retrofit.responseConverter(ModelErrors.class, new Annotation[0]);
            try {
                return errorConverter.convert(response.errorBody());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static void showToastError(Response response, Context context, Retrofit retrofit) {
        ModelErrors errors = parseError(response, retrofit);
        Toast.makeText(context, "Код "+errors.data.errors.code+" "+errors.data.errors.message, Toast.LENGTH_SHORT).show();
    }

    public static void logout(Activity activity, boolean isBadToken) {
        Realm realm = Realm.getInstance(activity);
        realm.beginTransaction();
        realm.clear(Doctor.class);
        realm.commitTransaction();
        realm.close();

        SharedPref.setIsFirstStartApp(true, activity);
        SharedPref.setTokenApp("", activity);
        SharedPref.setTokenUser("", activity);
        SharedPref.setDocStatus(Constants.STATUS_DOC_FREE, activity);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        sharedPreferences.edit().putString(QuickstartPreferences.STRING_TOKEN_TO_SERVER, "").apply();

        if (!isBadToken) {
            SharedPref.setLoginPass("", "", activity);
        }


//        activity.stopService(new Intent(activity, GeoService.class));
        activity.stopService(new Intent(activity, FluffyGeoService.class));

        Intent intent = new Intent(activity, LoginActivity.class);
        intent.putExtra("isBadToken", isBadToken);
        activity.startActivity(intent);
        activity.finish();
    }

    public static String getPackageVersion(Context context)
    {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    public static AlertDialog showLocationDialog(final Activity activity, final boolean isCloseActivity) {

        LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            Log.d("error: " + ex);
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            Log.d("error: " + ex);
        }

        if (!gps_enabled && !network_enabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
            dialog.setMessage(activity.getString(R.string.enableGeo));
            dialog.setPositiveButton(activity.getString(R.string.settingsGeo), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    activity.startActivity(myIntent);

                }
            });
            dialog.setNegativeButton(activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    if (isCloseActivity)
                        activity.finish();
                }
            });
            return dialog.show();
        }
        return null;
    }
    //похоже на костыль. взял из другой части приложения
    public static void setCurrentOnLoad(List<DataCall> dataCall) {
        for (int i = 0; i < dataCall.size(); i++) {
            if (dataCall.get(i).getStatusCall().equals(Constants.STATUS_CALL_START_C) ||
                    dataCall.get(i).getStatusCall().equals(Constants.STATUS_CALL_ARRIVED_I) ||
                    dataCall.get(i).getStatusCall().equals(Constants.STATUS_CALL_COMPLETE_D)
                    ) {
                dataCall.get(i).setIsCurrent(true);
                break;
            }
        }
    }

    public static String getShortStatus(String statusLetter) {
        String status="";
        switch (statusLetter) {
            // B - Назначен
            // M - Принята
            // C - В пути
            // I - Подъезжаю
            // D - Прибыл к клиенту
            // E - Выполнено
            // L - Заполнена анкета
            case "B":
                status = "Назначен";
                break;
            case "M":
                status = "Принята";
                break;
            case "C":
                status = "В пути";
                break;
            case "I":
                status = "Подъезжаю";
                break;
            case "D":
                status = "Прибыл к клиенту";
                break;
            case "E":
                status = "Выполнено";
                break;
            case "L":
                status = "Заполнена анкета";
                break;
        }
        return status;
    }

    public static boolean canMakeTimeString(DataCall call) {
        return call.getDateArrival()!=null
                && call.getDateArrival().getTo()!=null && call.getDateArrival().getFrom()!=null
                && !call.getDateArrival().getTo().isEmpty() && !call.getDateArrival().getFrom().isEmpty();
    }

    public static String makeDateString(String longTime) {
        SimpleDateFormat source = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        SimpleDateFormat target = new SimpleDateFormat("dd.MM.yyyy");

        try {
            return target.format(source.parse(longTime));
        } catch (ParseException e) {
            return "";
        }
    }

    public static String makeTimeString(DataCall call) {
        SimpleDateFormat source = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        SimpleDateFormat target = new SimpleDateFormat("HH:mm");

        String from=null;
        String to=null;
        try {
            from = target.format(source.parse(call.getDateArrival().getFrom()));
            to = target.format(source.parse(call.getDateArrival().getTo()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return String.format("%1$s - %2$s", from, to);
    }

    public static String makeDateString(String date1, String date2) {
        SimpleDateFormat source = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        SimpleDateFormat target = new SimpleDateFormat("dd.MM.yyyy");

        String from=null;
        String to=null;
        try {
            from = target.format(source.parse(date1));
            to = target.format(source.parse(date2));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return String.format("С %1$s по %2$s", from, to);
    }

    public static boolean isAdult(DataCall dataCall) {
        SimpleDateFormat source = new SimpleDateFormat("dd.MM.yyyy");
        try {
            Date date = source.parse(dataCall.getPatientBirthday());
            LocalDate ld = new LocalDate(date);
            LocalDate now = new LocalDate();
            return Years.yearsBetween(ld, now).getYears()>=18;
        } catch (ParseException e) {
            return true;
        }

    }

    public static String getHourString(int hour) {
        switch (hour) {
            case 1:
                return "час";
            case 2:
            case 3:
            case 4:
                return "часа";
            default:
                return "часов";
        }
    }

    public static String ToBase64FromFilePath(String filePath) {
        byte[] bytes;
        int bytesRead;
        String result = "";
        try {
            byte[] buffer = new byte[8192];
            InputStream inputStream = new FileInputStream(filePath);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            bytes = output.toByteArray();
            result = Base64.encodeToString(bytes, Base64.DEFAULT + Base64.NO_PADDING + Base64.NO_WRAP);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
