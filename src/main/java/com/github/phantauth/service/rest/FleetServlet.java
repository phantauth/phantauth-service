package com.github.phantauth.service.rest;

import com.github.phantauth.core.Fleet;
;
import com.github.phantauth.resource.Repository;
import com.github.phantauth.resource.TenantRepository;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.service.TemplateManager;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FleetServlet extends ResourceServlet<Fleet> {

    @Inject
    FleetServlet(final TenantRepository tenantRepository, final Repository<Fleet> repository, final TemplateManager templateManager) {
        super(Fleet.class, Endpoint.FLEET, tenantRepository, repository, null, templateManager);
    }
}
