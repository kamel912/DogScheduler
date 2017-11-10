package com.teamvii.dogscheduler.connection;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by MK on 9/24/2017.
 */

public class ApiClient {
    private static final String BASE_URL = "https://hy3gm2dndd.execute-api.us-east-1.amazonaws.com/";
    private static Retrofit retrofit ;

    public static Retrofit getApiClient(){
        if (retrofit == null){
            retrofit = new Retrofit.Builder().
                    baseUrl(BASE_URL).
                    addConverterFactory(GsonConverterFactory.create()).
                    build();
        }
        return retrofit;
    }
}
