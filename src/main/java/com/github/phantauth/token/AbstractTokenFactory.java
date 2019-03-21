package com.github.phantauth.token;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.phantauth.core.Claim;
import com.github.phantauth.core.Scope;
import com.github.phantauth.core.Tenant;
import com.github.phantauth.core.TokenKind;
import com.github.phantauth.exception.InvalidParameterException;
import com.github.phantauth.resource.Name;
import com.google.common.base.Preconditions;
import com.nimbusds.jose.Payload;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.SignedJWT;
import net.minidev.json.JSONObject;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

abstract class AbstractTokenFactory<T> implements TokenFactory<T> {
    static final String TENANT = "tenant";
    static final String SCOPE = "scope";
    static final String TOKEN_KIND = "token_kind";
    private static final ObjectMapper MAPPER = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
    static final String NONCE = "nonce";

    final TokenManager tokenManager;
    final Class<T> resourceClass;

    AbstractTokenFactory(final Class<T> resourceClass, final TokenManager tokenManager) {
        this.resourceClass = resourceClass;
        this.tokenManager = tokenManager;
    }

    String newSelfieToken(final String name, final Object claim) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(claim);
        final JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                .claim(name, convert(claim))
                .claim(TOKEN_KIND, TokenKind.SELFIE.getName())
                .expirationTime(new Date(System.currentTimeMillis() + TokenKind.SELFIE.getMaxAge()));
        final Payload payload = new Payload(builder.build().toJSONObject());

        return tokenManager.octEncrypt(payload).serialize();
    }

    String newStorageToken(final StorageToken storage, final TokenKind... allowedTokenKinds) {
        Preconditions.checkNotNull(storage);
        Preconditions.checkNotNull(storage.getSubject());

        boolean found = false;
        for(TokenKind kind : allowedTokenKinds) {
            if ( storage.getTokenKind() == kind ) {
                found = true;
                break;
            }
        }

        if ( ! found ) {
            throw new IllegalArgumentException("kind");
        }

        final JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                .subject(storage.getSubject())
                .claim(TOKEN_KIND, storage.getTokenKind())
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + storage.getTokenKind().getMaxAge()));

        if ( storage.getScopes() != null ) {
            builder.claim(SCOPE, Scope.format(storage.getScopes()));
        }

        if ( storage.getNonce() != null ) {
            builder.claim(NONCE,  storage.getNonce());
        }

        if ( storage.getTenant() != null ) {
            builder.claim(TENANT,  storage.getTenant());
        }

        return tokenManager.hmacSign(builder.build()).serialize();
    }

    public boolean verifyIDToken(final JWT token) {
        Preconditions.checkNotNull(token);
        return tokenManager.rsaVerify((SignedJWT)token);
    }

    @Override
    public StorageToken parseStorageToken(final String token, final TokenKind kind) {
        Preconditions.checkNotNull(token);
        Preconditions.checkNotNull(kind);

        final JWTClaimsSet claims;

        try {
            final SignedJWT jwt = SignedJWT.parse(token);

            if ( ! tokenManager.hmacVerify(jwt) ) {
                throw new InvalidParameterException(kind.getName());
            }

            claims = jwt.getJWTClaimsSet();
            validate(claims, kind);

            final StorageToken.Builder builder = new StorageToken.Builder()
                    .setSubject(claims.getSubject())
                    .setTokenKind(TokenKind.valueOf(claims.getStringClaim(TOKEN_KIND)));

            if ( claims.getStringClaim(SCOPE) != null ) {
                    builder.setScopes(Scope.split(claims.getStringClaim(SCOPE)));
            }

            if ( claims.getStringClaim(NONCE) != null ) {
                builder.setNonce(claims.getStringClaim(NONCE));
            }

            if ( claims.getStringClaim(TENANT) != null ) {
                builder.setTenant(claims.getStringClaim(TENANT));
            }

            return builder.build();
        } catch (ParseException e) {
            throw new InvalidParameterException(kind.getName());
        }
    }

    void validate(final JWTClaimsSet claims, final TokenKind kind) {

        try {
            if ( ! kind.toString().equalsIgnoreCase(claims.getStringClaim(TOKEN_KIND)) ) {
                throw new InvalidParameterException(kind.getName());
            }
        } catch (ParseException e) {
            throw new InvalidParameterException(kind.getName());
        }

        final Date exp = claims.getExpirationTime();

        if ( exp == null ) {
            throw new InvalidParameterException(kind.getName());
        }

        if ( exp.getTime() < System.currentTimeMillis() ) {
            throw new InvalidParameterException(kind.getName());
        }
    }

    Map<String, Object> convert(final Object value) {
        return MAPPER.convertValue(value, MAPPER.getTypeFactory().constructMapType(Map.class, String.class, Object.class));
    }

    T convert(final JSONObject obj) {
        return MAPPER.convertValue(obj, resourceClass);
    }

    void removeSubject(final Map<String,Object> claims) {
        claims.remove(Claim.SUB.getName());
    }

    void addSubject(final JSONObject json, final String value) {
        json.put(Claim.SUB.getName(), value);
    }

    @Override
    public String newPlainToken(final T from) {
        final Map<String,Object> claims = MAPPER.convertValue(from, Map.class);
        removeSubject(claims);
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
        for(String key: claims.keySet()) {
            builder.claim(key, claims.get(key));
        }

        return new PlainJWT(builder.build()).serialize();
    }

    @Override
    public T parsePlainToken(final String token) {
        try {
            final JSONObject json = PlainJWT.parse(token).getJWTClaimsSet().toJSONObject();
            addSubject(json, token);
            return convert(json);
        } catch (ParseException e) {
            throw new InvalidParameterException(TokenKind.PLAIN.getName());
        }
    }

    protected boolean isPlain(final String value) {
        return TokenKind.PLAIN.match(value);
    }

    protected boolean isSelfie(final String value) {
        return TokenKind.SELFIE.match(value);
    }

    @Override
    public T get(final Tenant tenant, final Name name) {
        final String raw = name.getRaw();

        if ( isPlain(raw) ) {
            return parsePlainToken(raw);
        } else if ( isSelfie(raw) ) {
            return parseSelfieToken(raw);
        } else {
            return null;
        }
    }
}
