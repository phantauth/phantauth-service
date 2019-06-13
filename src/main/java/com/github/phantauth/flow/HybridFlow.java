package com.github.phantauth.flow;

import com.github.phantauth.core.Claim;
import com.github.phantauth.core.Client;
import com.github.phantauth.core.User;
import com.github.phantauth.resource.Repository;
import com.github.phantauth.resource.TenantRepository;
import com.github.phantauth.token.ClientTokenFactory;
import com.github.phantauth.token.UserTokenFactory;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.openid.connect.sdk.*;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class HybridFlow extends AbstractFlow {
    private static final ResponseType TOKEN_RESPONSE_TYPE = new ResponseType(OIDCResponseTypeValue.ID_TOKEN, ResponseType.Value.TOKEN);

    @Inject
    public HybridFlow(final TenantRepository tenantRepository, final Repository<User> userRepository, final UserTokenFactory userTokenFactory, final Repository<Client> clientRepository, final ClientTokenFactory clientTokenFactory) {
        super(tenantRepository, userRepository, userTokenFactory, clientRepository, clientTokenFactory);
    }

    @Override
    boolean implied(final AuthorizationRequest request) {
        return request.getResponseType() != null && request.getResponseType().impliesHybridFlow();
    }

    @Override
    boolean implied(final TokenRequest request) {
        return request.getAuthorizationGrant() != null && request.getAuthorizationGrant().getType().equals(GrantType.AUTHORIZATION_CODE) && request.getCustomParameter(Claim.ME.getName()) == null;
    }

    @Override
    Response handle(final AuthorizationRequest request) {
        return handle((AuthenticationRequest)request);
    }

    Response handle(final AuthenticationRequest request) {
        final AuthTokens tokens = newTokens(request);

        return new AuthenticationSuccessResponse(
                request.getRedirectionURI(),
                tokens.getAuthorizationCode(),
                tokens.getIdToken(),
                tokens.getAccessToken(),
                request.getState(),
                null,
                request.getResponseMode());
    }

    @Override
    Response handle(final TokenRequest request) {

        if (request.getClientID() == null && (request.getClientAuthentication() == null || request.getClientAuthentication().getClientID() == null)) {
            return new TokenErrorResponse(OAuth2Error.INVALID_CLIENT);
        }

        final ClientID clientID = request.getClientID() == null ? request.getClientAuthentication().getClientID() : request.getClientID();

        final AuthorizationCode code = ((AuthorizationCodeGrant) request.getAuthorizationGrant()).getAuthorizationCode();

        final AuthTokens tokens = newTokens(getTenant(request), TOKEN_RESPONSE_TYPE, code, clientID);

        return new OIDCTokenResponse(tokens.getOIDCTokens());
    }
}
