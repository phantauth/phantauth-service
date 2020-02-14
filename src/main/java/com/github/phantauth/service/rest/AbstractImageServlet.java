package com.github.phantauth.service.rest;

import com.github.javafaker.Faker;
import com.github.phantauth.config.Config;
import com.github.phantauth.exception.RequestMethodException;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.resource.Name;
import com.github.phantauth.resource.TenantRepository;
import com.github.phantauth.resource.producer.Hashes;
import com.github.phantauth.service.AbstractServlet;
import com.github.phantauth.service.Param;
import com.github.phantauth.service.Response;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;

import java.net.URI;
import java.util.Random;
import java.util.concurrent.TimeUnit;

abstract class AbstractImageServlet extends AbstractServlet {

    static final String IMAGE_URL_PREFIX = "https://image." + Config.DEFAULT_DOMAIN;
    static final Faker faker = new Faker();

    AbstractImageServlet(final Endpoint endpoint, final TenantRepository tenantRepository) {
        super(endpoint, tenantRepository);
    }

    abstract String getLocation(Name name, Random random);

    @Override
    protected HTTPResponse handleGet(HTTPRequest req) {
        final Param param = Param.build(req, endpoint);
        final Name name = Name.parse(param.getSubject()).ensureAuthority(faker.name()::username);
        final Random random = new Random(Hashes.hashSeed(name.getRaw()));

        final String location = getLocation(name, random);

        final HTTPResponse response = Response.redirect(URI.create(location), param.getSubject());

        return cache(response, param.getSubject(), (int) TimeUnit.DAYS.toSeconds(1));
    }

    @Override
    protected HTTPResponse handlePost(HTTPRequest req) {
        throw new RequestMethodException(req.getMethod().name());
    }

}
