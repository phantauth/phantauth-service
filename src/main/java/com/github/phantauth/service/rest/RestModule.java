package com.github.phantauth.service.rest;

import com.github.phantauth.service.AbstractServlet;
import com.google.common.collect.ImmutableSet;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;

import java.util.Set;

@Module
public class RestModule {

    @Provides
    @ElementsIntoSet
    static Set<AbstractServlet> provideRestServlets(final UserServlet user, final TeamServlet team, final ClientServlet client, final FleetServlet fleet, final TenantServlet tenant, final DomainServlet domain) {
        return ImmutableSet.of(user, team, client, fleet, tenant, domain);
    }

    @Provides
    @ElementsIntoSet
    static Set<AbstractServlet> provideOtherServlets(final IndexServlet index, final MeServlet me, final TestServlet test) {
        return ImmutableSet.of(index, me, test);
    }
}
