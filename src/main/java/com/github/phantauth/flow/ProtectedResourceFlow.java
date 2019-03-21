package com.github.phantauth.flow;

import com.github.phantauth.core.Client;
import com.github.phantauth.core.TokenKind;
;
import com.github.phantauth.core.Tenant;
import com.github.phantauth.core.User;
import com.github.phantauth.resource.Repository;
import com.github.phantauth.resource.TenantRepository;
;
import com.github.phantauth.token.ClientTokenFactory;
import com.github.phantauth.token.StorageToken;
import com.github.phantauth.token.UserTokenFactory;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.SoftwareID;
import com.nimbusds.oauth2.sdk.id.SoftwareVersion;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientMetadata;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class ProtectedResourceFlow extends AbstractFlow {

    @Inject
    public ProtectedResourceFlow(final TenantRepository tenantRepository, final Repository<User> userRepository, final UserTokenFactory userTokenFactory, final Repository<Client> clientRepository, final ClientTokenFactory clientTokenFactory) {
        super(tenantRepository, userRepository, userTokenFactory, clientRepository, clientTokenFactory);
    }

    public UserInfo getUserInfo(final Tenant tenant, final AccessToken accessToken) {
        final StorageToken storageToken = userTokenFactory.parseStorageToken(accessToken.toString(), TokenKind.ACCESS);
        final Tenant tokenTenant = storageToken.getTenant() == null ? tenant : tenantRepository.get(storageToken.getTenant());
        return new UserInfo(userTokenFactory.getIdTokenClaims(tokenTenant, userRepository.get(tokenTenant, storageToken.getSubject()), storageToken.getScopes()));
    }

    public OIDCClientInformation registerClient(final Tenant tenant, final OIDCClientMetadata input, final URL endpoint) {
        final Client client = newClient(input);
        final ClientID clientId = new ClientID(clientTokenFactory.newSelfieToken(client));

        final OIDCClientMetadata meta = newClientMetadata(client);

        final OIDCClientInformation info = new OIDCClientInformation(
                clientId,
                new Date(),
                meta,
                new Secret(client.getClientSecret()),
                URI.create(endpoint.toExternalForm()),
                new BearerAccessToken(clientTokenFactory.newStorageToken(StorageToken.Builder.of(tenant, TokenKind.REGISTRATION, client.getClientId())))
        );

        return info;
    }

    public static Client newClient(final OIDCClientMetadata meta) {
        final String secret = new PasswordGenerator().generatePassword(8,
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1));

        final Client.Builder builder = new Client.Builder()
                .setClientId(UUID.randomUUID().toString()) // dummy, will be removed in selfie token
                .setClientSecret(secret)
                .setSoftwareId(Objects.toString(meta.getSoftwareID(), null))
                .setSoftwareVersion(Objects.toString(meta.getSoftwareVersion(), null))
                .setClientName(meta.getName())
                .setClientUri(Objects.toString(meta.getURI(), null))
                .setLogoUri(Objects.toString(meta.getLogoURI(), null))
                .setPolicyUri(Objects.toString(meta.getPolicyURI(), null))
                .setTosUri(Objects.toString(meta.getTermsOfServiceURI(), null))
                .setScope(Objects.toString(meta.getScope(), null))
                .setContacts(toStringList(meta.getEmailContacts()))
                .setGrantTypes(toStringList(meta.getGrantTypes()))
                .setRedirectUris(toStringList(meta.getRedirectionURIs()))
                .setResponseTypes(toStringList(meta.getResponseTypes()))
                .setJwks(Objects.toString(meta.getJWKSet()))
                .setJwksUri(Objects.toString(meta.getJWKSetURI(), null))
                .setTokenEndpointAuthMethod(Objects.toString(meta.getTokenEndpointAuthMethod(), null));
        return builder.build();
    }

    public static OIDCClientMetadata newClientMetadata(final Client client) {
        final OIDCClientMetadata meta = new OIDCClientMetadata();

        meta.setSoftwareID(new SoftwareID(client.getSoftwareId()));
        meta.setSoftwareVersion(new SoftwareVersion(client.getSoftwareVersion()));
        meta.setName(client.getClientName());
        meta.setURI(URI.create(client.getClientUri()));
        meta.setLogoURI(URI.create(client.getLogoUri()));
        meta.setPolicyURI(URI.create(client.getPolicyUri()));
        meta.setTermsOfServiceURI(URI.create(client.getTosUri()));
        meta.setScope(client.getScope() == null ? null : new Scope(client.getScope()));
        meta.setEmailContacts(client.getContacts());

        if ( client.getGrantTypes() != null  ) {
            meta.setGrantTypes(client.getGrantTypes().stream().map(str -> new GrantType(str)).collect(Collectors.toSet()));
        }

        if ( client.getRedirectUris() != null ) {
            meta.setRedirectionURIs(client.getRedirectUris().stream().map(str -> URI.create(str)).collect(Collectors.toSet()));
        }

        if ( client.getResponseTypes() != null ) {
            meta.setResponseTypes(client.getResponseTypes().stream().map(str -> new ResponseType(str)).collect(Collectors.toSet()));
        }

        if ( client.getJwks() != null ) {
            try {
                meta.setJWKSet(JWKSet.parse(client.getJwks()));
            } catch (ParseException e) {
                // nothing to do
            }
        }

        if ( client.getJwksUri() != null ) {
            meta.setJWKSetURI(URI.create(client.getJwksUri()));
        }

        if ( client.getTokenEndpointAuthMethod() != null ) {
            meta.setTokenEndpointAuthMethod(new ClientAuthenticationMethod(client.getTokenEndpointAuthMethod()));
        }

        return meta;
    }

    static List<String> toStringList(final Collection<?> collection) {
        if ( collection == null || collection.isEmpty() ) {
            return Collections.EMPTY_LIST;
        }
        return collection.stream().map(object -> Objects.toString(object, null)).collect(Collectors.toList());
    }

}
