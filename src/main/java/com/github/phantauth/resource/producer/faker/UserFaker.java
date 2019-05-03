package com.github.phantauth.resource.producer.faker;

import com.devskiller.jfairy.producer.person.Country;
import com.devskiller.jfairy.producer.person.Person;
import com.devskiller.jfairy.producer.person.PersonProperties;
import com.github.phantauth.config.Config;
import com.github.phantauth.core.Address;
import com.github.phantauth.core.User;
import com.github.phantauth.core.Tenant;
import com.github.phantauth.core.UserBean;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.resource.Flags;
import com.github.phantauth.resource.Name;
import com.github.phantauth.resource.producer.FakeName;
import com.github.phantauth.resource.producer.Hashes;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

public class UserFaker extends AbstractFaker<User> {

    @Inject
    public UserFaker() {
        // just for Inject annotation
    }

    @Override
    public User get(@Nonnull final Tenant tenant, @Nonnull final Name name) {
        return new UserGen(tenant, indieFix(tenant, name)).toImmutable();
    }

    private Name indieFix(final Tenant tenant, final Name name) {
        final String sub = name.getSubject();
        final String me = Endpoint.ME.resolve(tenant.getIssuer());

        if ( ! sub.startsWith(me) ) {
            return name;
        }

        if ( me.equals(sub) ) {
            return Name.EMPTY;
        }

        return Name.parse(sub.substring(me.length()+1));
    }

    static class UserGen extends UserBean {

        private static final Map<Country, String> TZS = ImmutableMap.<Country, String>builder()
                .put(Country.UnitedKingdom, "Europe/London")
                .put(Country.Sweden, "Europe/Stockholm")
                .put(Country.Spain, "Europe/Madrid")
                .put(Country.Poland, "Europe/Warsaw")
                .put(Country.Italy, "Europe/Rome")
                .put(Country.Germany, "Europe/Berlin")
                .put(Country.France, "Europe/Paris")
                .put(Country.China, "Asia/Shanghai")
                .put(Country.Canada, "Canada/Central")
                .put(Country.Australia, "Australia/Sydney")
                .put(Country.USA, "America/Chicago").build();

        private static final Pattern HAS_FULL_NAME = Pattern.compile("^~?\\p{L}+[ .]\\p{L}+([ .]\\p{L}+)?$");
        private static final String PICTURE_FORMAT = "https://www.gravatar.com/avatar/%s?s=256&d=%s";
        private static final String AVATARS_PREFIX = "https://avatars.";
        private static final String PICTURE_AI_FORMAT = AVATARS_PREFIX + Config.DEFAULT_DOMAIN + "/ai/%s/%s.jpg";
        private static final int PICTURE_AI_MAX = 3299;
        private static final String PICTURE_PHOTO_FORMAT = AVATARS_PREFIX + Config.DEFAULT_DOMAIN + "/photo/%s/%s.jpg";
        private static final int PICTURE_PHOTO_MAX = 299;
        private static final String PICTURE_DICE_FORMAT = AVATARS_PREFIX + Config.DEFAULT_DOMAIN + "/dice/%s/%s.png";
        private static final int PICTURE_DICE_MAX = 299;
        private static final String PICTURE_SKETCH_FORMAT = AVATARS_PREFIX + Config.DEFAULT_DOMAIN + "/sketch/%s/%s.jpg";
        private static final int PICTURE_SKETCH_MAX = 999;
        private static final String PICTURE_KITTEN_FORMAT = AVATARS_PREFIX + Config.DEFAULT_DOMAIN + "/kitten/%s.jpg";
        private static final int PICTURE_KITTEN_MAX = 299;
        private static final String PICTURE_ADORABLE_FORMAT = "https://api.adorable.io/avatars/256/%s.png";

        private static String newSubject() {
            return AbstractFaker.faker.name().username();
        }

        private UserGen(final Tenant tenant, final Name name) {
            final FakeName fake = new FakeName(tenant, name, Endpoint.USER, UserGen::newSubject);
            final Person person = newPerson(fake);

            setGender(newGender(fake, person));
            setZoneinfo(TZS.containsKey(person.getNationality()) ? TZS.get(person.getNationality()) : TimeZone.getDefault().getID());
            setPicture(newAvatar(fake));
            setPreferredUsername(StringUtils.stripAccents(person.getFirstName().charAt(0) + person.getLastName()).toLowerCase());

            setProfile(Endpoint.USER.toProfile(tenant.getIssuer(), fake.getSubject()));
            setMe(Endpoint.ME.toProfile(tenant.getIssuer(), fake.getSubject()));

            if (fake.getFlags().getMail() != Flags.Mail.NOEMAIL) {
                setEmail(person.getEmail());
                setEmailVerified(fake.getFairy().baseProducer().trueOrFalse());
                if ( ! fake.isCustomDomain() ) {
                    setWebmail(String.format("https://www.mailinator.com/v3/?zone=public&query=%s.%s", fake.getUserInfo(), fake.getDistinct()));
                }
            }

            setBirthdate(person.getDateOfBirth().format(DateTimeFormatter.ISO_DATE));

            setLocale(LocaleUtils.languagesByCountry(person.getNationality().getCode()).get(0).toString());

            setUpdatedAt(person.getDateOfBirth().withYear(LocalDate.now().minusYears(1).getYear()).atStartOfDay().toEpochSecond(ZoneOffset.UTC));

            setId(fake.getId());
            setSub(person.getUsername());
            setFamilyName(person.getLastName());
            setGivenName(person.getFirstName());
            setNickname(person.getFirstName());
            setMiddleName(person.getMiddleName());
            setName(person.getFullName());
            setPhoneNumber(person.getTelephoneNumber());
            setPhoneNumberVerified(fake.getFairy().baseProducer().trueOrFalse());
            setWebsite(tenant.getWebsite());
            setPassword(person.getPassword());
            setAddress(new Address.Builder()
                    .setCountry(person.getNationality().name())
                    .setLocality(person.getAddress().getCity())
                    .setPostalCode(person.getAddress().getPostalCode())
                    .setFormatted(person.getAddress().toString())
                    .setStreetAddress(person.getAddress().getAddressLine1())
                    .build()
            );
        }

