package com.github.phantauth.service.auth;

import com.github.phantauth.exception.RequestMethodException;
import com.github.phantauth.resource.TenantRepository;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.service.AbstractServlet;
import com.github.phantauth.service.Response;
import com.github.phantauth.token.TokenManager;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class JWKSServlet extends AbstractServlet {
    private final TokenManager tokenManager;

    @Inject
    JWKSServlet(final TenantRepository tenantRepository, final TokenManager tokenManager) {
        super(Endpoint.JWKS, tenantRepository);
        this.tokenManager = tokenManager;
    }

    @Override
    protected HTTPResponse doGet(HTTPRequest req) {
        return Response.json(tokenManager.getPublicKeySet().toJSONObject(true));
    }

    @Override
    protected HTTPResponse doPost(HTTPRequest req) {
        throw new RequestMethodException(req.getMethod().name());
    }
}
