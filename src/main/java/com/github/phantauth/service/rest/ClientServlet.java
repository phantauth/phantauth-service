package com.github.phantauth.service.rest;

import com.github.phantauth.core.Client;
import com.github.phantauth.resource.Repository;
import com.github.phantauth.resource.TenantRepository;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.service.TemplateManager;
import com.github.phantauth.token.ClientTokenFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ClientServlet extends ResourceServlet<Client> {

    @Inject
    ClientServlet(final TenantRepository tenantRepository, final Repository<Client> repository, final ClientTokenFactory tokenFactory, final TemplateManager templateManager) {
        super(Client.class, Endpoint.CLIENT, tenantRepository, repository, tokenFactory, templateManager);
    }
}
