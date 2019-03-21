package com.github.phantauth.core;

import lombok.Getter;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public enum TokenKind {

    ACCESS("access_token", TimeUnit.MINUTES.toMillis(10), Patterns.JWT),
    ID("id_token", TimeUnit.DAYS.toMillis(10), Patterns.JWT),
    AUTHORIZATION("authorization_code", TimeUnit.MINUTES.toMillis(10), Patterns.JWT),
    REFRESH("refresh_token", TimeUnit.DAYS.toMillis(365), Patterns.JWT),
    REGISTRATION("registration_access_token", TimeUnit.DAYS.toMillis(365), Patterns.JWT),
    LOGIN("login_token", TimeUnit.DAYS.toMillis(30), Patterns.JWT),
    SELFIE("selfie_token", TimeUnit.DAYS.toMillis(5 * 365), Patterns.JWE),
    PLAIN("plain_token", Long.MAX_VALUE, Patterns.PLAIN),
    API("apikey", TimeUnit.DAYS.toMillis(365), Patterns.JWT);

    @Getter
    private final String name;

    @Getter
    private final long maxAge;

    private final Pattern pattern;

    TokenKind(final String name, final long maxAge, final Pattern pattern) {
        this.name = name;
        this.pattern = pattern;
        this.maxAge = maxAge;
    }

    public boolean match(final String value) {
        return pattern.matcher(value).matches();
    }

    private static class Patterns {
        private static final Pattern JWE = Pattern.compile("^[\\p{Alnum}_-]+\\.[\\p{Alnum}_-]*\\.[\\p{Alnum}_-]+\\.[\\p{Alnum}_-]+\\.[\\p{Alnum}_-]+$");
        private static final Pattern JWT = Pattern.compile("^[\\p{Alnum}_-]+\\.[\\p{Alnum}_-]+\\.[\\p{Alnum}_-]+$");
        private static final Pattern PLAIN = Pattern.compile("^[\\p{Alnum}_-]+\\.[\\p{Alnum}_-]+\\.$");
    }
}
