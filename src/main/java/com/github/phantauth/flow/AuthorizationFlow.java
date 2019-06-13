package com.github.phantauth.flow;

import com.github.phantauth.core.Claim;
import com.github.phantauth.exception.InvalidParameterException;
import com.github.phantauth.exception.MissingParameterException;
import com.github.phantauth.indie.IndieAuthResponseTypeValue;
import com.github.phantauth.core.Tenant;
import com.github.phantauth.resource.Endpoint;
import com.google.common.collect.ImmutableList;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.as.AuthorizationServerMetadata;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.*;
import com.nimbusds.openid.connect.sdk.claims.ClaimType;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class AuthorizationFlow {

    private static final List<ResponseType> RESPONSE_TYPES = ImmutableList.of(
            new ResponseType(ResponseType.Value.CODE), // Authorization code
            new ResponseType(OIDCResponseTypeValue.ID_TOKEN), // Implicit
            new ResponseType(OIDCResponseTypeValue.ID_TOKEN, ResponseType.Value.TOKEN), // Implicit
            new ResponseType(ResponseType.Value.CODE, OIDCResponseTypeValue.ID_TOKEN),  // Hybrid
            new ResponseType(ResponseType.Value.CODE, ResponseType.Value.TOKEN), // Hybrid
            new ResponseType(ResponseType.Value.CODE, OIDCResponseTypeValue.ID_TOKEN, ResponseType.Value.TOKEN), // Hybrid
            new ResponseType(IndieAuthResponseTypeValue.ID)  // IndieAuth
    );
    private static final List<GrantType> GRANT_TYPES = ImmutableList.of(GrantType.AUTHORIZATION_CODE, GrantType.IMPLICIT, GrantType.PASSWORD, GrantType.REFRESH_TOKEN);
    private static final String REL_ISSUER = "http://openid.net/specs/connect/1.0/issuer";

    private final AbstractFlow[] flows;
    private final DefaultFlow defaultFlow;

    @Inject
    public AuthorizationFlow(final AuthorizationCodeFlow codeFlow, final ImplicitFlow implicitFlow, final HybridFlow hybridFlow, final PasswordFlow passwordFlow, final RefreshTokenFlow refreshTokenFlow, final IndieAuthFlow indieFlow) {
        this.flows = new AbstractFlow[] {codeFlow, implicitFlow, hybridFlow, passwordFlow, refreshTokenFlow, indieFlow};
        this.defaultFlow = new DefaultFlow();
    }

    AbstractFlow getFlow(final AuthorizationRequest request) {
        for(AbstractFlow flow : flows) {
            if ( flow.implied(request) ) {
                return flow;
            }
        }
        return defaultFlow;
    }

    AbstractFlow getFlow(final TokenRequest request) {
        for(AbstractFlow flow : flows) {
            if ( flow.implied(request) ) {
                return flow;
            }
        }
        return defaultFlow;
    }

    public boolean impliesLogin(final AuthorizationRequest request) {
        return getFlow(request).impliesLogin(request);
    }

    public boolean impliesConsent(final AuthorizationRequest request) {
        return getFlow(request).impliesConsent(request);
    }

    public Response handle(final AuthorizationRequest request) {

        return getFlow(request).handle(request);
    }

    public Response handle(final TokenRequest request) {

        return getFlow(request).handle(request);
    }

    public JSONObject webfinger(final Tenant tenant, final String resource, final String rel) {
        if ( resource == null ) {
            throw new MissingParameterException("resource", "query");
        }

        if ( rel == null ) {
            throw new MissingParameterException("rel", "query");
        }

        if ( ! REL_ISSUER.equals(rel) ) {
            throw new InvalidParameterException("rel");
        }

        final JSONObject ret = new JSONObject();
        ret.put("subject", resource);
        final JSONObject item = new JSONObject();
        item.put("rel", rel);
        item.put("href", tenant.getIssuer());
        final JSONArray array = new JSONArray();
        array.appendElement(item);
        ret.put("links", array);

        return ret;
    }

    public OIDCProviderMetadata newOpenidConfiguration(final Tenant tenant) {
        final OIDCProviderMetadata meta = new OIDCProviderMetadata(new Issuer(tenant.getIssuer()), Collections.singletonList(SubjectType.PUBLIC), Endpoint.JWKS.toURI(tenant.getIssuer()));
        fillCommonConfiguration(tenant, meta);

        meta.setClaimTypes(Collections.singletonList(ClaimType.NORMAL));
        meta.setClaims(Arrays.stream(Claim.values()).map(Claim::getName).collect(Collectors.toList()));
        meta.setSupportsClaimsParams(true);

        meta.setIDTokenJWSAlgs(Collections.singletonList(JWSAlgorithm.RS256));


        meta.setUserInfoEndpointURI(Endpoint.USERINFO.toURI(tenant.getIssuer()));
        meta.setUserInfoJWSAlgs(Collections.singletonList(JWSAlgorithm.RS256));

        return meta;
    }

    public AuthorizationServerMetadata newAuthorizationServerConfig(final Tenant tenant) {
        final AuthorizationServerMetadata meta = new AuthorizationServerMetadata(new Issuer(tenant.getIssuer()));
        fillCommonConfiguration(tenant, meta);
        return meta;
    }

    private void fillCommonConfiguration(final Tenant tenant, final AuthorizationServerMetadata meta) {
        final String issuer = tenant.getIssuer();
        meta.applyDefaults();
        meta.setGrantTypes(GRANT_TYPES);
        meta.setResponseTypes(RESPONSE_TYPES);
        meta.setRegistrationEndpointURI(Endpoint.REGISTER.toURI(issuer));
        meta.setIntrospectionEndpointURI(Endpoint.INTROSPECTION.toURI(issuer));
        meta.setAuthorizationEndpointURI(Endpoint.AUTHORIZATION.toURI(issuer));
        meta.setTokenEndpointURI(Endpoint.TOKEN.toURI(issuer));
        meta.setTokenEndpointAuthMethods(ImmutableList.of(
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                ClientAuthenticationMethod.CLIENT_SECRET_POST,
                ClientAuthenticationMethod.CLIENT_SECRET_JWT,
                ClientAuthenticationMethod.PRIVATE_KEY_JWT));
    }

    static class DefaultFlow extends AbstractFlow {
        DefaultFlow() {
            super(null, null, null, null, null);
        }

        @Override
        boolean implied(final AuthorizationRequest request) {
            return false;
        }

        @Override
        boolean implied(final TokenRequest request) {
            return false;
        }

        @Override
        Response handle(final AuthorizationRequest request) {
            return new AuthorizationErrorResponse(request.getRedirectionURI(), OAuth2Error.UNSUPPORTED_RESPONSE_TYPE, request.getState(), request.getResponseMode());
        }

        @Override
        Response handle(final TokenRequest request) {
            return new TokenErrorResponse(OAuth2Error.UNSUPPORTED_GRANT_TYPE);
        }

    }
}
