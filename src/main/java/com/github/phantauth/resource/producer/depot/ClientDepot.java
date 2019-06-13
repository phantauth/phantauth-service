package com.github.phantauth.resource.producer.depot;

import com.github.phantauth.core.*;
import com.github.phantauth.resource.Name;
import com.github.phantauth.resource.Producer;
import com.github.phantauth.resource.Repository;
import com.github.phantauth.resource.Endpoint;
import com.google.common.base.Strings;

import javax.inject.Inject;
import javax.inject.Named;

public class ClientDepot extends AbstractDepot<Client> {

    @Inject
    public ClientDepot(@Named("ttl") final long cacheTTL) {
        super(Client.class, Client::getClientId, cacheTTL, ClientBean.class);
    }

    @Override
    protected Client defaults(final Tenant tenant, final Client value) {
        final ClientBean client = new ClientBean().from(value);

        if (Strings.isNullOrEmpty(client.getClientUri())) {
            client.setClientUri(Endpoint.CLIENT.toProfile(tenant.getIssuer(), client.getClientId()));
        }

        if (Strings.isNullOrEmpty(client.getId())) {
            client.setId(Endpoint.CLIENT.toResource(tenant.getIssuer(), client.getClientId()));
        }

        return client.toImmutable();
    }

    public Producer<Fleet> getFleetDepot() {
        return new Repository<Fleet>() {
            @Override
            public Fleet get(Tenant tenant, Name name) {
                return hasTemplate(tenant.getDepot(), tenant.getDepots()) ? getFleet(tenant, name) : null;
            }
        };
    }

    Fleet getFleet(final Tenant tenant, final Name input) {
        final Name name = input.ensureAuthority(tenant::getSub);

        return new Fleet.Builder()
                .setSub(name.getSubject())
                .setProfile(Endpoint.TEAM.toProfile(tenant.getIssuer(), name.getSubject()))
                .setMembers(list(tenant, name.getFlags().getSize()))
                .setLogo(tenant.getLogo())
                .setName(tenant.getName())
                .setId(Endpoint.TEAM.toResource(tenant.getIssuer(), name.getSubject()))
                .build();
    }

}
