package com.uniquepaybd.uniquepay;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {
    @FormUrlEncoded
    //@POST("/home/connect_with_mobile_app")
    @POST("/?uniquepaybd")

    Call<YourResponseModel> connectWithMobileApp(
            @Field("user_email") String user_email,
            @Field("device_key") String device_key,
            @Field("device_ip") String device_ip
    );
}
