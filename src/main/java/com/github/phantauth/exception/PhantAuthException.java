package com.github.phantauth.exception;

import com.nimbusds.oauth2.sdk.ErrorObject;
import lombok.Getter;

public class PhantAuthException extends RuntimeException {
    @Getter
    private final transient ErrorObject errorObject;

    public PhantAuthException(final String code, final String description, final int httpStatusCode) {
        super(String.format("%s - %s", code, description));
        this.errorObject = new ErrorObject(code, description, httpStatusCode);
    }
}
