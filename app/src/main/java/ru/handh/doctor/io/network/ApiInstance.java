package ru.handh.doctor.io.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;


import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;


import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import ru.handh.doctor.utils.Constants;
import ru.handh.doctor.utils.LoggingInterceptor;

import static ru.handh.doctor.io.network.ApiInstance.retrofit;

/**
 * Created by hugochaves on 18.07.2016.
 */
public class ApiInstance {
    private static final int TIMEOUT = 60;

    private static Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .excludeFieldsWithModifiers(Modifier.TRANSIENT)
            .create();

    public static Retrofit retrofit;
    public static RestApi restApi;

    static {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(TIMEOUT, TimeUnit.SECONDS);
        client.setReadTimeout(TIMEOUT, TimeUnit.SECONDS);
        //client.interceptors().add(new LoggingInterceptor());
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        restApi = retrofit.create(RestApi.class);
    }


}
