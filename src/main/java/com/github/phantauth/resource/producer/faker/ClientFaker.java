package com.github.phantauth.resource.producer.faker;

import com.github.javafaker.Faker;
import com.github.phantauth.core.Client;
import com.github.phantauth.core.ClientBean;
import com.github.phantauth.core.Tenant;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.resource.Flags;
import com.github.phantauth.resource.Name;
import com.github.phantauth.resource.producer.FakeName;
import com.github.phantauth.resource.producer.Hashes;
import com.google.common.io.BaseEncoding;
import com.google.common.net.UrlEscapers;
import com.google.common.primitives.Longs;
import org.apache.commons.text.WordUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Random;

public class ClientFaker extends AbstractFaker<Client> {

    @Inject
    public ClientFaker() {
    }

    @Override
    public Client get(@Nonnull final Tenant tenant,  @Nullable final Name clientId) {

        return new ClientGen(tenant, clientId).toImmutable();
    }

    static class ClientGen extends ClientBean {

        private static final String LOGO_ICON_FORMAT = "https://avatars.phantauth.me/icon/%s.png";
        private static final int LOGO_ICON_MAX = 4099;
        private static final String LOGO_FRACTAL_FORMAT = "https://avatars.phantauth.me/fractal/%s.jpg";
        private static final int LOGO_FRACTAL_MAX = 1400;
        private static final String PICTURE_FORMAT = "https://www.gravatar.com/avatar/%s?s=256&d=%s";
        private static final String VERSION_PATTERN = "#.#.#";
        private static final String TOS_FORMAT = "%s/tos";
        private static final String POLICY_FORMAT = "%s/policy";
        private static final String PROFILE_FORMAT = "%s/profile";

        private static String newSubject() {
            return new StringBuilder(AbstractFaker.faker.app().name())
                    .append(Name.INSTANCE_SEPARATOR)
                    .append(BaseEncoding.base64Url().omitPadding().encode(Longs.toByteArray(AbstractFaker.faker.random().nextLong())))
                    .toString();
        }

        private ClientGen(final Tenant tenant, final Name clientId) {
            final FakeName fake = new FakeName(tenant, clientId, Endpoint.CLIENT, ClientGen::newSubject);

            setClientId(fake.getSubject());
            setClientSecret(fake.getPassword());
            setId(fake.getId());

            final String softwareNorm = newSoftwareName(fake);

            String email = fake.getInstance() == null && ! fake.isCustomDomain() ? fake.getEmail(softwareNorm, Hashes.shortHash(softwareNorm)) :
                    fake.isCustomDomain() ? fake.getEmail() : fake.getEmail(fake.getUserInfo(), Hashes.shortHash(softwareNorm));

            setLogoEmail(email);
            setLogoUri(newLogo(fake));

            setClientName(WordUtils.capitalize(softwareNorm.replace('.', ' ')));
            setSoftwareId(Hashes.md5Base64Url(softwareNorm));
            setSoftwareVersion(fake.getFairy().baseProducer().numerify(VERSION_PATTERN));
            setClientUri(String.format(PROFILE_FORMAT, fake.getId()));
            setTosUri(String.format(TOS_FORMAT, fake.getId()));
            setPolicyUri(String.format(POLICY_FORMAT, fake.getId()));
        }

        private String newSoftwareName(final FakeName fake) {
            return fake.getInstance() == null && ! fake.isCustomDomain() ? newAppName(fake.getSeed()).replace(' ', '.').toLowerCase() :
                    fake.getMailtag() == null ? fake.getUserInfo() :
                            fake.getMailtag();
        }

        private String newLogo(final FakeName fake) {
            final Flags.Logo flag = fake.getFlags().getLogo();

            final String defaultLogo;
            switch (flag) {
                case FRACTAL:
                    defaultLogo = String.format(LOGO_FRACTAL_FORMAT, fake.randomHashidBetween(0, LOGO_FRACTAL_MAX));
                    break;
                case ICON:
                    defaultLogo = String.format(LOGO_ICON_FORMAT, fake.randomHashidBetween(0, LOGO_ICON_MAX));
                    break;
                case NOLOGO:
                default:
                    return null;
            }

            return String.format(PICTURE_FORMAT, Hashes.md5(getLogoEmail()), UrlEscapers.urlFormParameterEscaper().escape(defaultLogo));
        }

        private String newAppName(final long seed) {
            return new Faker(new Random(seed)).app().name();
        }
    }

}
