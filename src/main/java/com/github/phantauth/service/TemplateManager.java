package com.github.phantauth.service;

import com.damnhandy.uri.template.UriTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.phantauth.core.Views;
import com.github.phantauth.core.Tenant;
import com.github.phantauth.resource.Flags;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.experimental.Delegate;
import org.apache.commons.lang3.tuple.Pair;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.templateresource.UrlTemplateResource;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class TemplateManager {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
            .registerModule(new JavaTimeModule());

    private static final String KEY_DATA_TENANT = "dataTenant";
    private static final String KEY_TEMPLATE = "template";
    private static final String SUFFIX = ".html";

    private final TemplateEngine engine;

    @Getter
    private final long templateTTL;

    @Inject
    public TemplateManager(@Named("ttl") final long templateTTL) {

        engine = new TemplateEngine();
        this.templateTTL = templateTTL;

        final ClassLoaderTemplateResolver localResolver = new ClassLoaderTemplateResolver();
        localResolver.setPrefix("docroot/template/");
        localResolver.setCacheTTLMs(Long.MAX_VALUE);
        localResolver.setTemplateMode(TemplateMode.HTML);
        localResolver.setCacheable(true);
        localResolver.setSuffix(SUFFIX);
        localResolver.setOrder(1);
        localResolver.setCheckExistence(true);
        engine.addTemplateResolver(localResolver);


        final TenantTemplateResolver tenantResolver = new TenantTemplateResolver();
        tenantResolver.setCacheTTLMs(templateTTL);
        tenantResolver.setCacheable(templateTTL > 0);
        tenantResolver.setTemplateMode(TemplateMode.HTML);
        tenantResolver.setSuffix(SUFFIX);
        tenantResolver.setOrder(2);
        tenantResolver.setCheckExistence(true);
        engine.addTemplateResolver(tenantResolver);

        final ClassLoaderTemplateResolver defaultResolver = new ClassLoaderTemplateResolver();
        defaultResolver.setPrefix("docroot/default/");
        defaultResolver.setCacheTTLMs(Long.MAX_VALUE);
        defaultResolver.setTemplateMode(TemplateMode.HTML);
        defaultResolver.setCacheable(true);
        defaultResolver.setSuffix(SUFFIX);
        defaultResolver.setOrder(3);
        defaultResolver.setCheckExistence(true);
        engine.addTemplateResolver(defaultResolver);
    }

    @SafeVarargs
    public final String process(final Tenant tenant, final String template, final Pair<String, Object>... variables) {
        final Context context = new Context();
        final ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
        Tenant actualTenant = null;
        for (Pair<String, Object> variable : variables) {
            builder.put(variable.getKey(), variable.getValue());
            if ( Tenant.class.isAssignableFrom(variable.getValue().getClass()) ) {
                actualTenant = new TenantWrapper((Tenant) variable.getValue());
            }
        }
        if ( actualTenant == null ) {
            actualTenant = new TenantWrapper(tenant);
            builder.put(AbstractServlet.PARAM_TENANT, actualTenant);
        }
        builder.put(KEY_TEMPLATE, UriTemplate.fromTemplate(tenant.getTemplate()).set("resource", template + SUFFIX).expand());
        final TemplateSpec spec = new TemplateSpec(template, ImmutableMap.of(AbstractServlet.PARAM_TENANT, tenant));

        final Map<String, Object> map = builder.build();
        context.setVariables(map);
        context.setVariable(KEY_DATA_TENANT, toJSON(actualTenant));
        return engine.process(spec, context);
    }

    String toJSON(final Object value) {
        try {
            return MAPPER.writerWithView(Views.Meta.class).writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    static class TenantWrapper implements Tenant {
        @Delegate(types = Tenant.class)
        private final Tenant tenant;

        @Getter
        private final Flags flag;

        public TenantWrapper(final Tenant tenant) {
            this.tenant = tenant;
            this.flag = Flags.parse(tenant.getFlags() == null ? "" : tenant.getFlags());
        }
    }

    static class TenantTemplateResolver extends AbstractConfigurableTemplateResolver {

        @Override
        protected ITemplateResource computeTemplateResource(final IEngineConfiguration configuration, final String ownerTemplate, final String template, final String resourceName, final String characterEncoding, final Map<String, Object> templateResolutionAttributes) {
            final Tenant tenant = (Tenant) templateResolutionAttributes.get(AbstractServlet.PARAM_TENANT);

            try {
                final String uri = UriTemplate.fromTemplate(tenant.getTemplate()).set("resource", resourceName).expand();
                return new UrlTemplateResource(new URL(uri), characterEncoding);
            } catch (MalformedURLException ignored) {
                return null;
            }
        }
    }
}
