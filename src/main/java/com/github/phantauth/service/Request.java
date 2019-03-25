package com.github.phantauth.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;

import java.io.IOException;

public class Request {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
            .registerModule(new JavaTimeModule());

    private Request() {
        // no instances
    }

    public static <T> T body(final HTTPRequest request, final Class<T> type) throws IOException {
        return MAPPER.readValue(request.getQuery(), type);
    }

}

