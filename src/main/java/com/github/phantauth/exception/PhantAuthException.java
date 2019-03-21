package com.github.phantauth.exception;

import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import lombok.Getter;

public class PhantAuthException extends RuntimeException {
    @Getter
    private final ErrorObject errorObject;

    public PhantAuthException(final String code, final String description, final int httpStatusCode) {
        super(String.format("%s - %s", code, description));
        this.errorObject = new ErrorObject(code, description, httpStatusCode);
    }
}
