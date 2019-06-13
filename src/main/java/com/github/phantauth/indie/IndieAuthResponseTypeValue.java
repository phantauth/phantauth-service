package com.github.phantauth.indie;

import com.nimbusds.oauth2.sdk.ResponseType;

public class IndieAuthResponseTypeValue {

    /**
     * ID response type (me property).
     */
    public static final ResponseType.Value ID = new ResponseType.Value("id");

    private IndieAuthResponseTypeValue() {
    }
}
