package com.github.phantauth.flow;

import com.github.phantauth.core.Client;
import com.github.phantauth.core.User;
;
import com.github.phantauth.core.Tenant;
import com.github.phantauth.resource.Repository;
import com.github.phantauth.resource.TenantRepository;
;
import com.github.phantauth.token.ClientTokenFactory;
import com.github.phantauth.token.UserTokenFactory;
import com.nimbusds.oauth2.sdk.*;

import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.OIDCResponseTypeValue;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class PasswordFlow extends AbstractFlow {
    private static final ResponseType PASSWORD_RESPONSE_TYPE = new ResponseType(OIDCResponseTypeValue.ID_TOKEN, ResponseType.Value.TOKEN);

    @Inject
    public PasswordFlow(final TenantRepository tenantRepository, final Repository<User> userRepository, final UserTokenFactory userTokenFactory, final Repository<Client> clientRepository, final ClientTokenFactory clientTokenFactory) {
        super(tenantRepository, userRepository, userTokenFactory, clientRepository, clientTokenFactory);
    }

    @Override
    boolean implied(final AuthorizationRequest request) {
        return false;
    }

    @Override
    boolean implied(final TokenRequest request) {
        return request.getAuthorizationGrant() != null && request.getAuthorizationGrant().getType().equals(GrantType.PASSWORD);
    }

    @Override
    TokenResponse handle(final TokenRequest request) {

        final ResourceOwnerPasswordCredentialsGrant grant = (ResourceOwnerPasswordCredentialsGrant) request.getAuthorizationGrant();

        final Tenant tenant = getTenant(request);
        final User user = userRepository.get(tenant, grant.getUsername());

        final boolean passwordMatch = user.getPassword().equals(grant.getPassword().getValue());

        if ( ! passwordMatch ) {
            return new TokenErrorResponse(OAuth2Error.ACCESS_DENIED);
        }

        final AuthTokens tokens = newTokens(
                tenant,
                PASSWORD_RESPONSE_TYPE,
                user.getSub(),
                Nonce.parse(request.getCustomParameter("nonce")),
                request.getScope(),
                request.getClientID(),
                Integer.MAX_VALUE);

        return new AccessTokenResponse(tokens.getTokens(), tokens.getCustomParams());
    }
}
