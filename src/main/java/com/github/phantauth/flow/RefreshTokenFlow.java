package com.github.phantauth.flow;

import com.github.phantauth.core.Client;
import com.github.phantauth.core.TokenKind;
import com.github.phantauth.core.User;
import com.github.phantauth.resource.Repository;
import com.github.phantauth.resource.TenantRepository;
import com.github.phantauth.token.ClientTokenFactory;
import com.github.phantauth.token.StorageToken;
import com.github.phantauth.token.UserTokenFactory;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.openid.connect.sdk.OIDCResponseTypeValue;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RefreshTokenFlow extends AbstractFlow {
    private static final ResponseType REFRESH_TOKEN_RESPONSE_TYPE = new ResponseType(OIDCResponseTypeValue.ID_TOKEN, ResponseType.Value.TOKEN);

    @Inject
    public RefreshTokenFlow(final TenantRepository tenantRepository, final Repository<User> userRepository, final UserTokenFactory userTokenFactory, final Repository<Client> clientRepository, final ClientTokenFactory clientTokenFactory) {
        super(tenantRepository, userRepository, userTokenFactory, clientRepository, clientTokenFactory);
    }

    @Override
    boolean implied(final AuthorizationRequest request) {
        return false;
    }

    @Override
    boolean implied(final TokenRequest request) {
        return request.getAuthorizationGrant() != null && request.getAuthorizationGrant().getType().equals(GrantType.REFRESH_TOKEN);
    }

    @Override
    Response handle(final AuthorizationRequest request) {
        return new AuthorizationErrorResponse(request.getRedirectionURI(), OAuth2Error.UNSUPPORTED_RESPONSE_TYPE, request.getState(), request.getResponseMode());
    }

    @Override
    TokenResponse handle(final TokenRequest request) {

        final RefreshTokenGrant grant = (RefreshTokenGrant) request.getAuthorizationGrant();
        final StorageToken token = userTokenFactory.parseStorageToken(grant.getRefreshToken().toString(), TokenKind.REFRESH);

        final AuthTokens tokens = newTokens(
                getTenant(request),
                REFRESH_TOKEN_RESPONSE_TYPE,
                token.getSubject(),
                token.getNonceAsNonce(),
                token.getScopesAsScope(),
                request.getClientAuthentication().getClientID(),
                token.getMaxAge());

        return new AccessTokenResponse(tokens.getTokens(), tokens.getCustomParams());
    }
}
