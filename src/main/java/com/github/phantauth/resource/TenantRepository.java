package com.github.phantauth.resource;

import com.github.phantauth.core.Tenant;
import com.github.phantauth.resource.producer.TenantDomainTenantProducer;
import com.github.phantauth.resource.producer.DNSTenantProducer;
import com.github.phantauth.resource.producer.ExternalTenantProducer;

import javax.inject.Inject;

public class TenantRepository extends Repository<Tenant> {

    @Inject
    public TenantRepository(final DNSTenantProducer dnsTenantProducer, final TenantDomainTenantProducer configTenantProducer, final ExternalTenantProducer externalProducer) {
        super(dnsTenantProducer, configTenantProducer, externalProducer);
    }

    public Tenant get(final Name name) {
        return get(null, name);
    }

    public Tenant get(final String name) {
        return get(null, Name.parse(name));
    }

    public Tenant get(final String name, final String server) {
        return get(null, Name.parse(name));
    }

    @Override
    public Tenant get(final Tenant tenant, final Name name) {

        final Tenant normal = super.get(tenant, name);

        if ( ! normal.isDomain() || name.isInstanceEmpty() ) {
            return normal;
        }

        final Tenant subtenant = super.get(tenant, Name.parse(name.getInstance()));

        return new Tenant.Builder()
                .from(subtenant)
                .setSubtenant(true)
                .setSub(normal.getSub())
                .setIssuer(normal.getIssuer())
                .setDomain(false)
                .setId(normal.getId())
                .build();
    }

    public Tenant getDefaultTenant() {
        return get(null, Name.EMPTY);
    }
}
