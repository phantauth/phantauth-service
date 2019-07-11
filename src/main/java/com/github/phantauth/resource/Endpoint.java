package com.github.phantauth.resource;

import com.nimbusds.oauth2.sdk.http.CommonContentTypes;
import lombok.Getter;

import java.io.UnsupportedEncodingException;
import java.net.*;

public enum Endpoint {
    INDEX("/index/*"),
    USER("/user/*"),
    CLIENT("/client/*"),
    TEAM("/team/*"),
    FLEET("/fleet/*"),
    TENANT("/tenant/*"),
    DOMAIN("/domain/*"),
    TEST("/test/*"),
    WELL_KNOWN("/.well-known/*"),
    JWKS("/auth/jwks/*"),
    REGISTER("/auth/register/*"),
    INTROSPECTION("/auth/introspect/*"),
    AUTHORIZATION("/auth/authorize/*"),
    TOKEN("/auth/token/*"),
    USERINFO("/auth/userinfo/*"),
    AVATAR("/img/avatar/*"),
    LOGO("/img/logo/*"),
    ME("/me/*") {
        @Override
        public String toProfile(String issuer, String subject) {
            return issuer + "/~" + urlencode(subject);
        }
    };

    private static final String WILDCARD = "/*";

    @Getter
    private final String path;

    @Getter
    private final String pathSpec;

    Endpoint(final String pathSpec) {
        this.pathSpec = pathSpec;
        this.path = pathSpec.endsWith(WILDCARD) ? pathSpec.substring(0, pathSpec.length()- WILDCARD.length()) : pathSpec;
    }

    public String resolve(final String issuer) {
        return issuer + path;
    }

    public URL toURL(final URL issuer) {
        try {
            return new URL(resolve(issuer.toString()));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public URI toURI(final URI issuer) {
        return toURI(issuer.toString());
    }

    public URI toURI(String issuer) {
        try {
            return new URI(resolve(issuer));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String toOperation(final String issuer, final String subject, final String operation) {
        return resolve(issuer) + "/" + urlencode(subject) + "/" + operation;
    }

    public String toProfile(final String issuer, final String subject) {
        return toOperation(issuer, subject, "profile");
    }

    public String toResource(final String issuer, final String subject) {
        return resolve(issuer) + "/" + urlencode(subject);
    }

    private static String urlencode(final String value) {
        try {
            return URLEncoder.encode(value, CommonContentTypes.DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unsupported encoding");
        }
    }
}
