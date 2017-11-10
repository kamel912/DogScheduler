package com.teamvii.dogscheduler.connection;


import com.teamvii.dogscheduler.models.ResponseFromServer;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by MK on 9/24/2017.
 */

public interface ApiInterface {
    @GET("Prod")
    Call<ResponseFromServer> getMessages(@Query("Clientid") String clientId,
                                         @Query("Type") String type,
                                         @Query("Number") String number,
                                         @Query("Message") String message);
}