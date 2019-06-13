package com.github.phantauth.resource;

import com.github.phantauth.core.Tenant;

@FunctionalInterface
public interface Producer<T> {
    T get(Tenant tenant, Name name);
    default T get(Tenant tenant, String name) {
        return get(tenant, Name.parse(name));
    }
}
