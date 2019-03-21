package com.github.phantauth.token;

import com.github.phantauth.exception.ConfigurationException;
import com.github.phantauth.exception.InvalidParameterException;
import com.google.common.base.Preconditions;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.token.AccessToken;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.text.ParseException;
import java.util.List;

@Singleton
public class TokenManager {
    private static final JWKMatcher MATCHER_RSA = new JWKMatcher.Builder()
            .algorithm(JWSAlgorithm.RS256)
            .hasKeyUse(false)
            .keyType(KeyType.RSA)
            .build();
    private static final JWKMatcher MATCHER_RSA_SIG = new JWKMatcher.Builder()
            .algorithm(JWSAlgorithm.RS256)
            .keyUse(KeyUse.SIGNATURE)
            .keyType(KeyType.RSA)
            .build();
    private static final JWKMatcher MATCHER_RSA_ENC = new JWKMatcher.Builder()
            .algorithm(JWSAlgorithm.RS256)
            .keyUse(KeyUse.ENCRYPTION)
            .keyType(KeyType.RSA)
            .build();
    private static final JWKMatcher MATCHER_OCT = new JWKMatcher.Builder()
            .algorithm(JWSAlgorithm.HS256)
            .hasKeyUse(false)
            .keyType(KeyType.OCT)
            .build();
    private static final JWKMatcher MATCHER_OCT_SIG = new JWKMatcher.Builder()
            .algorithm(JWSAlgorithm.HS256)
            .keyUse(KeyUse.SIGNATURE)
            .keyType(KeyType.OCT)
            .build();
    private static final JWKMatcher MATCHER_OCT_ENC = new JWKMatcher.Builder()
            .algorithm(JWSAlgorithm.HS256)
            .keyUse(KeyUse.ENCRYPTION)
            .keyType(KeyType.OCT)
            .build();


    @Inject
    public TokenManager(final JWKSet keySet) {
        try {
            OctetSequenceKey octKey;
            octKey =  getKey(keySet, OctetSequenceKey.class, MATCHER_OCT_SIG, MATCHER_OCT);
            hmacSigner = new MACSigner(octKey);
            hmacVerifier = new MACVerifier(octKey);
            octKey = getKey(keySet, OctetSequenceKey.class, MATCHER_OCT_ENC, MATCHER_OCT);
            octEncrypter = new DirectEncrypter(octKey);
            octDecrypter = new DirectDecrypter(octKey);
            sigRSAKey = getKey(keySet, RSAKey.class, MATCHER_RSA_SIG, MATCHER_RSA);
            rsaSigner = new RSASSASigner(sigRSAKey);
            rsaVerifier = new RSASSAVerifier(sigRSAKey);
            RSAKey encRSAKey = getKey(keySet, RSAKey.class, MATCHER_RSA_ENC, MATCHER_RSA);
            rsaEncrypter = new RSAEncrypter((encRSAKey.toRSAPublicKey()));
            rsaDecrypter = new RSADecrypter((encRSAKey));
            publicKeySet = keySet.toPublicJWKSet();
        } catch (JOSEException e) {
            throw new ConfigurationException("server keyset problem");
        }
    }

    private final MACSigner hmacSigner;
    private final MACVerifier hmacVerifier;
    private final DirectEncrypter octEncrypter;
    private final DirectDecrypter octDecrypter;
    private final RSAKey sigRSAKey;
    private final RSASSASigner rsaSigner;
    private final RSASSAVerifier rsaVerifier;
    private final JWKSet publicKeySet;
    private final RSAEncrypter rsaEncrypter;
    private final RSADecrypter rsaDecrypter;

    public JWKSet getPublicKeySet() {
        return publicKeySet;
    }

    public JWTClaimsSet getClaimSet(final AccessToken accessToken) {
        try {
            SignedJWT jwt = SignedJWT.parse(accessToken.toString());
            if ( ! jwt.verify(hmacVerifier) ) {
                throw new InvalidParameterException("access_token");
            }
            return jwt.getJWTClaimsSet();
        } catch (ParseException | JOSEException e) {
            throw new InvalidParameterException("access_token");
        }
    }

    public SignedJWT hmacSign(final JWTClaimsSet claims) {
        final SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        try {
            jwt.sign(hmacSigner);
        } catch (JOSEException e) {
            throw new ConfigurationException("encryption problem");
        }
        return jwt;
    }

    public boolean hmacVerify(final SignedJWT jwt) {
        try {
            return jwt.verify(hmacVerifier);
        } catch (JOSEException e) {
            throw new InvalidParameterException("token");
        }
    }

    public SignedJWT rsaSign(final JWTClaimsSet claims) {
        final SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(sigRSAKey.getKeyID()).build(), claims);
        try {
            jwt.sign(rsaSigner);
        } catch (JOSEException e) {
            throw new ConfigurationException("encryption problem");
        }
        return jwt;
    }

    public boolean rsaVerify(final SignedJWT jwt) {
        try {
            return jwt.verify(rsaVerifier);
        } catch (JOSEException e) {
            throw new InvalidParameterException("token");
        }
    }

    public JWEObject rsaEncrypt(final Payload payload) {
        final JWEHeader header = new JWEHeader(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A256GCM);
        final JWEObject jwe = new JWEObject(header, payload);
        try {
            jwe.encrypt(rsaEncrypter);
        } catch (JOSEException e) {
            throw new ConfigurationException("encryption problem");
        }

        return jwe;
    }

    public Payload decrypt(final String compact) {
        final JWEObject jwe;
        try {
            jwe = JWEObject.parse(compact);
        } catch (ParseException e) {
            throw new InvalidParameterException("token");
        }
        return decrypt(jwe);
    }

    @SuppressWarnings("WeakerAccess")
    public Payload decrypt(final JWEObject jwe) {
        final JWEAlgorithm alg = jwe.getHeader().getAlgorithm();

        if ( JWEAlgorithm.RSA_OAEP_256.equals(alg) ) {
            return rsaDecrypt(jwe);
        } else if ( JWEAlgorithm.DIR.equals(alg) ) {
            return octDecrypt(jwe);
        } else {
            throw new InvalidParameterException("token");
        }
    }

    @SuppressWarnings("WeakerAccess")
    public Payload rsaDecrypt(final JWEObject jwe) {
        try {
            jwe.decrypt(rsaDecrypter);
            return jwe.getPayload();
        } catch (JOSEException e) {
            throw new InvalidParameterException("token");
        }
    }

    public JWEObject octEncrypt(final Payload payload) {
        final JWEHeader header = new JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A256GCM);
        final JWEObject jwe = new JWEObject(header, payload);
        try {
            jwe.encrypt(octEncrypter);
        } catch (JOSEException e) {
            throw new ConfigurationException("encryption problem");
        }

        return jwe;
    }

    @SuppressWarnings("WeakerAccess")
    public Payload octDecrypt(final JWEObject jwe) {
        try {
            jwe.decrypt(octDecrypter);
            return jwe.getPayload();
        } catch (JOSEException e) {
            throw new InvalidParameterException("token");
        }
    }

    private static <T> T getKey(final JWKSet keySet, final Class<T> type, final JWKMatcher matcher, final JWKMatcher fallback) {
        List<JWK> keys = new JWKSelector(matcher).select(keySet);
        if ( keys.size() == 0 ) {
            keys = new JWKSelector(fallback).select(keySet);
        }
        Preconditions.checkState(keys.size() == 1);
        final JWK key = keys.get(0);
        Preconditions.checkState( type.isAssignableFrom(key.getClass()) );
        return type.cast(key);
    }

}
