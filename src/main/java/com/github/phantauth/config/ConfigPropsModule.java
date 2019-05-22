package com.github.phantauth.config;

import com.github.phantauth.exception.ConfigurationException;
import com.nimbusds.jose.PlainObject;
import com.nimbusds.jose.jwk.JWKSet;
import dagger.Module;
import dagger.Provides;

import javax.inject.Named;
import javax.inject.Singleton;
import java.net.URI;
import java.text.ParseException;

@Module
public class ConfigPropsModule {

    private ConfigPropsModule() {
        // no instances
    }

    @Provides @Named("serviceURI") @Singleton
    static URI provideServiceURI(final Config config) {
        return URI.create(config.getServiceURI());
    }

    @Provides @Named("defaultTenantURI") @Singleton
    static URI provideDefaultTenantURI(final Config config) {
        return URI.create(config.getDefaultTenantURI());
    }

    @Provides @Named("developerPortalURI") @Singleton
    static URI provideDeveloperPortalURI(final Config config) {
        return URI.create(config.getDeveloperPortalURI());
    }

    @Provides @Named("tenantDomain") @Singleton
    static String provideTenantDomain(final Config config) {
        return config.getTenantDomain();
    }

    @Provides @Named("port")
    static int providePort(final Config config) {
        return config.getPort();
    }

    @Provides @Named("ttl")
    static long provideTTL(final Config config) {
        return config.getTTL();
    }

    @Provides @Named("standalone")
    static boolean provideStandalone(final Config config) {
        return config.isStandalone();
    }

    @Provides
    static JWKSet provideKeySet(final Config config)  {
        try {
            return JWKSet.parse(PlainObject.parse(config.getServiceKeys()).getPayload().toJSONObject());
        } catch (ParseException e) {
            throw new ConfigurationException("unable to parse server keyset");
        }
    }
}
