package com.androidloginlib.model;

/**
 * Created by asanthan on 9/18/16.
 */
public class Authorization {
    private String Authorization;

    public Authorization(String authorization) {
        this.Authorization = authorization;
    }

    public String getAuthorization() {
        return Authorization;
    }

    public String toString() {
        return "Authorization:"+getAuthorization();
    }
}
