package com.github.phantauth.test;

import com.github.phantauth.core.Tenant;
import com.github.phantauth.indie.IndieAuthResponseTypeValue;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.resource.Name;
import com.google.common.collect.ImmutableList;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.oauth2.sdk.ResponseMode;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.OIDCResponseTypeValue;
import com.nimbusds.openid.connect.sdk.SubjectType;
import com.nimbusds.openid.connect.sdk.claims.ClaimType;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

import java.net.URI;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

public class TestHelper  {

    private final TestComponent component;
    private final Tenant tenant;
    private final AtomicReference<OIDCProviderMetadata> meta;

    public TestHelper(final TestComponent component) {
        this(component, null);
    }

    public TestHelper(final TestComponent component, final Tenant tenant) {
        this.component = component;
        this.tenant = tenant == null ? component.getTenantRepository().getDefaultTenant() : tenant;
        meta = new AtomicReference<>();
    }

    public TestClient getClient() {
        return new TestClient(component.getClientFaker().get(tenant, Name.EMPTY));
    }

    public TestUser getUser() {
        return new TestUser(component.getUserFaker().get(tenant, Name.EMPTY));
    }

    public OIDCProviderMetadata getMeta() {
        OIDCProviderMetadata value = meta.get();
        if ( value == null ) {
            value = getMeta(tenant);
            meta.set(value);
        }
        return value;
    }

    private OIDCProviderMetadata getMeta(final Tenant tenant) {
        OIDCProviderMetadata instance = new OIDCProviderMetadata(new Issuer(tenant.getIssuer()), Collections.singletonList(SubjectType.PUBLIC), URI.create(tenant.getIssuer() + "/auth/jwks"));

        instance.setAuthorizationEndpointURI(Endpoint.AUTHORIZATION.toURI(tenant.getIssuer()));
        instance.setTokenEndpointURI(Endpoint.TOKEN.toURI(tenant.getIssuer()));
        instance.setUserInfoEndpointURI(Endpoint.USERINFO.toURI(tenant.getIssuer()));
        instance.setIntrospectionEndpointURI(Endpoint.INTROSPECTION.toURI(tenant.getIssuer()));
        instance.setRegistrationEndpointURI(Endpoint.REGISTER.toURI(tenant.getIssuer()));

        instance.setClaimTypes(Collections.singletonList(ClaimType.NORMAL));
        instance.setGrantTypes(ImmutableList.of(GrantType.AUTHORIZATION_CODE, GrantType.IMPLICIT, GrantType.PASSWORD, GrantType.REFRESH_TOKEN));
        instance.setClaims(ImmutableList.of("sub", "name", "given_name", "family_name", "middle_name", "nickname", "preferred_username", "profile", "picture", "website", "email", "email_verified", "gender", "birthdate", "zoneinfo", "locale", "phone_number", "phone_number_verified", "address", "updated_at", "me", "uid", "tenant"));
        instance.setSupportsClaimsParams(true);
        instance.setScopes(new Scope("openid profile email phone address uid"));

        instance.setIDTokenJWSAlgs(Collections.singletonList(JWSAlgorithm.RS256));
        instance.setUserInfoJWSAlgs(Collections.singletonList(JWSAlgorithm.RS256));

        instance.setTokenEndpointAuthMethods(ImmutableList.of(
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                ClientAuthenticationMethod.CLIENT_SECRET_POST,
                ClientAuthenticationMethod.CLIENT_SECRET_JWT,
                ClientAuthenticationMethod.PRIVATE_KEY_JWT));

        instance.setResponseModes(ImmutableList.of(ResponseMode.QUERY, ResponseMode.FRAGMENT));
        instance.setResponseTypes(ImmutableList.of(
                new ResponseType(ResponseType.Value.CODE), // Authorization code
                new ResponseType(OIDCResponseTypeValue.ID_TOKEN), // Implicit
                new ResponseType(OIDCResponseTypeValue.ID_TOKEN, ResponseType.Value.TOKEN), // Implicit
                new ResponseType(ResponseType.Value.CODE, OIDCResponseTypeValue.ID_TOKEN),  // Hybrid
                new ResponseType(ResponseType.Value.CODE, ResponseType.Value.TOKEN), // Hybrid
                new ResponseType(ResponseType.Value.CODE, OIDCResponseTypeValue.ID_TOKEN, ResponseType.Value.TOKEN), // Hybrid
                new ResponseType(IndieAuthResponseTypeValue.ID)  // IndieAuth
        ));

        return instance;
    }

    public URI getAt(Endpoint endpoint) {
        return endpoint.toURI(tenant.getIssuer());
    }
}
