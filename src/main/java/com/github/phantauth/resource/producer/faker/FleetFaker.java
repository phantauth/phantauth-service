package com.github.phantauth.resource.producer.faker;

import com.github.javafaker.Faker;
import com.github.phantauth.core.Client;
import com.github.phantauth.core.Fleet;
import com.github.phantauth.core.FleetBean;
import com.github.phantauth.core.Tenant;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.resource.Flags;
import com.github.phantauth.resource.Name;
import com.github.phantauth.resource.Producer;
import com.github.phantauth.resource.producer.FakeName;
import com.github.phantauth.resource.producer.Hashes;
import com.google.common.io.BaseEncoding;
import org.apache.commons.text.WordUtils;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Random;

public class FleetFaker extends AbstractFaker<Fleet> {

    private final Producer<Client> clientProducer;

    @Inject
    public FleetFaker(final Producer<Client> clientProducer) {
        this.clientProducer = clientProducer;
    }

    @Override
    public Fleet get(@Nonnull final Tenant tenant, @Nonnull final Name subject) {
        return new FleetGen(tenant, subject, clientProducer).toImmutable();
    }

    static class FleetGen extends FleetBean {

        private static final String PROFILE_FORMAT = "%s/profile";
        private static final String PICTURE_FORMAT = "https://www.gravatar.com/avatar/%s?s=256&d=identicon";

        private static String newSubject() {
            return AbstractFaker.fairy.company().getName();
        }

        private final Producer<Client> clientProducer;

        private FleetGen(final Tenant tenant, final Name name, Producer<Client> clientProducer) {
            this.clientProducer = clientProducer;
            final FakeName fake = new FakeName(tenant, name, Endpoint.FLEET, FleetGen::newSubject);

            setId(fake.getId());
            setSub(fake.getSubject());

            String fleetname = fake.getMailtag() == null ? fake.getUserInfo() : fake.getMailtag();

            setName(WordUtils.capitalize(fleetname.replace('.', ' ')));

            setProfile(String.format(PROFILE_FORMAT, fake.getId()));

            setLogoEmail(fake.isCustomDomain() ? fake.getEmail() : fake.getEmail(fake.getUserInfo(), fake.getDistinct()));
            setLogo(String.format(PICTURE_FORMAT, Hashes.md5(getLogoEmail())));

            addMembers(tenant, fake);
        }

        private void addMembers(final Tenant tenant, final FakeName fake) {
            final Flags childFlags = new Flags.Builder().from(fake.getFlags()).setSize(Flags.Size.DEFAULT).build();
            final String nameSuffix = childFlags.isDefault() ? "" : Flags.DELIMITER + childFlags.format();
            final Random random = new Random(fake.getSeed());
            final Faker faker = new Faker(random);

            for(int i=0; i < fake.getFlags().getSize().getLimit(); i++) {
                final StringBuilder buff = new StringBuilder(faker.app().name()).append('~');
                final byte[] bytes = new byte[Long.BYTES];
                random.nextBytes(bytes);
                buff.append(BaseEncoding.base64Url().omitPadding().encode(bytes)).append(nameSuffix);

                addMembers(clientProducer.get(tenant, Name.parse(buff.toString())));
            }
        }
    }

}
