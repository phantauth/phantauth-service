package com.github.phantauth.resource.producer;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.URI;
import java.util.Map;
import java.util.function.Function;

public class TenantDomainTenantProducer extends DNSTenantProducer {

    private final String tenantDomainSuffix;

    @Inject
    public TenantDomainTenantProducer(@Named("tenantDomain") final String tenantDomain, @Named("serviceURI") final URI serviceURI, @Named("defaultTenantURI") final URI defaultTenantURI, @Named("developerPortalURI") final URI developerPortalURI, @Named("txtMapper") final Function<String, Map<String,Object>> txtMapper) {
        super(serviceURI, defaultTenantURI, developerPortalURI, txtMapper);
        this.tenantDomainSuffix = '.' + tenantDomain;
    }

    @Override
    protected String getDefaultDomainSuffix() {
        return tenantDomainSuffix;
    }
}
