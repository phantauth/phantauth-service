package com.github.phantauth.resource.producer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.phantauth.core.Domain;
import com.github.phantauth.core.Property;
import com.github.phantauth.core.Tenant;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.resource.Name;
import com.github.phantauth.resource.Producer;
import com.github.phantauth.resource.TenantRepository;
import com.google.common.collect.ImmutableList;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DNSDomainProducer implements Producer<Domain> {
    static final String PROP_TENANTS = "tenants";
    private static final String LOGO_PATTERN = "%s/logo/phantauth-logo.svg";

    private final ObjectMapper mapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private final Function<String, Map<String, Object>> txtMapper;
    private final TenantRepository tenantRepository;

    private final String defaultDomain;
    private final String defaultLogo;

    @Inject
    public DNSDomainProducer(@Named("serviceURI") final URI serviceURI, @Named("developerPortalURI") final URI developerPortalURI, @Named("txtMapper") final Function<String, Map<String, Object>> txtMapper, final TenantRepository tenantRepository) {
        this.txtMapper = txtMapper;
        defaultDomain = serviceURI.getHost();
        this.defaultLogo = String.format(LOGO_PATTERN, developerPortalURI.toString());
        this.tenantRepository = tenantRepository;
    }

    @Override
    public Domain get(final Tenant tenant, final Name input) {
        final Name name = input.ensureAuthority(() -> defaultDomain);
        final String base = name.getUserInfo();
        final int idx = base.lastIndexOf('@');
        final String hostname = idx < 0 ? base : base.substring(idx + 1);
        final Map<String, Object> props = txtMapper.apply(hostname);
        if ( props == null ) {
            return null;
        }

        return convert(tenant, props, name);
    }

    private Domain convert(final Tenant tenant, final Map<String, Object> props, final Name name) {

        props.put(Property.ID, Endpoint.DOMAIN.toResource(tenant.getIssuer(), name.getSubject()));
        props.put(Property.PROFILE, Endpoint.DOMAIN.toProfile(tenant.getIssuer(), name.getSubject()));
        props.put(Property.SUB, name.getSubject());

        if ( ! props.containsKey(Property.LOGO)) {
            props.put(Property.LOGO, defaultLogo);
        }

        final Domain domain = mapper.convertValue(props, Domain.class);
        final ImmutableList.Builder<Tenant> builder = ImmutableList.builder();

        if ( props.containsKey(PROP_TENANTS) ) {
            final Object tenants =  props.get(PROP_TENANTS);
            final List<String> list = (tenants instanceof List) ? (List<String>) tenants : ImmutableList.of((String) tenants);
            for(String value : list) {
                builder.add(tenantRepository.get(value));
            }
        }


        return new Domain.Builder().from(domain).addAllMembers(builder.build()).build();
    }
}
