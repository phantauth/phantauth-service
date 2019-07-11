package com.github.phantauth.service.rest;

import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.resource.Name;
import com.github.phantauth.resource.TenantRepository;
import com.github.phantauth.resource.producer.Hashes;
import com.github.phantauth.resource.producer.faker.GenderResolver;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Random;

@Singleton
public class AvatarServlet extends AbstractImageServlet {

    private static final String AVATAR_FORMAT = IMAGE_URL_PREFIX + "/%s/%s.jpg";
    private static final int AVATAR_MAX = 3299;

    @Inject
    AvatarServlet(final TenantRepository tenantRepository) {
        super(Endpoint.AVATAR, tenantRepository);
    }

    @Override
    String getLocation(final Name name, final Random random) {
        final String gender;

        switch (name.getFlags().getGender()) {
            case MALE:
            case FEMALE:
                gender = name.getFlags().getGender().format();
                break;
            default:
                gender = GenderResolver.Gender.UNKNOWN.toProperty();
                break;
        }

        return String.format(AVATAR_FORMAT, gender, Hashes.hashids(random.nextInt(AVATAR_MAX)));
    }
}
