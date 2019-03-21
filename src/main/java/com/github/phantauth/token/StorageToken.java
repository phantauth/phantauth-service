package com.github.phantauth.token;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.phantauth.core.Scope;
import com.github.phantauth.core.Tenant;
import com.github.phantauth.core.TokenKind;
import com.nimbusds.openid.connect.sdk.Nonce;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
@JsonSerialize(as = StorageToken.class)
@JsonDeserialize(builder = StorageToken.Builder.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public interface StorageToken {
    class Builder extends StorageTokenValue.Builder {

        public static StorageToken of(final Tenant tenant, final StorageToken from, final TokenKind kind) {
            final Builder builder = new Builder().from(from).setTokenKind(kind);
            return addTenant(builder, tenant).build();
        }

        public static StorageToken of(final Tenant tenant, final TokenKind kind, final String subject) {
            final Builder builder = new Builder().setTokenKind(kind).setSubject(subject);
            return addTenant(builder, tenant).build();
        }

        public static StorageToken of(final Tenant tenant, final TokenKind kind, final String subject, final Scope... scopes) {
            final Builder builder = new Builder().setTokenKind(kind).setSubject(subject).setScopes(scopes);
            return addTenant(builder, tenant).build();
        }

        public static StorageToken of(final Tenant tenant, final TokenKind kind, final String subject, final String nonce, final Scope... scopes) {
            final Builder builder =  new Builder().setTokenKind(kind).setSubject(subject).setNonce(nonce).setScopes(scopes);
            return addTenant(builder, tenant).build();
        }

        public static StorageToken of(final Tenant tenant, final TokenKind kind, final String subject, final com.nimbusds.oauth2.sdk.Scope scope) {
            final Builder builder = new Builder().setTokenKind(kind).setSubject(subject).setScopes(scope);
            return addTenant(builder, tenant).build();
        }

        public static StorageToken of(final Tenant tenant, final TokenKind kind, final String subject, final Nonce nonce, final int maxAge, final com.nimbusds.oauth2.sdk.Scope scope) {
            final Builder builder = new Builder().setTokenKind(kind).setSubject(subject).setNonce(nonce).setMaxAge(maxAge).setScopes(scope);
            return addTenant(builder, tenant).build();
        }

        private static Builder addTenant(final Builder builder, final Tenant tenant) {
            if ( tenant.isSubtenant() ) {
                return builder.setTenant(tenant.getSub());
            }
            return builder;
        }

        public Builder setScopes(final com.nimbusds.oauth2.sdk.Scope scope) {
            if ( scope != null ) {
                setScopes(Scope.split(scope.toString()));
            }
            return this;
        }

        public Builder setNonce(final Nonce nonce) {
            if ( nonce != null ) {
                setNonce(nonce.getValue());
            }
            return this;
        }
    }

    String getSubject();
    TokenKind getTokenKind();
    Scope[] getScopes();
    @Nullable
    String getNonce();
    @Nullable
    String getTenant();

    @Value.Default
    default int getMaxAge() {
        return Integer.MAX_VALUE;
    }

    @Value.Lazy
    @Nullable
    default com.nimbusds.oauth2.sdk.Scope getScopesAsScope() {
        return getScopes() == null ? null : new com.nimbusds.oauth2.sdk.Scope(Scope.format(getScopes()));
    }

    @Value.Lazy
    @Nullable
    default Nonce getNonceAsNonce() {
        return getNonce() == null ? null :new Nonce(getNonce());
    }
}
