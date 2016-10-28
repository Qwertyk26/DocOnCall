package ru.handh.doctor.io.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.ConnectionPool;
import com.squareup.okhttp.OkHttpClient;


import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;


import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import ru.handh.doctor.ui.main.CallsOnResponce;
import ru.handh.doctor.utils.Constants;
import ru.handh.doctor.utils.LoggingInterceptor;

import static ru.handh.doctor.io.network.ApiInstance.retrofit;

/**
 * Created by hugochaves on 18.07.2016.
 */
public class ApiInstance {
    private static final int DEFAULT_TIMEOUT = 60;

    private static Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .excludeFieldsWithModifiers(Modifier.TRANSIENT)
            .create();

    public static Retrofit retrofit;
    public static RestApi restApi;

    public static <S> S defaultService(Class<S> serviceClass) {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(Constants.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        client.setReadTimeout(Constants.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        client.interceptors().add(new LoggingInterceptor());
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(serviceClass);
    }
    public static <S> S uploadService(Class<S> serviceClass) {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(Constants.UPLOAD_FILE_TIME_OUT, TimeUnit.SECONDS);
        client.setReadTimeout(Constants.UPLOAD_FILE_TIME_OUT, TimeUnit.SECONDS);
        client.setWriteTimeout(Constants.UPLOAD_FILE_TIME_OUT, TimeUnit.SECONDS);
        client.interceptors().add(new LoggingInterceptor());
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(serviceClass);
    }
}
