package com.github.phantauth.resource;

import com.nimbusds.oauth2.sdk.http.CommonContentTypes;
import lombok.Getter;

import java.io.UnsupportedEncodingException;
import java.net.*;

public enum Endpoint {
    INDEX(Endpoint.INDEX_PATH_SPEC),
    USER(Endpoint.USER_PATH_SPEC),
    CLIENT(Endpoint.CLIENT_PATH_SPEC),
    TEAM(Endpoint.TEAM_PATH_SPEC),
    FLEET(Endpoint.FLEET_PATH_SPEC),
    TENANT(Endpoint.TENANT_PATH_SPEC),
    DOMAIN(Endpoint.DOMAIN_PATH_SPEC),
    TEST(Endpoint.TEST_PATH_SPEC),
    WELL_KNOWN(Endpoint.WELL_KNOWN_PATH_SPEC),
    JWKS(Endpoint.JWKS_PATH_SPEC),
    REGISTER(Endpoint.REGISTER_PATH_SPEC),
    INTROSPECTION(Endpoint.INTROSPECTION_PATH_SPEC),
    AUTHORIZATION(Endpoint.AUTHORIZATION_PATH_SPEC),
    TOKEN(Endpoint.TOKEN_PATH_SPEC),
    USERINFO(Endpoint.USERINFO_PATH_SPEC),
    ME(Endpoint.ME_PATH_SPEC) {
        @Override
        public String toProfile(String issuer, String subject) {
            return issuer + "/~" + urlencode(subject);
        }
    };

    public static final String INDEX_PATH_SPEC = "/index/*";
    public static final String USER_PATH_SPEC = "/user/*";
    public static final String CLIENT_PATH_SPEC = "/client/*";
    public static final String TEAM_PATH_SPEC = "/team/*";
    public static final String FLEET_PATH_SPEC = "/fleet/*";
    public static final String TENANT_PATH_SPEC = "/tenant/*";
    public static final String DOMAIN_PATH_SPEC = "/domain/*";
    public static final String TEST_PATH_SPEC = "/test/*";
    public static final String WELL_KNOWN_PATH_SPEC = "/.well-known/*";
    public static final String JWKS_PATH_SPEC = "/auth/jwks/*";
    public static final String REGISTER_PATH_SPEC = "/auth/register/*";
    public static final String INTROSPECTION_PATH_SPEC = "/auth/introspect/*";
    public static final String AUTHORIZATION_PATH_SPEC = "/auth/authorize/*";
    public static final String TOKEN_PATH_SPEC = "/auth/token/*";
    public static final String USERINFO_PATH_SPEC = "/auth/userinfo/*";
    public static final String ME_PATH_SPEC = "/me/*";

    public static final String WILDCHAR = "/*";

    @Getter
    private final String path;

    @Getter
    private final String pathSpec;

    Endpoint(final String pathSpec) {
        this.pathSpec = pathSpec;
        this.path = pathSpec.endsWith(WILDCHAR) ? pathSpec.substring(0, pathSpec.length()-WILDCHAR.length()) : pathSpec;
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
