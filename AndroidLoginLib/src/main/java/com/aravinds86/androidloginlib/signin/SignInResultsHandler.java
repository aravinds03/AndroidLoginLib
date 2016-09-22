package com.aravinds86.androidloginlib.signin;


/**
 *  Implement this interface to get callbacks for the results to a sign-in operation.
 */
public interface SignInResultsHandler {
    /**
     * Sign-in was successful.
     * @param provider sign-in identity provider
     */
    void onSuccess(SignInProvider provider);

    /**
     * Sign-in was cancelled by the user.
     * @param provider sign-in identity provider
     */
    void onCancel(SignInProvider provider);

    /**
     * Sign-in failed.
     * @param provider sign-in identity provider
     * @param ex exception that occurred
     */
    void onError(SignInProvider provider, Exception ex);
}
