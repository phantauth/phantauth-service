package com.github.phantauth.service.rest;

import com.github.phantauth.core.User;
import com.github.phantauth.resource.Repository;
import com.github.phantauth.resource.TenantRepository;
;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.service.TemplateManager;
import com.github.phantauth.token.UserTokenFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserServlet extends ResourceServlet<User> {

    @Inject
    UserServlet(final TenantRepository tenantRepository, final Repository<User> repository, final UserTokenFactory tokenFactory, final TemplateManager templateManager) {
        super(User.class, Endpoint.USER, tenantRepository, repository, tokenFactory, templateManager);
    }
}
