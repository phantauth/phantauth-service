package com.github.phantauth.flow;

import com.github.phantauth.core.*;
import com.github.phantauth.core.Scope;
import com.github.phantauth.resource.Repository;
import com.github.phantauth.resource.TenantRepository;
import com.github.phantauth.token.ClientTokenFactory;
import com.github.phantauth.token.StorageToken;
import com.github.phantauth.token.UserTokenFactory;
import com.google.common.base.Strings;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.openid.connect.sdk.*;
import com.nimbusds.openid.connect.sdk.claims.AccessTokenHash;
import com.nimbusds.openid.connect.sdk.claims.CodeHash;

import java.text.ParseException;

abstract class AbstractFlow {
    protected static final String PARAM_TENANT = "tenant";
    protected static final String PARAM_ISSUER = "issuer";
    protected static final String PARAM_CONSENT = "consent";

    protected final TenantRepository tenantRepository;
    protected final Repository<User> userRepository;
    protected final UserTokenFactory userTokenFactory;
    protected final Repository<Client> clientRepository;
    protected final ClientTokenFactory clientTokenFactory;

    AbstractFlow(final TenantRepository tenantRepository, final Repository<User> userRepository, final UserTokenFactory userTokenFactory, final Repository<Client> clientRepository, final ClientTokenFactory clientTokenFactory) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.userTokenFactory = userTokenFactory;
        this.clientRepository = clientRepository;
        this.clientTokenFactory = clientTokenFactory;
    }

    AuthorizationCode newAuthorizationCode(final Tenant tenant, final String subject, final Nonce nonce, final int maxAge, final com.nimbusds.oauth2.sdk.Scope scope) {
        return new AuthorizationCode(userTokenFactory.newStorageToken(StorageToken.Builder.of(tenant, TokenKind.AUTHORIZATION, subject, nonce, maxAge, scope)));
    }

    AccessToken newAccessToken(final Tenant tenant, final String subject, final com.nimbusds.oauth2.sdk.Scope scope) {
        return new BearerAccessToken(userTokenFactory.newStorageToken(StorageToken.Builder.of(tenant, TokenKind.ACCESS, subject, scope)));
    }

    RefreshToken newRefreshToken(final Tenant tenant, final String subject, final Nonce nonce, final int maxAge, final com.nimbusds.oauth2.sdk.Scope scope) {
        return new RefreshToken(userTokenFactory.newStorageToken(StorageToken.Builder.of(tenant, TokenKind.REFRESH, subject, nonce, maxAge, scope)));
    }

    JWT newIdToken(final Tenant tenant, final String subject, final ClientID clientId, final Nonce nonce, final String accessTokenHash, final String authorizationCodeHash, final int maxAge, final com.nimbusds.oauth2.sdk.Scope scope) {
        final User user = userRepository.get(tenant, subject);

        return userTokenFactory.newIdToken(
                tenant,
                user,
                clientId.getValue(),
                nonce == null ? null : nonce.getValue(),
                accessTokenHash,
                authorizationCodeHash,
                maxAge,
                scope == null ? Scope.getDefaultScopes(): Scope.split(scope.toString())
        );
    }

    AuthTokens newTokens(final AuthenticationRequest request) {
        final JWTClaimsSet idTokenHint = getIdTokenHint(request);
        return newTokens(
                getTenant(request),
                request.getResponseType(),
                getSubject(request, idTokenHint),
                request.getNonce(),
                getScopeWithConsent(request, idTokenHint),
                request.getClientID(),
                request.getMaxAge());
    }

    String getSubject(final AuthorizationRequest request, final JWTClaimsSet idTokenHint) {
        return idTokenHint == null ? parseLoginToken(request).getSubject() : idTokenHint.getSubject();
    }

    com.nimbusds.oauth2.sdk.Scope getScopeWithConsent(final AuthorizationRequest request, final JWTClaimsSet idTokenHint) {

        if ( idTokenHint != null ) {
            try {
                return com.nimbusds.oauth2.sdk.Scope.parse(idTokenHint.getStringClaim("scope"));
            } catch (ParseException e) {
                // do nothing
            }
        }

        com.nimbusds.oauth2.sdk.Scope consent = com.nimbusds.oauth2.sdk.Scope.parse(request.getCustomParameter(PARAM_CONSENT));
        com.nimbusds.oauth2.sdk.Scope scope = request.getScope();
        scope.retainAll(consent);
        return scope;
    }

    AuthTokens newTokens(final Tenant tenant, final ResponseType responseType, final AuthorizationCode code, final ClientID clientId) {
        final StorageToken token = userTokenFactory.parseStorageToken(code.getValue(), TokenKind.AUTHORIZATION);
        final Tenant tokenTenant = token.getTenant() == null ? tenant : tenantRepository.get(token.getTenant());

        return newTokens(tokenTenant, responseType, token.getSubject(), token.getNonceAsNonce(), token.getScopesAsScope(), clientId, token.getMaxAge());
    }

    AuthTokens newTokens(final Tenant tenant, final ResponseType responseType, final String subject, final Nonce nonce, final com.nimbusds.oauth2.sdk.Scope scope, final ClientID clientId, final int maxAge) {
        final AuthorizationCode code = responseType.contains(ResponseType.Value.CODE) ? newAuthorizationCode(tenant, subject, nonce, maxAge, scope) : null;
        final AccessToken accessToken = responseType.contains(ResponseType.Value.TOKEN) ? newAccessToken(tenant, subject, scope) : null;
        final RefreshToken refreshToken = responseType.contains(ResponseType.Value.TOKEN) ? newRefreshToken(tenant, subject, nonce, maxAge, scope) : null;
        final String loginToken = responseType.contains(ResponseType.Value.TOKEN) ? userTokenFactory.newStorageToken(StorageToken.Builder.of(tenant, TokenKind.LOGIN, subject)) : null;

        final JWT idToken = responseType.contains(OIDCResponseTypeValue.ID_TOKEN) && clientId != null
                ? newIdToken(
                        tenant,
                        subject,
                        clientId,
                        nonce,
                        AccessTokenHash.isRequiredInIDTokenClaims(responseType) ? AccessTokenHash.compute(accessToken, JWSAlgorithm.HS256).getValue() : null,
                        CodeHash.isRequiredInIDTokenClaims(responseType) ? CodeHash.compute(code, JWSAlgorithm.HS256).getValue() : null,
                        maxAge,
                        scope)
                : null;

        return new AuthTokens.Builder()
                .setAuthorizationCode(code)
                .setAccessToken(accessToken)
                .setIdToken(idToken)
                .setRefreshToken(refreshToken)
                .setLoginToken(loginToken)
                .build();
    }

    StorageToken parseLoginToken(final AuthorizationRequest request) {
        return parseLoginToken(request.getCustomParameter(TokenKind.LOGIN.getName()));
    }

    StorageToken parseLoginToken(final String token) {
        return userTokenFactory.parseStorageToken(token, TokenKind.LOGIN);
    }

    abstract boolean implied(final AuthorizationRequest request);

    abstract boolean implied(final TokenRequest request);

    abstract Response handle(final AuthorizationRequest request);

    abstract Response handle(final TokenRequest request);

    boolean impliesLogin(final AuthorizationRequest request) {
        final JWTClaimsSet hint = getIdTokenHint(request);

        boolean implies = hint == null;

        if ( implies ) {
            final String tokenParam = request.getCustomParameter(TokenKind.LOGIN.getName());

            if (tokenParam != null) {
                try {
                    parseLoginToken(tokenParam);
                    implies = false;
                } catch (Exception x) {
                    // nothig to do
                }
            }
        }

        if ( request instanceof  AuthenticationRequest ) {
            final Prompt prompt = ((AuthenticationRequest) request).getPrompt();
            implies |= (prompt != null && prompt.contains(Prompt.Type.LOGIN));
        }

        return implies;
    }

    boolean impliesConsent(final AuthorizationRequest request) {
        final JWTClaimsSet hint = getIdTokenHint(request);

        boolean implies = hint == null && Strings.isNullOrEmpty(request.getCustomParameter(PARAM_CONSENT));

        if ( !implies && request instanceof  AuthenticationRequest ) {
            final Prompt prompt = ((AuthenticationRequest) request).getPrompt();
            implies |= (prompt != null && prompt.contains(Prompt.Type.CONSENT));
        }

        return implies;
    }

    JWTClaimsSet getIdTokenHint(final AuthorizationRequest request) {
        if ( request instanceof  AuthenticationRequest ) {
            final JWT jwt = ((AuthenticationRequest) request).getIDTokenHint();
            try {
                return jwt != null && !isExpired(jwt) && userTokenFactory.verifyIDToken(jwt) ? jwt.getJWTClaimsSet() : null;
            } catch (ParseException e) {
                return null;
            }
        }
        return null;
    }

    boolean isExpired(final JWT jwt) {
        try {
            return jwt.getJWTClaimsSet().getExpirationTime().getTime() <= System.currentTimeMillis();
        } catch (ParseException e) {
            return true;
        }
    }

    Tenant getTenant(final AuthorizationRequest request) {
        return tenantRepository.get(request.getCustomParameter(PARAM_TENANT));
    }

    Tenant getTenant(final TokenRequest request) {
        return tenantRepository.get(request.getCustomParameter(PARAM_TENANT));
    }
}
