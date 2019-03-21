package com.github.phantauth.service.rest;

import com.github.phantauth.core.Domain;
import com.github.phantauth.core.Tenant;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.resource.Repository;
import com.github.phantauth.resource.TenantRepository;
import com.github.phantauth.service.TemplateManager;
import com.github.phantauth.token.TenantTokenFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DomainServlet extends ResourceServlet<Domain> {

    @Inject
    DomainServlet(final TenantRepository tenantRepository, final Repository<Domain> repository, final TemplateManager templateManager) {
        super(Domain.class, Endpoint.DOMAIN, tenantRepository, repository, null, templateManager);
    }
}
