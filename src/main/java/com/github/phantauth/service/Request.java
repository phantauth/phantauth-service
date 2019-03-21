package com.github.phantauth.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.phantauth.exception.ConfigurationException;
import com.github.phantauth.resource.Endpoint;
import com.google.common.base.Charsets;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import lombok.ToString;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.StringTokenizer;

public class Request {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
            .registerModule(new JavaTimeModule());

    private static final String  PATH_SEPARATOR = "/";

    @ToString
    public static class Param {
        public String subject;
        public String operation;
        public String argument;
        public String query;
        public Map<String, String> params;
    }

    public static Param param(final HTTPRequest request, final Endpoint endpoint) {

        final Param param = new Param();
        param.query = request.getQuery();
        param.params = request.getQueryParameters();

        final String url = request.getURL().toExternalForm();
        final int idx = url.indexOf(endpoint.getPath()) + endpoint.getPath().length();

        if ( idx < 0 || url.length() <= idx ) {
            return param;
        }

        final String pathinfo;

        try {
            pathinfo = URLDecoder.decode(url.substring(idx), Charsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new ConfigurationException("unsupported encoding: UTF8");
        }

        final StringTokenizer tokenizer = new StringTokenizer(pathinfo, PATH_SEPARATOR);
        int count = tokenizer.countTokens();
        if (count == 0) {
            return param;
        }
        param.subject = tokenizer.nextToken();

        if (--count == 0) {
            return param;
        }
        param.operation = tokenizer.nextToken();
        if (--count == 0) {
            return param;
        }
        param.argument = tokenizer.nextToken();
        return param;
    }

    public static <T> T body(final HTTPRequest request, final Class<T> type) throws IOException {
        return MAPPER.readValue(request.getQuery(), type);
    }
}

