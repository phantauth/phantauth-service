package com.github.phantauth.resource;

import com.github.phantauth.core.Tenant;
import com.github.phantauth.exception.InvalidParameterException;

import java.util.Collections;
import java.util.Set;

public class Repository<T> implements Producer<T> {
    private final Producer<T>[] producers;

    public Repository(final Producer<T>... producers) {
        this.producers = producers;
    }

    @Override
    public T get(final Tenant tenant, final Name name) {
        T entity;
        for(int i = 0; i < producers.length; i++) {
            entity = producers[i].get(tenant, name);
            if ( entity != null ) {
                return entity;
            }
        }
        throw new InvalidParameterException(name.getSubject());
    }
}
