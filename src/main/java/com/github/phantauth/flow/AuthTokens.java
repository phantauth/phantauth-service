package com.github.phantauth.flow;

import com.github.phantauth.core.TokenKind;
import com.google.common.collect.ImmutableMap;
import com.nimbusds.jwt.JWT;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.oauth2.sdk.token.Tokens;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import org.immutables.value.Value;

import java.util.Map;

@Value.Immutable
interface AuthTokens {

    AccessToken getAccessToken();

    JWT getIdToken();

    RefreshToken getRefreshToken();

    AuthorizationCode getAuthorizationCode();

    String getLoginToken();

    @Value.Lazy
    default OIDCTokens getOIDCTokens() {
        return new OIDCTokens(getIdToken(), getAccessToken(), getRefreshToken());
    }

    @Value.Lazy
    default Tokens getTokens() {
        return new Tokens(getAccessToken(), getRefreshToken());
    }

    @Value.Lazy
    default Map<String, Object> getCustomParams() {
        final ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
        builder.put(TokenKind.LOGIN.getName(), getLoginToken());
        if ( getIdToken() != null ) {
            builder.put(TokenKind.ID.getName(), getIdToken().serialize());
        }
        return builder.build();
    }

    class Builder extends AuthTokensValue.Builder {

    }
}
