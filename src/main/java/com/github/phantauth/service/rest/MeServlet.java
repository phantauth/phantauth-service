package com.github.phantauth.service.rest;

import com.github.phantauth.core.Tenant;
import com.github.phantauth.core.User;
import com.github.phantauth.resource.Repository;
import com.github.phantauth.resource.TenantRepository;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.service.*;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import org.apache.commons.lang3.tuple.Pair;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Singleton
public class MeServlet extends AbstractServlet {
    private static final String VAR_USER = "user";
    private static final String TEMPLATE_USER = "user";

    private final Repository<User> repository;
    private final TemplateManager templateManager;

    @Inject
    MeServlet(final TenantRepository tenantRepository, final Repository<User> repository, final TemplateManager templateManager) {
        super(Endpoint.ME, tenantRepository);
        this.repository = repository;
        this.templateManager = templateManager;
    }

    @Override
    protected void handleGet(final HttpServletRequest servletRequest, final HttpServletResponse servletResponse) throws IOException {
        addIndieAuthHeaders(servletRequest, servletResponse);
        super.handleGet(servletRequest, servletResponse);
    }

    @Override
    protected void handlePost(final HttpServletRequest servletRequest, final HttpServletResponse servletResponse) throws IOException {
        addIndieAuthHeaders(servletRequest, servletResponse);
        super.handlePost(servletRequest, servletResponse);
    }

    protected HTTPResponse handleGet(final HTTPRequest req) {
        final Param param = Param.build(req, endpoint);
        return processTemplate(getTenant(req), TEMPLATE_USER , param);
    }

    @Override
    protected HTTPResponse handlePost(final HTTPRequest req) {
        return handleGet(req);
    }

    private HTTPResponse processTemplate(final Tenant tenant, final String template, final Param param) {
        final HTTPResponse response =  Response.html(templateManager.process(
                tenant,
                template,
                Pair.of(ResourceServlet.VAR_QUERY, param.getQuery()),
                Pair.of(ResourceServlet.VAR_PARAMS, param.getParams()),
                Pair.of(ResourceServlet.VAR_WIDGET, Optional.ofNullable(param.getArgument()).orElse("")),
                Pair.of(VAR_USER, repository.get(tenant, param.getSubject()))
        ));
        return cache(response, param.getSubject(), (int)TimeUnit.MILLISECONDS.toSeconds(templateManager.getTemplateTTL()));
    }

    void addIndieAuthHeaders(final HttpServletRequest servletRequest, final HttpServletResponse servletResponse) {
        final String issuer = tenantRepository.get(servletRequest.getParameter(PARAM_TENANT)).getIssuer();
        final String authorizationEndpoint = String.format("<%s>; rel=\"authorization_endpoint\"", Endpoint.AUTHORIZATION.toURI(issuer).toString());
        final String tokenEndpoint = String.format("<%s>; rel=\"token_endpoint\"", Endpoint.TOKEN.toURI(issuer).toString());

        servletResponse.addHeader("Link", authorizationEndpoint);
        servletResponse.addHeader("Link", tokenEndpoint);
    }
}
