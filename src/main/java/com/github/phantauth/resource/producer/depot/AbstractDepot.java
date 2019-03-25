package com.github.phantauth.resource.producer.depot;

import com.damnhandy.uri.template.UriTemplate;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.github.phantauth.core.*;
import com.github.phantauth.exception.InvalidParameterException;
import com.github.phantauth.resource.Flags;
import com.github.phantauth.resource.Name;
import com.github.phantauth.resource.Producer;
import com.github.phantauth.resource.producer.AbstractExternalProducer;
import com.github.phantauth.resource.producer.ExternalCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.extern.flogger.Flogger;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

@Flogger
public abstract class AbstractDepot<T> extends AbstractExternalProducer<T> implements Producer<T> {
    private static final int CACHE_MAX_REPOSITORIES = 32;
    private static final int MAX_REPOSITORY_LINES = 512;
    private final CsvMapper mapper = (CsvMapper)new CsvMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    private final CsvSchema schema;
    private final Function<T,String> getKey;
    private final ExternalCache<Map<String,T>> cache;

    public AbstractDepot(final Class<T> type, final Function<T,String> getKey, final long cacheTTL) {
        this(type, getKey, cacheTTL, null);
    }

    public AbstractDepot(final Class<T> type, final Function<T,String> getKey, final long cacheTTL, final Class<? extends T> mixin) {
        super(type);
        this.getKey = getKey;
        cache = new ExternalCache<>(CACHE_MAX_REPOSITORIES, cacheTTL, this::reader);

        if ( mixin != null ) {
            mapper.addMixIn(type, mixin);
        }
        schema = mapper.schema().withHeader();
    }

    private Map<String,T> reader(InputStream inputStream) {

        final ImmutableMap.Builder<String, T> builder = new ImmutableMap.Builder<>();

        try {
            final MappingIterator<T> it = mapper.readerFor(type).with(schema).readValues(inputStream);
            for (int lines = 0; it.hasNextValue() && lines < MAX_REPOSITORY_LINES; lines++) {
                T value = it.nextValue();
                builder.put(getKey.apply(value), value);
            }
        } catch (IOException x) {
            return null;
        }

        return builder.build();
    }

    T getMissing(final Map<String,T> map, final Name name) {
        final Random random = new Random(name.isAuthorityEmpty() ? System.currentTimeMillis() : name.getSubject().hashCode());
        Object[] keys = map.keySet().toArray();
        return map.get(keys[random.nextInt(keys.length)]);
    }

    protected List<T> list(final Tenant tenant, final Flags.Size size) {
        try {
            return list(tenant, expand(templateFrom(tenant.getDepot(), tenant.getDepots())), size.getLimit());
        } catch (ExecutionException e) {
            log.atWarning().withCause(e).log();
            throw new InvalidParameterException(tenant.getSub());
        }
    }

    private List<T> list(final Tenant tenant, final String uri, final int size)  throws ExecutionException {
        final Collection<T> values = cache.get(uri).values();
        final int limit = Math.min(size, values.size());
        final ImmutableList.Builder<T> builder = ImmutableList.builder();
        int i = 0;
        for(Iterator<T> it = values.iterator(); it.hasNext() && i < limit; i++) {
            builder.add(defaults(tenant, it.next()));
        }
        return builder.build();
    }

    private T get(final String uri, final Name name) throws ExecutionException {
        final Map<String, T> map = cache.get(uri);
        return name.isAuthorityEmpty() || ! map.containsKey(name.getSubject()) ? getMissing(map, name) : map.get(name.getSubject());
    }

    protected abstract T defaults(final Tenant tenant, final T value);

    @Override
    public T get(final Tenant tenant, final Name name) {

        final UriTemplate template = templateFrom(tenant.getDepot(), tenant.getDepots());

        if ( template == null ) {
            return null;
        }

        try {
            final T value = get(expand(template, name), name);
            return value == null ? null : defaults(tenant, value);
        } catch (ExecutionException e) {
            log.atWarning().withCause(e).log();
            throw new InvalidParameterException(tenant.getSub());
        }
    }
}
