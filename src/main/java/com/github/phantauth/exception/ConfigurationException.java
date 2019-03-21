package com.github.phantauth.exception;

import com.nimbusds.oauth2.sdk.http.HTTPResponse;

public class ConfigurationException extends PhantAuthException {
    private static final String CODE = "configuration_error";

    public ConfigurationException(final String description) {
        super(CODE, "Server configuration error: " + description,HTTPResponse.SC_SERVER_ERROR);
    }
}
