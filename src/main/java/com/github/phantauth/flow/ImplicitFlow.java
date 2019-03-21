package com.github.phantauth.flow;


import com.github.phantauth.core.Client;
import com.github.phantauth.core.User;
import com.github.phantauth.resource.Repository;
import com.github.phantauth.resource.TenantRepository;
;
import com.github.phantauth.token.ClientTokenFactory;
import com.github.phantauth.token.UserTokenFactory;
import com.nimbusds.oauth2.sdk.AuthorizationRequest;
import com.nimbusds.oauth2.sdk.Response;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.openid.connect.sdk.*;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ImplicitFlow extends AbstractFlow {

    @Inject
    public ImplicitFlow(final TenantRepository tenantRepository, final Repository<User> userRepository, final UserTokenFactory userTokenFactory, final Repository<Client> clientRepository, final ClientTokenFactory clientTokenFactory) {
        super(tenantRepository, userRepository, userTokenFactory, clientRepository, clientTokenFactory);
    }

    @Override
    boolean implied(final AuthorizationRequest request) {
        return request.getResponseType() != null && request.getResponseType().impliesImplicitFlow();
    }

    @Override
    boolean implied(final TokenRequest request) {
        return false;
    }

    @Override
    Response handle(final AuthorizationRequest request) {
        return handle((AuthenticationRequest)request);
    }

    Response handle(final AuthenticationRequest request) {
        final AuthTokens tokens = newTokens(request);

        return new AuthenticationSuccessResponse(
                request.getRedirectionURI(),
                null,
                tokens.getIdToken(),
                tokens.getAccessToken(),
                request.getState(),
                null,
                request.getResponseMode());
    }
}
