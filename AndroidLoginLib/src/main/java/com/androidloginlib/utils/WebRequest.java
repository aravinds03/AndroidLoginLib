package com.androidloginlib.utils;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by asanthan on 6/28/2015.
 */
public class WebRequest extends JsonRequest {
    private static final String TAG = WebRequest.class.getSimpleName();
    private static final Gson GSON = new Gson();
    private static final int MAX_NUM_RETRIES = 3;
    private static final int INITIAL_TIMEOUT_MS = 5000;
    private static final float BACKOFF_MULTIPLIER = 1.0f;
    private static final DefaultRetryPolicy RETRY_POLICY
            = new DefaultRetryPolicy(INITIAL_TIMEOUT_MS, MAX_NUM_RETRIES, BACKOFF_MULTIPLIER);
    private Map<String, String> requestParams;
    private Type responseType;
    private String userIdentity;

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Log.d(TAG, "getHeaders");
        Map headers = new HashMap();
        headers.put("Authorization", userIdentity);
        return headers;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        if(requestParams == null) {
            return super.getParams();
        } else {
            return requestParams;
        }
    }

    private WebRequest(int httpMethod, String url, Object requestBody, Response.Listener<?> listener
            , Response.ErrorListener errorListener) {
        super(httpMethod, url, GSON.toJson(requestBody), listener, errorListener);
        setTag(requestBody.getClass().getSimpleName());
        setRetryPolicy(RETRY_POLICY);
        Log.i(TAG, "Method:"+httpMethod+" token="+getBody().toString());
        setShouldCache(false); // do not cache any request.
    }

    @Override
    protected Response<?> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data, HttpHeaderParser.parseCharset(response.headers));
            Object jsonResponse = GSON.fromJson(json, responseType);
            return Response.success(
                    jsonResponse, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    public WebRequest withParams(Map<String, String> requestParams) {
        this.requestParams = requestParams;
        return this;
    }

    public WebRequest responseType(Type type) {
        this.responseType = type;
        return this;
    }

    public WebRequest userIdentity(String userIdentity) {
        this.userIdentity = userIdentity;
        return this;
    }

    public static WebRequest get(String url, Object object, Response.Listener<?> listener,
                                 Response.ErrorListener errorListener) {
        return new WebRequest(Method.GET, url, object, listener, errorListener);
    }

    public static WebRequest post(String url, Object object, Response.Listener<?> listener,
                                  Response.ErrorListener errorListener) {
        return new WebRequest(Method.POST, url, object, listener, errorListener);
    }

    public void enqueue(RequestQueue requestQueue) {
        requestQueue.cancelAll(this.getTag());
        requestQueue.add(this);
    }
}
