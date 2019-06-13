package com.github.phantauth.token;

import com.github.phantauth.core.*;
import com.github.phantauth.resource.Producer;
import com.google.common.base.Preconditions;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import net.minidev.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Singleton
public class UserTokenFactory extends AbstractTokenFactory<User> implements Producer<User> {
    private static final String NONCE = "nonce";
    private static final String ACCESS_TOKEN_HASH = "at_hash";
    private static final String CODE_HASH = "c_hash";
    private static final String AUTH_TIME = "auth_time";
    private static final String EMPTY_STRING = "";

    @Inject
    public UserTokenFactory(final TokenManager tokenManager) {
        super(User.class, tokenManager);
    }

    @Override
    public String newStorageToken(final StorageToken storage) {
        return newStorageToken(storage, TokenKind.ACCESS, TokenKind.REFRESH, TokenKind.AUTHORIZATION, TokenKind.LOGIN);
    }

    @Override
    public String newSelfieToken(final User user) {
        Preconditions.checkNotNull(user);
        final User nosub = new User.Builder().from(user).setSub(EMPTY_STRING).build();
        return newSelfieToken("user", nosub);
    }

    /**
     * OpenID Connect ID Token  The primary extension that OpenID Connect makes to OAuth 2.0 to enable End-Users to be Authenticated is the ID Token data structure. The ID Token is a security token that contains Claims about the Authentication of an End-User by an Authorization Server when using a Client, and potentially other requested Claims. The ID Token is represented as a JSON Web Token.
     *
     * @return idToken
     **/
    @Override
    public JWT newIdToken(final Tenant tenant, final User user, final String audience, final String nonce, final String accessTokenHash, final String authorizationCodeHash, final int maxAge, final Scope... scopes) {
        Preconditions.checkNotNull(user);
        final Date now = new Date();
        final JWTClaimsSet.Builder builder = getIdTokenBuilder(user, scopes)
                .claim(Claim.TENANT.getName(), tenant.getSub())
                .claim(TOKEN_KIND, TokenKind.ID.getName())
                .audience(audience)
                .issuer(getIssuer(tenant))
                .issueTime(now)
                .claim(AUTH_TIME, TimeUnit.MILLISECONDS.toSeconds(now.getTime()))
                .expirationTime(new Date(now.getTime() + Math.min(TokenKind.ID.getMaxAge(), TimeUnit.SECONDS.toMillis(maxAge))));

        if ( nonce != null ) {
            builder.claim(NONCE, nonce);
        }

        if ( accessTokenHash != null ) {
            builder.claim(ACCESS_TOKEN_HASH, accessTokenHash);
        }

        if ( authorizationCodeHash != null ) {
            builder.claim(CODE_HASH, authorizationCodeHash);
        }

        return tokenManager.rsaSign(builder.build());
    }

    private String getIssuer(final Tenant tenant) {
        final String issuer = tenant.getIssuer();
        if ( ! tenant.isSubtenant() ) {
            return issuer;
        }
        return issuer.replaceFirst("~[^/]+", "");
    }

    public JWTClaimsSet getIdTokenClaims(final Tenant tenant, final User user, final Scope... scopes) {
        Preconditions.checkNotNull(user);
        return getIdTokenBuilder(user, scopes).claim(Claim.TENANT.getName(), tenant.getSub()).build();
    }

    @Override
    public User parseSelfieToken(final String selfie) {
        final JSONObject obj = (JSONObject) tokenManager.decrypt(selfie).toJSONObject().get("user");
        obj.put(Claim.SUB.getName(), selfie);

        return convert(obj);
    }

    private JWTClaimsSet.Builder getIdTokenBuilder(final User user, final Scope... scopes) {
        final JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                .subject(user.getSub())
                .expirationTime(new Date(System.currentTimeMillis() + TokenKind.ID.getMaxAge()));

        final Map<String, Object> map = convert(user);

        for (Scope scope : scopes) {
            for (Claim claim : scope.getClaims()) {
                if (map.containsKey(claim.getName())) {
                    builder.claim(claim.getName(), map.get(claim.getName()));
                }
            }
        }
        return builder;
    }
}
