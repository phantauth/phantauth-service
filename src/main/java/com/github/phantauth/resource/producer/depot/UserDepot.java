package com.github.phantauth.resource.producer.depot;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.phantauth.core.*;
import com.github.phantauth.resource.Name;
import com.github.phantauth.resource.Producer;
import com.github.phantauth.resource.Repository;
import com.github.phantauth.resource.Endpoint;
import com.google.common.base.Strings;

import javax.inject.Inject;
import javax.inject.Named;

public class UserDepot extends AbstractDepot<User> {
    @Inject
    public UserDepot(@Named("ttl") final long cacheTTL) {
        super(User.class, user -> user.getSub(), cacheTTL, UserFlat.class);
    }

    @Override
    protected User defaults(final Tenant tenant, final User value) {
        final UserBean user = new UserBean().from(value);

        if (Strings.isNullOrEmpty(user.getProfile())) {
            user.setProfile(Endpoint.USER.toProfile(tenant.getIssuer(), user.getSub()));
        }

        if (Strings.isNullOrEmpty(user.getMe())) {
            user.setMe(Endpoint.USER.toProfile(tenant.getIssuer(), user.getSub()));
        }

        if (Strings.isNullOrEmpty(user.getWebsite())) {
            user.setWebsite(tenant.getWebsite());
        }

        if (Strings.isNullOrEmpty(user.getId())) {
            user.setId(Endpoint.USER.toResource(tenant.getIssuer(), user.getSub()));
        }

        return user.toImmutable();
    }

    public Producer<Team> getTeamDepot() {
        return new Repository<Team>() {
            @Override
            public Team get(Tenant tenant, Name name) {
                return hasTemplate(tenant.getDepot(), tenant.getDepots()) ? getTeam(tenant, name) : null;
            }
        };
    }

    Team getTeam(final Tenant tenant, final Name input) {
        final Name name = input.ensureAuthority(tenant::getSub);

        return new Team.Builder()
                .setSub(name.getSubject())
                .setProfile(Endpoint.TEAM.toProfile(tenant.getIssuer(), name.getSubject()))
                .setMembers(list(tenant, name.getFlags().getSize()))
                .setLogo(tenant.getLogo())
                .setName(tenant.getName())
                .setId(Endpoint.TEAM.toResource(tenant.getIssuer(), name.getSubject()))
                .build();
    }

    @JsonDeserialize(as = UserBean.class)
    interface UserFlat extends User {
        @JsonUnwrapped(prefix = "address.")
        Address getAddress();
    }
}
