package com.github.phantauth.core;

import java.util.StringTokenizer;

public enum Scope {

    /**
     * OpenID Connect requests MUST contain the openid scope value.
     * If the openid scope value is not present, the behavior is entirely unspecified.
     **/
    OPENID("openid", Claim.SUB),

    /**
     * This scope value requests access to the End-User's default profile Claims.
     **/
    PROFILE("profile", Claim.NAME, Claim.FAMILY_NAME, Claim.GIVEN_NAME, Claim.MIDDLE_NAME, Claim.NICKNAME, Claim.PREFERRED_USERNAME, Claim.PROFILE, Claim.PICTURE, Claim.WEBSITE, Claim.GENDER, Claim.BIRTHDATE, Claim.ZONEINFO, Claim.LOCALE, Claim.UPDATED_AT),

    /**
     * This scope value requests access to the email and email_verified Claims.
     **/
    EMAIL("email", Claim.EMAIL, Claim.EMAIL_VERIFIED),

    /**
     * This scope value requests access to the address Claim.
     **/
    ADDRESS("address", Claim.ADDRESS),

    /**
     * This scope value requests access to the phone_number and phone_number_verified Claims.
     **/
    PHONE("phone", Claim.PHONE_NUMBER, Claim.PHONE_NUMBER_VERIFIED),

    /**
     * Non official (PhantAuth only) scope for IndieAuth Claims
     */
    INDIEAUTH("indieauth", Claim.ME),

    /**
     * Non official (PhantAuth only) scope for uid claim.
     */
    UID("uid", Claim.UID);

    private final String name;
    private final Claim[] claims;

    Scope(final String name, final Claim... claims) {
        this.name = name;
        this.claims = claims;
    }

    @SuppressWarnings("WeakerAccess")
    public String getName() {
        return name;
    }

    public Claim[] getClaims() {
        return claims;
    }

    public static Scope[] getDefaultScopes() {
        return Scope.values();
    }

    @SuppressWarnings("WeakerAccess")
    public static Scope parse(final String name) {
        for (Scope scope : values()) {
            if (scope.getName().equalsIgnoreCase(name)) {
                return scope;
            }
        }
        return null;
    }

    public static Scope[] split(final String names) {
        final StringTokenizer tokenizer = new StringTokenizer(names);
        final Scope[] scopes = new Scope[tokenizer.countTokens()];
        for (int i = 0; i < scopes.length; i++) {
            scopes[i] = parse(tokenizer.nextToken());
        }
        return scopes;
    }

    public static String format(final Scope... scopes) {
        final StringBuilder buff = new StringBuilder();
        for (int i = 0; i < scopes.length; i++) {
            buff.append(scopes[i].getName());
            if (i < scopes.length - 1) {
                buff.append(' ');
            }
        }
        return buff.toString();
    }
}
