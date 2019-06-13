package com.github.phantauth.exception;

import com.nimbusds.oauth2.sdk.http.HTTPResponse;

public class ExtraParameterException extends PhantAuthException {
    private static final String CODE = "extra_parameter";

    public ExtraParameterException() {
        super(CODE, "Operation doesn't accept query or path parameters", HTTPResponse.SC_BAD_REQUEST);
    }
}
