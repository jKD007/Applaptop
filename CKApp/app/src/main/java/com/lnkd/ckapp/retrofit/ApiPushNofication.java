package com.lnkd.ckapp.retrofit;

import com.lnkd.ckapp.model.NotiRespone;
import com.lnkd.ckapp.model.NotiSendData;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiPushNofication {
    @Headers(
            {
                    "Content-Type: application/json",
                    "Authorization: key=AAAALpQcShQ:APA91bHYnw_dX0p7o8QLCD23eiHZ6G2o6Ich4eAQHKGWdC0_64LJ3iFmifBl7ixiXbRZRxsr1rCr7QxWfb1mqLi09-BYzuEySAg3xF5PrE70iFiovKrXZHkvH7cI62Uq90dP8hYMRGAh"
            }
    )
    @POST("fcm/send")
    Observable<NotiRespone> sendNofitication(@Body NotiSendData data);
}
