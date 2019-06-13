package com.github.phantauth.test;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.phantauth.core.Client;
import com.github.phantauth.flow.ProtectedResourceFlow;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.ClientSecretPost;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientMetadata;
import lombok.experimental.Delegate;

public class TestClient implements Client {

    @Delegate
    private final Client impl;

    public TestClient(final Client impl) {
        this.impl = impl;
    }

    @JsonIgnore
    public ClientID getClientID() {
        return new ClientID(getClientId());
    }

    @JsonIgnore
    public Secret getSecret() {
        return new Secret(getClientSecret());
    }

    @JsonIgnore
    public ClientSecretBasic getSecretBasic() {
        return new ClientSecretBasic(getClientID(), getSecret());
    }

    @JsonIgnore
    public ClientSecretPost getSecretPost() {
        return new ClientSecretPost(getClientID(), getSecret());
    }

    @JsonIgnore
    public OIDCClientMetadata getMeta() {
        return ProtectedResourceFlow.newClientMetadata(this);
    }
}
