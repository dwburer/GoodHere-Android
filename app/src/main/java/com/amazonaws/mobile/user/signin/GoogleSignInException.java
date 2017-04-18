package com.amazonaws.mobile.user.signin;
//
// Copyright 2017 Amazon.com, Inc. or its affiliates (Amazon). All Rights Reserved.
//
// Code generated by AWS Mobile Hub. Amazon gives unlimited permission to 
// copy, distribute and modify it.
//
// Source code generated from template: aws-my-sample-app-android v0.16
//

import com.google.android.gms.auth.api.signin.GoogleSignInResult;

/**
 * Encapsulate exceptions that occurred due to a Google Sign-in failure.
 */
public class GoogleSignInException extends Exception {
    private static final String SIGN_IN_STATUS_UNAVAILBLE_MESSAGE = "";
    private GoogleSignInResult signInResult;

    /**
     * Constructor.
     *
     * @param signInResult the GoogleSignInResult.
     */
    public GoogleSignInException(final GoogleSignInResult signInResult) {
        super(signInResult.getStatus().getStatusMessage() != null ?
            signInResult.getStatus().getStatusMessage() : signInResult.getStatus().toString());
        this.signInResult = signInResult;
    }

    /**
     * @return GoogleSignInResult containing error status information.
     */
    public GoogleSignInResult getSignInResult() {
        return signInResult;
    }
}
