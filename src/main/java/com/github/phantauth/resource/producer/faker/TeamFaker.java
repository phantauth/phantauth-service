package com.github.phantauth.resource.producer.faker;

import com.devskiller.jfairy.producer.person.Person;
import com.github.phantauth.core.Team;
import com.github.phantauth.core.TeamBean;
import com.github.phantauth.core.Tenant;
import com.github.phantauth.core.User;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.resource.Flags;
import com.github.phantauth.resource.Name;
import com.github.phantauth.resource.Producer;
import com.github.phantauth.resource.producer.FakeName;
import com.github.phantauth.resource.producer.Hashes;
import org.apache.commons.text.WordUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

public class TeamFaker extends AbstractFaker<Team> {

    private final Producer<User> userProducer;

    @Inject
    public TeamFaker(final Producer<User> userProducer) {
        this.userProducer = userProducer;
    }

    @Override
    public Team get(@Nonnull final Tenant tenant, @Nullable final Name subject) {
        return new TeamGen(tenant, subject, userProducer).toImmutable();
    }

    static class TeamGen extends TeamBean {

        private static final String PROFILE_FORMAT = "%s/profile";
        private static final String PICTURE_FORMAT = "https://www.gravatar.com/avatar/%s?s=256&d=identicon";

        private static String newSubject() {
            return AbstractFaker.fairy.company().getName();
        }

        private final Producer<User> userProducer;

        private TeamGen(final Tenant tenant, final Name name, final Producer<User> userProducer) {
            this.userProducer = userProducer;
            final FakeName fake = new FakeName(tenant, name, Endpoint.TEAM, TeamGen::newSubject);

            setId(fake.getId());
            setSub(fake.getSubject());

            String teamname = fake.getMailtag() == null ? fake.getUserInfo() : fake.getMailtag();

            setName(WordUtils.capitalize(teamname.replace('.', ' ')));

            setProfile(String.format(PROFILE_FORMAT, fake.getId()));
            setLogoEmail(fake.isCustomDomain() ? fake.getEmail() : fake.getEmail(fake.getUserInfo(), fake.getDistinct()));
            setLogo(String.format(PICTURE_FORMAT, Hashes.md5(getLogoEmail())));

            addMembers(tenant, fake);
        }

        private void addMembers(final Tenant tenant, final FakeName fake) {
            final Flags childFlags = new Flags.Builder().from(fake.getFlags()).setSize(Flags.Size.DEFAULT).build();
            final String nameSuffix = childFlags.isDefault() ? "" : Flags.DELIMITER + childFlags.format();

            for(int i=0; i < fake.getFlags().getSize().getLimit(); i++) {
                final Person person = fake.getFairy().person();

                StringBuilder buff = new StringBuilder(person.getFirstName());
                if (person.getMiddleName() != null && person.getMiddleName().length() > 0) {
                    buff.append(' ').append(person.getMiddleName());
                }
                buff.append(' ').append(person.getLastName()).append(nameSuffix);

                addMembers(userProducer.get(tenant, Name.parse(buff.toString())));
            }
        }
    }

}
