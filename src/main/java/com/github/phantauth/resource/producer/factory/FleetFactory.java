package com.github.phantauth.resource.producer.factory;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.phantauth.core.*;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.resource.Name;
import com.google.common.base.Strings;

import javax.inject.Inject;
import javax.inject.Named;

public class FleetFactory extends AbstractFactory<Fleet> {

    @JsonDeserialize(as= FleetBean.class)
    interface FleetMixin extends Fleet {
    }

    @Inject
    public FleetFactory(@Named("ttl") final long cacheTTL) {
        super(Fleet.class, FleetMixin.class, cacheTTL);
    }

    @Override
    public Fleet get(final Tenant tenant, final Name name) {
        final FleetBean fleet = defaults(tenant, (FleetBean)super.get(tenant, name));

        return fleet == null ? null : fleet.toImmutable();
    }

    static FleetBean defaults(final Tenant tenant, final FleetBean fleet) {
        if ( fleet == null ) {
            return null;
        }

        if (Strings.isNullOrEmpty(fleet.getProfile())) {
            fleet.setProfile(Endpoint.FLEET.toProfile(tenant.getIssuer(), fleet.getSub()));
        }

        if (Strings.isNullOrEmpty(fleet.getId())) {
            fleet.setId(Endpoint.FLEET.toResource(tenant.getIssuer(), fleet.getSub()));
        }

        for(Client client: fleet.getMembers()) {
            ClientFactory.defaults(tenant, (ClientBean) client);
        }

        return fleet;
    }
}
