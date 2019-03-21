package com.github.phantauth.resource.producer;

import com.damnhandy.uri.template.UriTemplate;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.phantauth.core.*;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.resource.Name;
import com.github.phantauth.resource.Producer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.text.WordUtils;

import java.net.URI;
import java.util.List;
import java.util.Map;

abstract class AbstractTenantProducer implements Producer<Tenant> {
    private static final List<String> DEFAULT_FACTORIES = ImmutableList.of(name(User.class));
    private static final List<String> DEFAULT_DEPOTS = ImmutableList.of(name(User.class), name(Team.class));

    @Getter(AccessLevel.PROTECTED)
    private final ObjectMapper mapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private static final char TENANT_PREFIX = '_';
    private static final String DEFAULT_TEMPLATE_PATTERN = "%s{/resource}";
    private static final String ISSUER_PATTERN = "%s{/tenant}";
    private static final String LOGO_PATTERN = "%s/logo/phantauth-logo-light.svg";

    private static final String GOOGLE_SHEET_PATTERN="https://docs.google.com/spreadsheets/d/%s/gviz/tq?tqx=out:csv";

    @Getter(AccessLevel.PROTECTED)
    private final String defaultTenantId;

    private final String serviceURI;
    @Getter(AccessLevel.PROTECTED)
    private final String defaultDomainSuffix;
    private final String defaultDomain;
    private final String defaultTenantTemplate;
    private final String defaultLogo;
    private final UriTemplate issuerTemplate;

    AbstractTenantProducer(final URI serviceURI, final URI defaultTenantURI, final URI developerPortalURI) {
        this.serviceURI = serviceURI.toString();
        this.defaultDomain = serviceURI.getHost();
        this.defaultDomainSuffix = '.' + serviceURI.getHost();
        this.defaultTenantId = getDefaultTenantId(serviceURI, defaultTenantURI);
        this.defaultTenantTemplate = String.format(DEFAULT_TEMPLATE_PATTERN, defaultTenantURI.toString());
        this.defaultLogo = String.format(LOGO_PATTERN, developerPortalURI.toString());
        this.issuerTemplate = UriTemplate.fromTemplate(String.format(ISSUER_PATTERN, this.serviceURI));
    }

    private static String getDefaultTenantId(final URI serviceURI, final URI defaultTenantURI) {
        final String tenantHost = defaultTenantURI.getHost();
        final String serviceHost = serviceURI.getHost();

        if ( serviceHost.equals(tenantHost) || ! tenantHost.endsWith(serviceHost) ) {
            return defaultTenantURI.getAuthority();
        }

        return tenantHost.substring(0,tenantHost.indexOf('.'));
    }

    String getIssuer(final Name name) {
        return name.isAuthorityEmpty() || name.getAuthority().equals(defaultTenantId) ?
                serviceURI
                : issuerTemplate.expand(ImmutableMap.of("tenant", TENANT_PREFIX + (name.getAuthority().startsWith(defaultDomain) ? name.getAuthority().substring(defaultDomain.length()) : name.getSubject()) ));
    }

    protected String fqdn(final String host) {
        return host.indexOf('.') >= 0 ? host : host + getDefaultDomainSuffix();
    }

    private Map<String, Object> defaults(final Map<String, Object> props, final Name name) {

        if ( props.containsKey(Tenant.FACTORY) && ! props.containsKey(Tenant.FACTORIES)) {
            props.put(Tenant.FACTORIES, DEFAULT_FACTORIES);
        }

        if (props.containsKey(Tenant.SHEET)) {
            props.put(Tenant.DEPOT, String.format(GOOGLE_SHEET_PATTERN, props.get(Tenant.SHEET)));
        }

        if ( props.containsKey(Tenant.DEPOT) && ! props.containsKey(Tenant.DEPOTS) ) {
            props.put(Tenant.DEPOTS, DEFAULT_DEPOTS);
        }

        if ( ! props.containsKey(Tenant.TEMPLATE) ) {
            props.put(Tenant.TEMPLATE, defaultTenantTemplate);
        }

        if ( ! name.getFlags().isDefault() ) {
            props.put(Tenant.FLAGS, name.getFlags().format(false));
        }

        if ( ! props.containsKey(Tenant.NAME) ) {
            props.put(Tenant.NAME, WordUtils.capitalize(name.getUserInfo().replace('.',' ')));
        }

        if ( ! props.containsKey(Tenant.LOGO)) {
            props.put(Tenant.LOGO, defaultLogo);
        }

        final String issuer = getIssuer(name);

        if ( ! props.containsKey(Tenant.WEBSITE)) {
            props.put(Tenant.WEBSITE, issuer);
        }

        props.put(Tenant.ISSUER, issuer);
        props.put(Tenant.ID, Endpoint.TENANT.toResource(issuer, name.getSubject()));
        props.put(Tenant.SUB, name.getSubject());
        if ( ! name.isHostEmpty() ) {
            props.put(Tenant.USERINFO, name.getUserInfo());
        }
        if ( ! name.isInstanceEmpty() ) {
            props.put(Tenant.SUBTENANT, true);
        }

        if ( props.containsKey(DNSDomainProducer.PROP_TENANTS) ) {
            props.put(Tenant.DOMAIN, true);
        }

        return props;
    }

    private Tenant convert(final Map<String,Object> props) {
        return mapper.convertValue(props, Tenant.class);
    }

    protected abstract Map<String, Object> read(final Name name);

    @Override
    public Tenant get(final Tenant tenant, final Name input) {
        final Name name = input.isInstanceEmpty()
                ? input.ensureAuthority(() -> defaultTenantId)
                : input.ensureUserInfo(() -> defaultDomain);
        final Map<String, Object> props = read(name);
        if ( props == null ) {
            return null;
        }
        return convert(defaults(props, name));
    }

    private static String name(final Class clazz) {
        return clazz.getSimpleName().toLowerCase();
    }
}
