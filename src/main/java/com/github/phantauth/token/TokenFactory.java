package com.github.phantauth.token;

import com.github.phantauth.core.Scope;
import com.github.phantauth.core.TokenKind;
import com.github.phantauth.core.Tenant;
import com.github.phantauth.resource.Producer;
import com.nimbusds.jwt.JWT;

public interface TokenFactory<T> extends Producer<T> {
    String newStorageToken(StorageToken storage);
    StorageToken parseStorageToken(String token, TokenKind kind);

    String newSelfieToken(T from);
    T parseSelfieToken(String token);

    String newPlainToken(T from);
    T parsePlainToken(String token);

    JWT newIdToken(Tenant tenant, T from, String audience, String nonce, String accessTokenHash, String authorizationCodeHash, int maxAge, Scope... scopes);
}
