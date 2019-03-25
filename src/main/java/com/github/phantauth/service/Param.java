package com.github.phantauth.service;

import com.github.phantauth.exception.ConfigurationException;
import com.github.phantauth.resource.Endpoint;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import org.immutables.value.Value;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringTokenizer;

@Value.Immutable
public abstract class Param {
    private static final String PATH_SEPARATOR = "/";

    public abstract String getQuery();
    public abstract Map<String, String> getParams();
    public abstract String getSubject();
    public abstract String getOperation();
    public abstract String getArgument();

    public static class Builder extends ParamValue.BuilderBase {

    }

    public static Param build(final HTTPRequest request, final Endpoint endpoint) {

        final Builder builder = new Builder();

        builder.setQuery(request.getQuery());
        builder.putAllParams(request.getQueryParameters());

        final String url = request.getURL().toExternalForm();
        final int idx = url.indexOf(endpoint.getPath()) + endpoint.getPath().length();

        if (idx < 0 || url.length() <= idx) {
            return builder.build();
        }

        final String pathinfo;

        try {
            pathinfo = URLDecoder.decode(url.substring(idx), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new ConfigurationException("unsupported encoding: UTF8");
        }

        final StringTokenizer tokenizer = new StringTokenizer(pathinfo, PATH_SEPARATOR);
        int count = tokenizer.countTokens();

        if (count == 0) {
            return builder.build();
        }

        builder.setSubject(tokenizer.nextToken());

        if (--count == 0) {
            return builder.build();
        }

        builder.setOperation(tokenizer.nextToken());

        if (--count == 0) {
            return builder.build();
        }

        builder.setArgument(tokenizer.nextToken());
        return builder.build();
    }
}
