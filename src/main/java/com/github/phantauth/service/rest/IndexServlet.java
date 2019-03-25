package com.github.phantauth.service.rest;

import com.github.phantauth.core.Domain;
import com.github.phantauth.exception.RequestMethodException;
import com.github.phantauth.core.Tenant;
import com.github.phantauth.flow.AuthorizationFlow;
import com.github.phantauth.resource.Repository;
import com.github.phantauth.resource.TenantRepository;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.service.AbstractServlet;
import com.github.phantauth.service.Response;
import com.github.phantauth.service.TemplateManager;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import org.apache.commons.lang3.tuple.Pair;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

@Singleton
public class IndexServlet extends AbstractServlet {

    private final AuthorizationFlow flow;
    private final TemplateManager templateManager;
    private final Repository<Domain> domainRepository;

    @Inject
    IndexServlet(final Repository<Domain> domainRepository, final TenantRepository tenantRepository, final TemplateManager templateManager, final AuthorizationFlow flow) {
        super(Endpoint.INDEX, tenantRepository);
        this.templateManager = templateManager;
        this.flow = flow;
        this.domainRepository = domainRepository;
    }

    @Override
    protected HTTPResponse handleGet(final HTTPRequest req) {
        final Tenant tenant = getSubTenant(req);

        final HTTPResponse response = Response.html(templateManager.process(tenant,"tenant", getTemplateParams(tenant)));
        return cache(response, (int) TimeUnit.MILLISECONDS.toSeconds(templateManager.getTemplateTTL()));
    }

    Pair[] getTemplateParams(final Tenant tenant) {
        final boolean isDomain = tenant.isDomain();
        final Pair[] pairs = new Pair[2 + (isDomain ? 1 : 0)];
        pairs[0] = Pair.of("meta", flow.newOpenidConfiguration(tenant));
        pairs[1] = Pair.of(ResourceServlet.VAR_WIDGET, "");
        if ( isDomain ) {
            pairs[2] = Pair.of("domain", domainRepository.get(tenant, tenant.getSub()));
        }
        return pairs;
    }

    @Override
    protected HTTPResponse handlePost(final HTTPRequest req) {
        throw new RequestMethodException(req.getMethod().name());
    }
}
