package com.github.phantauth.service.auth;

import com.github.phantauth.exception.MissingParameterException;
import com.github.phantauth.exception.RequestMethodException;
import com.github.phantauth.flow.AuthorizationFlow;
import com.github.phantauth.core.Tenant;
import com.github.phantauth.resource.TenantRepository;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.service.AbstractServlet;
import com.github.phantauth.service.Request;
import com.github.phantauth.service.Response;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

@Singleton
public class WellKnownServlet extends AbstractServlet {
    private final AuthorizationFlow flow;
    static final String AUTHORIZATION_SERVER = "oauth-authorization-server";
    static final String OPENID_CONFIGURATION = "openid-configuration";
    static final String WEBFINGER = "webfinger";

    @Inject
    WellKnownServlet(final TenantRepository tenantRepository, final AuthorizationFlow flow) {
        super(Endpoint.WELL_KNOWN, tenantRepository);
        this.flow = flow;
    }

    @Override
    protected HTTPResponse doGet(final HTTPRequest req) {
        final Request.Param param = Request.param(req, endpoint);
        final Tenant tenant = getTenant(req);

        if ( param.subject == null ) {
            throw new MissingParameterException("configuration", "path");
        }

        final HTTPResponse response;
        switch (param.subject) {
            case AUTHORIZATION_SERVER:
                response = Response.json(flow.newAuthorizationServerConfig(tenant).toJSONObject());
                break;
            case OPENID_CONFIGURATION:
                response =  Response.json(flow.newOpenidConfiguration(tenant).toJSONObject());
                break;
            case WEBFINGER:
                response =  Response.jrd(flow.webfinger(tenant, req.getQueryParameters().get("resource"), req.getQueryParameters().get("rel")));
                break;
            default:
                return new HTTPResponse(HTTPResponse.SC_BAD_REQUEST);
        }
        return cache(response, (int) TimeUnit.HOURS.toSeconds(1));
    }

    @Override
    protected HTTPResponse doPost(final HTTPRequest req) {
        throw new RequestMethodException(req.getMethod().name());
    }
}
