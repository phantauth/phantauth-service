package com.github.phantauth.token;

import com.github.phantauth.core.*;
import com.github.phantauth.resource.Name;
import com.google.common.base.Preconditions;
import com.nimbusds.jwt.JWT;
import net.minidev.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class ClientTokenFactory extends AbstractTokenFactory<Client> {

    @Inject
    public ClientTokenFactory(final TokenManager tokenManager) {
        super(Client.class, tokenManager);
    }

    @Override
    public String newSelfieToken(final Client client) {
        Preconditions.checkNotNull(client);
        final Client noid = new Client.Builder().from(client).setClientId(null).build();
        return newSelfieToken("client", noid);
    }

    @Override
    public JWT newIdToken(final Tenant tenant, final Client from, final String audience, final String nonce, final String accessTokenHash, final String authorizationCodeHash, final int maxAge, final Scope... scopes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String newStorageToken(final StorageToken storage) {
        return newStorageToken(storage, TokenKind.REGISTRATION);
    }

    @Override
    public Client parseSelfieToken(final String selfie) {
        final JSONObject obj = (JSONObject) tokenManager.decrypt(selfie).toJSONObject().get("client");
        addSubject(obj, selfie);

        return convert(obj);
    }

    void removeSubject(final Map<String,Object> claims) {
        claims.remove("client_id");
    }

    void addSubject(final JSONObject json, final String value) {
        json.put("client_id", value);
    }

}
