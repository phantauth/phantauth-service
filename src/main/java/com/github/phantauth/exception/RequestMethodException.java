package com.github.phantauth.exception;

import com.nimbusds.oauth2.sdk.http.HTTPResponse;

public class RequestMethodException extends PhantAuthException {
    private static final String CODE = "unsupported_method";

    public RequestMethodException(final String method) {
        super(CODE, String.format("Request method %s is not supported by this operation", method), HTTPResponse.SC_BAD_REQUEST);
    }
}
