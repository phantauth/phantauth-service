package com.github.phantauth.resource;

import com.github.phantauth.core.Tenant;
import com.github.phantauth.exception.InvalidParameterException;

public class Repository<T> implements Producer<T> {
    private final Producer<T>[] producers;

    @SafeVarargs
    public Repository(final Producer<T>... producers) {
        this.producers = producers;
    }

    @Override
    public T get(final Tenant tenant, final Name name) {
        T entity;
        for (Producer<T> producer : producers) {
            entity = producer.get(tenant, name);
            if (entity != null) {
                return entity;
            }
        }
        throw new InvalidParameterException(name.getSubject());
    }
}