        private Person newPerson(final FakeName fake) {
            final List<PersonProperties.PersonProperty> properties = new ArrayList<>();

            properties.add(PersonProperties.withEmail(fake.getEmail(fake.getUserInfo(), fake.getDistinct())));
            properties.add(PersonProperties.withUsername(fake.getSubject()));
            properties.add(PersonProperties.withPassword(fake.getPassword()));


            String name = fake.getUserInfo() != null && HAS_FULL_NAME.matcher(fake.getUserInfo()).matches() ? fake.getUserInfo() :
                    fake.getMailtag() != null && HAS_FULL_NAME.matcher(fake.getMailtag()).matches() ? fake.getMailtag() :
                            null;

            if (name != null) {
                final StringTokenizer tokenizer = new StringTokenizer(name, ".");

                properties.add(PersonProperties.withFirstName(StringUtils.capitalize(tokenizer.nextToken())));
                properties.add(PersonProperties.withMiddleName(tokenizer.countTokens() > 1 ? StringUtils.capitalize(tokenizer.nextToken()) : ""));
                properties.add(PersonProperties.withLastName(StringUtils.capitalize(tokenizer.nextToken())));

            }

            return fake.getFairy().person(properties.toArray(new PersonProperties.PersonProperty[]{}));
        }

        private String newGender(final FakeName fake, final Person person) {
            final String gender;

            switch (fake.getFlags().getGender()) {
                case MALE:
                case FEMALE:
                    gender = fake.getFlags().getGender().format();
                    break;
                case GUESS:
                    gender = GenderResolver.getGender(person.getFirstName(), person.getLastName()).toProperty();
                    break;
                case NOGENDER:
                    gender = null;
                    break;
                default:
                    gender = GenderResolver.Gender.UNKNOWN.toProperty();
                    break;
            }

            return gender;
        }

        private String newAvatar(final FakeName fake) {
            final Flags.Avatar flag = fake.getFlags().getAvatar();

            if (flag == Flags.Avatar.NOAVATAR) {
                return null;
            }

            final String mailhash = Hashes.md5(fake.isCustomDomain() ? fake.getEmail() : fake.getEmail(fake.getUserInfo(), fake.getDistinct()));

            final String defaultPicture;
            switch (flag) {
                case BLANK:
                case IDENTICON:
                case MONSTERID:
                case MP:
                case RETRO:
                case ROBOHASH:
                case WAVATAR:
                    defaultPicture = flag.format();
                    break;
                case AI:
                    defaultPicture = String.format(PICTURE_AI_FORMAT, getGender(), fake.randomHashidBetween(0, PICTURE_AI_MAX));
                    break;
                case PHOTO:
                    defaultPicture = String.format(PICTURE_PHOTO_FORMAT, getGender(), fake.randomHashidBetween(0, PICTURE_PHOTO_MAX));
                    break;
                case SKETCH:
                    defaultPicture = String.format(PICTURE_SKETCH_FORMAT, getGender(), fake.randomHashidBetween(0, PICTURE_SKETCH_MAX));
                    break;
                case DICE:
                    if (getGender().equalsIgnoreCase(GenderResolver.Gender.UNKNOWN.toString())) {
                        defaultPicture = Flags.Avatar.RETRO.format();
                    } else {
                        defaultPicture = String.format(PICTURE_DICE_FORMAT, getGender(), fake.randomHashidBetween(0, PICTURE_DICE_MAX));
                    }
                    break;
                case KITTEN:
                    defaultPicture = String.format(PICTURE_KITTEN_FORMAT, fake.randomHashidBetween(0, PICTURE_KITTEN_MAX));
                    break;
                case ADORABLE:
                    defaultPicture = String.format(PICTURE_ADORABLE_FORMAT, mailhash);
                    break;
                case NOTFOUND:
                default:
                    defaultPicture = "404";
                    break;
            }

            return String.format(PICTURE_FORMAT, mailhash, defaultPicture);
        }
    }

}
