package com.github.phantauth.service.auth;

import com.github.phantauth.core.Tenant;
import com.github.phantauth.exception.RequestMethodException;
import com.github.phantauth.flow.ProtectedResourceFlow;
import com.github.phantauth.resource.TenantRepository;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.service.AbstractServlet;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.client.ClientRegistrationErrorResponse;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.rp.*;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RegisterServlet extends AbstractServlet {

    private final ProtectedResourceFlow flow;

    @Inject
    RegisterServlet(final TenantRepository tenantRepository, final ProtectedResourceFlow flow) {
        super(Endpoint.REGISTER, tenantRepository);
        this.flow = flow;
    }

    @Override
    protected HTTPResponse handleGet(final HTTPRequest req) {
        throw new RequestMethodException(req.getMethod().name());
    }

    @Override
    protected HTTPResponse handlePost(final HTTPRequest req) {

        final OIDCClientRegistrationRequest request;
        try {
            request = OIDCClientRegistrationRequest.parse(req);
        } catch (ParseException e) {
            return new ClientRegistrationErrorResponse(e.getErrorObject()).toHTTPResponse();
        }

        final Tenant tenant = getTenant(req);

        return new OIDCClientInformationResponse(flow.registerClient(tenant, request.getOIDCClientMetadata(), req.getURL())).toHTTPResponse();
    }
}
