package com.github.phantauth.resource.producer;

import com.damnhandy.uri.template.UriTemplate;
import com.github.phantauth.exception.InvalidParameterException;
import com.github.phantauth.resource.Name;
import lombok.extern.flogger.Flogger;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Flogger
public class ExternalTenantProducer extends AbstractTenantProducer {

    private static final String WELL_KNOWN_PATTERN = "https://{host}/.well-known/phantauth-tenant";
    private static final String BIT_LY_PREFIX = "https://bit.ly/phatnauth_";
    private static final int CACHE_MAX_TENANTS = 100;

    private final ExternalCache<Map<String,Object>> cache;

    @Inject
    public ExternalTenantProducer(@Named("serviceURI") final URI serviceURI, @Named("defaultTenantURI") final URI defaultTenantURI, @Named("ttl") final long cacheTTL) {
        super(serviceURI, defaultTenantURI);
        cache = new ExternalCache<>(CACHE_MAX_TENANTS, cacheTTL, this::read);
    }

    private Map<String, Object> read(final InputStream stream) {
        try {
            return getMapper().readerFor(Map.class).readValue(stream);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected Map<String, Object> read(final Name name) {

        final String host = name.getHost();
        final String uri;

        if ( host.indexOf('.') >= 0 ) {
            uri = UriTemplate.fromTemplate(WELL_KNOWN_PATTERN).set("host", host).expand();
        } else {
            uri = BIT_LY_PREFIX + host;
        }

        try {
            return cache.get(uri);
        } catch (ExecutionException e) {
            log.atWarning().withCause(e).log();
            throw new InvalidParameterException(uri);
        }
    }
}
