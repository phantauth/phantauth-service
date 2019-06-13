package com.github.phantauth.service.rest;

import com.github.phantauth.core.Domain;
import com.github.phantauth.core.Tenant;
import com.github.phantauth.flow.AuthorizationFlow;
import com.github.phantauth.resource.Repository;
import com.github.phantauth.resource.TenantRepository;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.service.Param;
import com.github.phantauth.service.TemplateManager;
import com.github.phantauth.token.TenantTokenFactory;
import org.apache.commons.lang3.tuple.Pair;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TenantServlet extends ResourceServlet<Tenant> {

    private final AuthorizationFlow flow;
    private final Repository<Domain> domainRepository;

    @Inject
    TenantServlet(final Repository<Domain> domainRepository, final TenantRepository repository, final TenantTokenFactory tokenFactory, final TemplateManager templateManager, final AuthorizationFlow flow) {
        super(Tenant.class, Endpoint.TENANT, repository, repository, tokenFactory, templateManager);
        this.domainRepository = domainRepository;
        this.flow = flow;
    }

    @Override
    protected Pair[] getTemplateParams(final Tenant tenant, final Param param) {
        final boolean isDomain = tenant.isDomain();
        final Pair[] orig = super.getTemplateParams(tenant, param);
        final Pair[] pairs = new Pair[orig.length + (isDomain ? 2 : 1)];
        System.arraycopy(orig,0,pairs,0,orig.length);

        pairs[orig.length] = Pair.of("meta", flow.newOpenidConfiguration(tenant));

        if ( isDomain ) {
            pairs[orig.length+1] = Pair.of("domain", domainRepository.get(tenant, tenant.getSub()));
        }

        return pairs;
    }
}
