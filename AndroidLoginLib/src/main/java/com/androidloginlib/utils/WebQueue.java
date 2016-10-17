package com.androidloginlib.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by asanthan on 10/2/16.
 */

public class WebQueue {
    private static WebQueue mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private WebQueue(Context context) {
        mCtx = context.getApplicationContext();
        mRequestQueue = getRequestQueue();
    }

    public static synchronized WebQueue of(Context context) {
        if (mInstance == null) {
            mInstance = new WebQueue(context);
        }
        return mInstance;
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx);
        }
        return mRequestQueue;
    }

    public <T> void enqueue(Request<T> req) {
        mRequestQueue.add(req);
    }
}
