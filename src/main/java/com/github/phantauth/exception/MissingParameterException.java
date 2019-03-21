package com.github.phantauth.exception;

import com.nimbusds.oauth2.sdk.http.HTTPResponse;

public class MissingParameterException extends PhantAuthException {
    private static final String CODE = "missing_parameter";

    public MissingParameterException(final String parameter, final String type) {
        super(CODE, String.format("Missing '%s' %s parameter", parameter, type), HTTPResponse.SC_BAD_REQUEST);
    }
}
