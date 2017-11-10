
package com.teamvii.dogscheduler.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseFromServer {


    @SerializedName("ok")
    @Expose
    private boolean ok;

    @SerializedName("message")
    @Expose
    private String respons;

    public String getRespons() {
        return respons;
    }
}
