package com.github.phantauth.service.rest;

import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.resource.Name;
import com.github.phantauth.resource.TenantRepository;
import com.github.phantauth.resource.producer.Hashes;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Random;

@Singleton
public class LogoServlet extends AbstractImageServlet {

    private static final String LOGO_FORMAT = IMAGE_URL_PREFIX + "/logo/icon/%s.png";
    private static final int LOGO_MAX = 4099;

    @Inject
    LogoServlet(final TenantRepository tenantRepository) {
        super(Endpoint.LOGO, tenantRepository);
    }

    @Override
    String getLocation(final Name name, final Random random) {
        return String.format(LOGO_FORMAT, Hashes.hashids(random.nextInt(LOGO_MAX)));
    }
}
