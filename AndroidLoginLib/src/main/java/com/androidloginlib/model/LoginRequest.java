package com.androidloginlib.model;

/**
 * Created by asanthan on 9/18/16.
 */
public class LoginRequest {
    private String identityType;
    private String identityToken;

    private LoginRequest(String identityType, String identityToken) {
        this.identityType = identityType;
        this.identityToken = identityToken;
    }

    public String getIdentityType() {
        return identityType;
    }

    public String getIdentityToken() {
        return identityToken;
    }

    public static LoginRequest of(String identityType, String identityToken) {
        return new LoginRequest(identityType, identityToken);
    }
}
