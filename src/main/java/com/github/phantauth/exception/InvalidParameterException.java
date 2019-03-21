package com.github.phantauth.exception;

import com.nimbusds.oauth2.sdk.http.HTTPResponse;

public class InvalidParameterException extends PhantAuthException {
    private static final String CODE = "invalid_parameter";

    public InvalidParameterException(final String parameter) {
        super(CODE, String.format("Missing '%s' parameter", parameter), HTTPResponse.SC_BAD_REQUEST);
    }
}
