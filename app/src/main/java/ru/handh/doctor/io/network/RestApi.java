package ru.handh.doctor.io.network;


import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;

import ru.handh.doctor.io.network.responce.ModelAuth;
import ru.handh.doctor.io.network.responce.ModelCallGet;
import ru.handh.doctor.io.network.responce.ModelCallImage;
import ru.handh.doctor.io.network.responce.ModelCallStatus;
import ru.handh.doctor.io.network.responce.ModelConfig;
import ru.handh.doctor.io.network.responce.ModelCoordinates;
import ru.handh.doctor.io.network.responce.ModelDoctor;
import ru.handh.doctor.io.network.responce.ModelLastUpdated;
import ru.handh.doctor.io.network.responce.ModelLogout;
import ru.handh.doctor.io.network.responce.ModelSessionStatus;
import ru.handh.doctor.io.network.responce.ModelStatus;
import ru.handh.doctor.io.network.responce.DefaultResponse;
import ru.handh.doctor.io.network.responce.calls.ModelCallsReq;
import ru.handh.doctor.io.network.send.AuthSend;
import ru.handh.doctor.io.network.send.CallGetSend;
import ru.handh.doctor.io.network.send.CallStatusSend;
import ru.handh.doctor.io.network.send.CallsSend;
import ru.handh.doctor.io.network.send.CoordinatesSend;
import ru.handh.doctor.io.network.send.DoctorSend;
import ru.handh.doctor.io.network.send.LogoutSend;
import ru.handh.doctor.io.network.send.SettingsNameSend;
import ru.handh.doctor.io.network.send.StatusSend;
import ru.handh.doctor.model.RedirectionPost;
import ru.handh.doctor.model.ReferenceListResponse;
import ru.handh.doctor.model.ReferenceResponse;
import ru.handh.doctor.model.SessionFinishPost;
import ru.handh.doctor.model.SessionStartPost;
import ru.handh.doctor.model.SicklistResponce;
import ru.handh.doctor.model.TransferListResponse;
import ru.handh.doctor.model.TransferPost;
import ru.handh.doctor.model.CallImagePost;

/**
 * Created by sergey on 08.12.15.
 * все запросы к серверу
 */
public interface RestApi {

    @POST("/api/auth")
    Call<ModelAuth> getAuth(@Body AuthSend auth);

    @GET("/api/config")
    Call<ModelConfig> getConfig(@Header("token") String tokenApp);

    @POST("/api/doctor/call/get")
    Call<ModelCallGet> getCallGet(@Header("token") String tokenApp, @Body CallGetSend send);

    @Headers({
            "Content-Type: application/json"
    })
    @POST("/api/doctor/calls")
    Call<ModelCallsReq> getCalls(@Header("token") String tokenApp, @Body CallsSend send);

    @POST("/api/doctor/call/status")
    Call<ModelCallStatus> getCallStatus(@Header("token") String tokenApp, @Body CallStatusSend send);

    @POST("/api/doctor/auth")
    Call<ModelDoctor> getDoctor(@Header("token") String authorization, @Body DoctorSend tas);

    @POST("/api/doctor/logout")
    Call<ModelLogout> getLogout(@Header("token") String authorization, @Body LogoutSend send);

    @POST("/api/doctor/coordinates")
    Call<ModelCoordinates> getCoordinates(@Header("token") String authorization, @Body CoordinatesSend send);

    @POST("/api/doctor/settings")
    Call<ModelDoctor> getSettings(@Header("token") String authorization, @Body SettingsNameSend send);

    @POST("/api/doctor/status")
    Call<ModelStatus> getStatus(@Header("token") String authorization, @Body StatusSend send);

    @GET("/api/client/call/last-modified")
    Call<ModelLastUpdated> getLastModified(@Header("token") String tokenApp, @Query("order_id") String orderId);

    @POST("/api/doctor/call/transfer")
    Call<TransferPost> postStatus(@Header("token") String tokenApp, @Body TransferPost transferPost);

    @GET("/api/doctor/call/transfer/list")
    Call<TransferListResponse> getTransferList(@Header("token") String tokenApp, @Query("token") String token, @Query("callId") int callId);

    @GET("/api/application/reference-book/list")
    Call<ReferenceListResponse> getReferenceList(@Header("token") String tokenApp);

    @GET("/api/application/reference-book")
    Call<ReferenceResponse> getReference(@Header("token") String tokenApp, @Query("code") String code);

    @POST("/api/doctor/call/redirection")
    Call<DefaultResponse> postRedirection(@Header("token") String tokenApp, @Body RedirectionPost redirectionPost);

    @GET("/api/doctor/sicklist")
    Call<SicklistResponce> getSickList(@Header("token") String tokenApp, @Query("token") String tokenUser, @Query("patient_id") int patientId);

    @POST("/api/doctor/shedule/start")
    Call<DefaultResponse> sessionStart(@Header("token") String tokenApp, @Body SessionStartPost sessionStartPost);

    @POST("/api/doctor/shedule/finish")
    Call<DefaultResponse> sessionFinish(@Header("token") String tokenApp, @Body SessionFinishPost sessionFinishPost);

    @GET("/api/doctor/schedule/status")
    Call<ModelSessionStatus> getSessionStatus(@Header("token") String tokenApp, @Query("token") String tokenUser);

    @POST("/api/doctor/call/image")
    Call<ModelCallImage> uploadImages(@Header("token") String tokenApp, @Body CallImagePost image);
}

