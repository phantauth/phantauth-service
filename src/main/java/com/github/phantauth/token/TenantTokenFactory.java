package com.github.phantauth.token;

import com.github.phantauth.core.Scope;
import com.github.phantauth.core.TokenKind;
import com.github.phantauth.core.Tenant;
import com.google.common.base.Preconditions;
import com.nimbusds.jwt.JWT;
import net.minidev.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TenantTokenFactory extends AbstractTokenFactory<Tenant> {

    @Inject
    public TenantTokenFactory(final TokenManager tokenManager) {
        super(Tenant.class, tokenManager);
    }

    @Override
    public String newSelfieToken(final Tenant tenant) {
        Preconditions.checkNotNull(tenant);
        final Tenant noid = new Tenant.Builder().from(tenant).setSub(null).build();
        return newSelfieToken("tenant", noid);
    }

    @Override
    public JWT newIdToken(final Tenant tenant, final Tenant from, final String audience, final String nonce, final String accessTokenHash, final String authorizationCodeHash, final int maxAge, final Scope... scopes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String newStorageToken(final StorageToken storage) {
        return newStorageToken(storage, TokenKind.API);
    }

    @Override
    public Tenant parseSelfieToken(final String selfie) {
        final JSONObject obj = (JSONObject) tokenManager.decrypt(selfie).toJSONObject().get("tenant");
        obj.put("sub", selfie);

        return convert(obj);
    }
}
