package com.github.phantauth.flow;

import com.github.phantauth.core.*;
import com.github.phantauth.indie.IndieAuthResponseTypeValue;
;
import com.github.phantauth.resource.Repository;
import com.github.phantauth.resource.TenantRepository;
;
import com.github.phantauth.token.ClientTokenFactory;
import com.github.phantauth.token.StorageToken;
import com.github.phantauth.token.UserTokenFactory;
import com.google.common.collect.ImmutableMap;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.token.Tokens;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class IndieAuthFlow extends AbstractFlow {

    @Inject
    public IndieAuthFlow(final TenantRepository tenantRepository, final Repository<User> userRepository, final UserTokenFactory userTokenFactory, final Repository<Client> clientRepository, final ClientTokenFactory clientTokenFactory) {
        super(tenantRepository, userRepository, userTokenFactory, clientRepository, clientTokenFactory);
    }

    @Override
    boolean implied(final AuthorizationRequest request) {
        return request.getResponseType() != null && request.getResponseType().contains(IndieAuthResponseTypeValue.ID);
    }

    @Override
    boolean implied(final TokenRequest request) {
        return request.getAuthorizationGrant() != null && request.getAuthorizationGrant().getType().equals(GrantType.AUTHORIZATION_CODE) && request.getCustomParameter(Claim.ME.getName()) != null;
    }

    @Override
    boolean impliesLogin(final AuthorizationRequest request) {
        return false;
    }

    @Override
    boolean impliesConsent(final AuthorizationRequest request) {
        return false;
    }

    @Override
    Response handle(final AuthorizationRequest request) {

        final String codeParam = request.getCustomParameter("code");

        if (codeParam == null) {
            final String me = request.getCustomParameter(Claim.ME.getName());

            final AuthorizationCode code = newAuthorizationCode(getTenant(request), me, null, Integer.MAX_VALUE, request.getScope());

            return new AuthorizationSuccessResponse(
                    request.getRedirectionURI(),
                    code,
                    null,
                    request.getState(),
                    request.getResponseMode());
        } else { // resposne type is id, this is IndieAuth authentication flow, code verification step
            final StorageToken token = userTokenFactory.parseStorageToken(codeParam, TokenKind.AUTHORIZATION);

            final Tenant tenant = token.getTenant() == null ? getTenant(request) : tenantRepository.get(token.getTenant());

            final User user = userRepository.get(tenant, token.getSubject());
            final Map<String, Object> params = ImmutableMap.of(Claim.ME.getName(), user.getMe());

            return new AccessTokenResponse(new Tokens(newAccessToken(tenant, token.getSubject(), token.getScopesAsScope()), null), params);
        }
    }

    @Override
    Response handle(final TokenRequest request) {

        if (request.getClientID() == null) {
            return new TokenErrorResponse(OAuth2Error.INVALID_CLIENT);
        }

        final AuthorizationCode code = ((AuthorizationCodeGrant) request.getAuthorizationGrant()).getAuthorizationCode();
        final StorageToken token = userTokenFactory.parseStorageToken(code.getValue(), TokenKind.AUTHORIZATION);

        final Tenant tenant = token.getTenant() == null ? getTenant(request) : tenantRepository.get(token.getTenant());

        final Map<String, Object> params = ImmutableMap.of(
                "scope", token.getScopesAsScope().toString(),
                Claim.ME.getName(), request.getCustomParameter(Claim.ME.getName())
        );

        return new AccessTokenResponse(new Tokens(newAccessToken(tenant, token.getSubject(), token.getScopesAsScope()), null), params);
    }
}


