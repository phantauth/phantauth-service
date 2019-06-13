package com.github.phantauth.resource.producer;

import com.github.phantauth.resource.Name;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.URI;
import java.util.*;
import java.util.function.Function;

public class DNSTenantProducer extends AbstractTenantProducer {

    private final Function<String,Map<String, Object>> txtMapper;

    @Inject
    public DNSTenantProducer(@Named("serviceURI") final URI serviceURI, @Named("defaultTenantURI") final URI defaultTenantURI, @Named("txtMapper") final Function<String,Map<String, Object>> txtMapper) {
        super(serviceURI, defaultTenantURI);
        this.txtMapper = txtMapper;
    }

    @Override
    protected Map<String, Object> read(final Name name) {

        final String base = name.getUserInfo();
        final int idx = base.lastIndexOf('@');
        final String hostname = fqdn(idx < 0 ? base : base.substring(idx + 1));

        return txtMapper.apply(hostname);
    }
}
