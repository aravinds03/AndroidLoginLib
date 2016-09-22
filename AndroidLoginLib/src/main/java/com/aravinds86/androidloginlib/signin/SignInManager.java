package com.aravinds86.androidloginlib.signin;
//
// Copyright 2016 Amazon.com, Inc. or its affiliates (Amazon). All Rights Reserved.
//
// Code generated by AWS Mobile Hub. Amazon gives unlimited permission to 
// copy, distribute and modify it.
//
// Source code generated from template: aws-my-sample-app-android v0.7
//

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * The SignInManager is a singleton component, which creates the sign-in identity providers and
 * orchestrates sign-in call flows. It is responsible for keeping track of the most recent provider,
 * refreshing credentials and initializing sign-in buttons with the providers.
 */
public class SignInManager {
    private final int HTTP_CODE_SERVICE_UNAVAILABLE = 503;
    private final static String LOG_TAG = SignInManager.class.getSimpleName();
    private final Map<Class<? extends SignInProvider>, SignInProvider> signInProviders = new HashMap<>();
    private static SignInManager singleton = null;
    private Context context = null;

    /**
     * Constructor.
     * @param context context.
     */
    private SignInManager(final Context context) {
        assert (singleton == null);
        singleton = this;

        this.context = context.getApplicationContext();

        // Initialize Facebook SDK.
        final FacebookSignInProvider facebookSignInProvider = new FacebookSignInProvider(context);
        addSignInProvider(facebookSignInProvider);

        // Initialize Google SDK.
        final GoogleSignInProvider googleSignInProvider = new GoogleSignInProvider(context);
        addSignInProvider(googleSignInProvider);

    }

    /**
     * Gets the singleton instance of this class.
     * @return instance
     */
    public synchronized static SignInManager getInstance(final Context context) {
        if (singleton == null) {
            singleton = new SignInManager(context);
        }
        return singleton;
    }

    public synchronized static void dispose() {
        singleton = null;
    }

    /**
     * Adds a sign-in identity provider.
     * @param signInProvider sign-in provider
     */
    public void addSignInProvider(final SignInProvider signInProvider) {
        signInProviders.put(signInProvider.getClass(), signInProvider);
    }

    /**
     * Call getPreviouslySignedInProvider to determine if the user was left signed-in when the app
     * was last running.  This should be called on a background thread since it may perform file
     * i/o.  If the user is signed in with a provider, this will return the provider for which the
     * user is signed in.  Subsequently, refreshCredentialsWithProvider should be called with the
     * provider returned from this method.
     * @return false if not already signed in, true if the user was signed in with a provider.
     */
    public SignInProvider getPreviouslySignedInProvider() {

        for (final SignInProvider provider : signInProviders.values()) {
            // Note: This method may block. This loop could potentially be sped
            // up by running these calls in parallel using an executorService.
            if (provider.isUserSignedIn()) {
                return provider;
            }
        }
        return null;
    }

    private class SignInResultsAdapter implements SignInResultsHandler {
        final private SignInResultsHandler handler;
        final private Activity activity;

        public SignInResultsAdapter(final Activity activity,
                                    final SignInResultsHandler handler) {
            this.handler = handler;
            this.activity = activity;
        }

        private Activity getActivity() {
            return activity;
        }

        private boolean runningFromMainThread() {
            return Looper.myLooper() == Looper.getMainLooper();
        }

	    /** {@inheritDoc} */
        @Override
        public void onSuccess(final SignInProvider provider) {
            if (!runningFromMainThread()) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handler.onSuccess(provider);
                    }
                });
            } else {
                handler.onSuccess(provider);
            }
        }

        /** {@inheritDoc} */
        @Override
        public void onCancel(final SignInProvider provider) {
            if (!runningFromMainThread()) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handler.onCancel(provider);
                    }
                });
            } else {
                handler.onCancel(provider);
            }
        }

        /** {@inheritDoc} */
        @Override
        public void onError(final SignInProvider provider, final Exception ex) {
            if (!runningFromMainThread()) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handler.onError(provider, ex);
                    }
                });
            } else {
                handler.onError(provider, ex);
            }
        }
    }

    private SignInResultsAdapter resultsAdapter;

    /**
     * Sets the results handler to handle final results from sign-in.  Results handlers are
     * always called on the UI thread.
     * @param activity the calling activity.
     * @param resultsHandler the handler for results from sign-in with a provider.
     */
    public void setResultsHandler(final Activity activity,
                                  final SignInResultsHandler resultsHandler) {
        resultsAdapter = new SignInResultsAdapter(activity, resultsHandler);
    }

    /**
     * Call initializeSignInButton to intialize the logic for sign-in for a specific provider.
     * @param providerClass the SignInProvider class.
     * @param buttonView the view for the button associated with this provider.
     * @return the onClickListener for the button to be able to override the listener.
     */
    public View.OnClickListener initializeSignInButton(final Class<? extends SignInProvider> providerClass,
                                       final View buttonView) {
        final SignInProvider provider = findProvider(providerClass);

        // Initialize the sign in button with the identity manager's results adapter.
        return provider.initializeSignInButton(resultsAdapter.getActivity(),
            buttonView,
            resultsAdapter);
    }

    private SignInProvider findProvider(final Class<? extends SignInProvider> clazz) {

        final SignInProvider provider = signInProviders.get(clazz);

        if (provider == null) {
            throw new IllegalArgumentException("No such provider : " + clazz.getCanonicalName());
        }

        return provider;
    }

    /**
     * Handle the Activity result for login providers.
     * @param requestCode the request code.
     * @param resultCode the result code.
     * @param data result intent.
     * @return true if the sign-in manager handle the result, otherwise false.
     */
    public boolean handleActivityResult(final int requestCode, final int resultCode, final Intent data) {

        for (final SignInProvider provider : signInProviders.values()) {
            if (provider.isRequestCodeOurs(requestCode)) {
                provider.handleActivityResult(requestCode, resultCode, data);
                return true;
            }
        }

        return false;
    }
}
