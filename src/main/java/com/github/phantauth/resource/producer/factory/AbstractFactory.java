package com.github.phantauth.resource.producer.factory;

import com.damnhandy.uri.template.UriTemplate;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.phantauth.core.Tenant;
import com.github.phantauth.exception.InvalidParameterException;
import com.github.phantauth.resource.Name;
import com.github.phantauth.resource.Producer;
import com.github.phantauth.resource.producer.AbstractExternalProducer;
import com.github.phantauth.resource.producer.ExternalCache;
import com.google.common.base.Strings;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public class AbstractFactory<T> extends AbstractExternalProducer<T> implements Producer<T> {
    private static final int CACHE_MAX_ENTRIES = 512;

    private final ObjectMapper mapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private final ExternalCache<T> cache;

    public AbstractFactory(final Class<T> type, final Class<? extends T> mixin, final long cacheTTL) {
        super(type);
        this.cache = new ExternalCache<>(CACHE_MAX_ENTRIES, cacheTTL, this::read);
        if ( mixin != null ) {
            mapper.addMixIn(type, mixin);
        }
    }

    private T read(final InputStream inputStream) {
        try {
            return mapper.readValue(inputStream, type);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public T get(final Tenant tenant, final Name name) {

        final UriTemplate template = templateFrom(tenant.getFactory(), tenant.getFactories());

        if ( template == null ) {
            return null;
        }

        final String uri = template
                .set(PARAM_KIND, typeName)
                .set(PARAM_NAME, name.getSubject()).expand();

        try {
            return cache.get(uri);
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new InvalidParameterException(uri);
        }
    }
}
