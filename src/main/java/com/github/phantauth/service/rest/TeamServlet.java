package com.github.phantauth.service.rest;

import com.github.phantauth.core.Team;
import com.github.phantauth.resource.Repository;
import com.github.phantauth.resource.TenantRepository;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.service.TemplateManager;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TeamServlet extends ResourceServlet<Team> {

    @Inject
    TeamServlet(final TenantRepository tenantRepository, final Repository<Team> repository, final TemplateManager templateManager) {
        super(Team.class, Endpoint.TEAM, tenantRepository, repository, null, templateManager);
    }
}
