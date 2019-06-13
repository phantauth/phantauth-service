package com.github.phantauth.test;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.phantauth.core.Tenant;
import com.github.phantauth.core.TokenKind;
import com.github.phantauth.core.User;
import com.github.phantauth.token.StorageToken;
import com.github.phantauth.token.UserTokenFactory;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.ResourceOwnerPasswordCredentialsGrant;
import com.nimbusds.oauth2.sdk.auth.Secret;
import lombok.experimental.Delegate;

public class TestUser implements User {

    @Delegate
    private final User impl;

    public TestUser(final User impl) {
        this.impl = impl;
    }

    @JsonIgnore
    public Secret getSecret() {
        return new Secret(getPassword());
    }

    @JsonIgnore
    public AuthorizationGrant getAuthorizationGrant() {
        return new ResourceOwnerPasswordCredentialsGrant(getSub(), getSecret());
    }

    @JsonIgnore
    public String getLoginToken() {
        final Tenant tenant = TestComponent.Holder.instance.getTenantRepository().getDefaultTenant();
        return TestComponent.Holder.instance.getUserTokenFactory().newStorageToken(StorageToken.Builder.of(tenant , TokenKind.LOGIN, getSub()));
    }
}
