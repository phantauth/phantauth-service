package com.github.phantauth.service.rest;

import com.github.phantauth.core.Tenant;
import com.github.phantauth.exception.RequestMethodException;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.resource.TenantRepository;
import com.github.phantauth.service.*;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import org.apache.commons.lang3.tuple.Pair;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.github.phantauth.service.rest.ResourceServlet.VAR_WIDGET;

@Singleton
public class TestServlet extends AbstractServlet {

    private final TemplateManager templateManager;

    @Inject
    TestServlet(final TenantRepository tenantRepository, final TemplateManager templateManager) {
        super(Endpoint.TEST, tenantRepository);
        this.templateManager = templateManager;
    }

    @Override
    protected HTTPResponse handleGet(final HTTPRequest req) {
        final Param param = Param.build(req, Endpoint.TEST);
        final Tenant tenant = getTenant(req);

        final HTTPResponse response = Response.html(templateManager.process(tenant,"test", Pair.of(VAR_WIDGET, Optional.ofNullable(param.getSubject()).orElse(""))));
        return cache(response, (int) TimeUnit.MILLISECONDS.toSeconds(templateManager.getTemplateTTL()));
    }

    @Override
    protected HTTPResponse handlePost(final HTTPRequest req) {
        throw new RequestMethodException(req.getMethod().name());
    }
}
