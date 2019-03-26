package com.github.phantauth.resource.producer;

import com.devskiller.jfairy.Fairy;
import com.github.phantauth.config.Config;
import com.github.phantauth.core.Tenant;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.resource.Flags;
import com.github.phantauth.resource.Name;
import com.google.common.collect.ImmutableSet;
import com.google.common.hash.Hashing;
import lombok.Getter;
import lombok.experimental.Delegate;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

@Getter
public class FakeName extends Name {
    private static final String DEFAULT_MAIL_DOMAIN = "mailinator.com";
    private static final char MAIL_SEPARATOR = '@';

    private static final int PASSWORD_LENGTH = 8;
    private static final Set<String> DOT_DOMAINS = ImmutableSet.of(DEFAULT_MAIL_DOMAIN);
    private static final Set<String> PLUS_DOMAINS = ImmutableSet.of("gmail.com", "outlook.com", "outlook.hu", "zoho.com", "protonmail.com", Config.DEFAULT_DOMAIN);
    private static final int HASHIDS_MIN_LENGTH = 8;
    private static final String DEFAULT_MAIL_TAG = "phantauth";

    @Delegate
    private final Name name;

    private final Tenant tenant;

    private final URI endpointURI;

    private final String domain;
    private final String mailbox;
    private final String id;
    private final String password;
    private final String distinct;
    private final char mailtagSeparator;
    private final String mailtag;
    private final int seed;
    private final Fairy fairy;

    public FakeName(final Tenant tenant, final Name input, final Endpoint endpoint, final Supplier<String> newAuthority) {
        name = input.ensureAuthority(newAuthority);
        this.tenant = tenant;
        this.endpointURI = endpoint.toURI(tenant.getIssuer());

        domain = name.isHostEmpty() ? DEFAULT_MAIL_DOMAIN : name.getHost();

        seed = newSeed();
        fairy =  Fairy.builder().withRandomSeed(seed).build();
        password = new PasswordGenerator(new Random(seed)).generatePassword(PASSWORD_LENGTH,
                        new CharacterRule(EnglishCharacterData.UpperCase, 1),
                        new CharacterRule(EnglishCharacterData.LowerCase, 1),
                        new CharacterRule(EnglishCharacterData.Digit, 1));

        distinct = Hashes.shortHash(password);

        mailtagSeparator = isPlusDomain() ? '+' : isDotDomain() ? '.' : 0;

        mailbox = getUserInfo();

        if ( mailbox != null && isCustomDomain() && isTagCapableDomain() && mailbox.indexOf(mailtagSeparator) >= 0 ) {
            mailtag = mailbox.substring(mailbox.indexOf(mailtagSeparator) + 1);
        } else {
            mailtag = null;
        }

        id = endpoint.toResource(tenant.getIssuer(), getSubject());
    }

    private int newSeed() {
        return Hashing.farmHashFingerprint64().hashString(getUserInfo(), StandardCharsets.UTF_8).asInt();
    }

    public String getEmail() {
        return getEmail(getUserInfo());
    }

    public String getEmail(final String mailbox) {
        return mailbox + MAIL_SEPARATOR + domain;
    }

    public String getEmail(final String mailbox, final String tag) {
        final StringBuilder buff = new StringBuilder(mailbox);
        appendTag(buff, isCustomDomain() ? DEFAULT_MAIL_TAG : tag);
        buff.append(MAIL_SEPARATOR).append(domain);
        return buff.toString();
    }

    public boolean isCustomDomain() {
        return ! DEFAULT_MAIL_DOMAIN.equals(domain);
    }

    private boolean isPlusDomain() {
        final Flags.Mail flag = getFlags().getMail();
        return flag == Flags.Mail.PLUS || (flag == Flags.Mail.DETECT && PLUS_DOMAINS.contains(domain));
    }

    private boolean isDotDomain() {
        final Flags.Mail flag = getFlags().getMail();
        return flag == Flags.Mail.DOT || (flag == Flags.Mail.DETECT && DOT_DOMAINS.contains(domain));
    }

    private boolean isTagCapableDomain() {
        return mailtagSeparator != 0;
    }

    private void appendTag(final StringBuilder buffer, final String tag) {
        if ( isTagCapableDomain() && mailtag == null ) {
            buffer.append(mailtagSeparator).append(tag);
        }
    }

    private int randomBetween(final int min, final int max) {
        return fairy.baseProducer().randomBetween(min, max);
    }

    public String randomHashidBetween(final int min, final int max) {
        return Hashes.hashids(randomBetween(min, max));
    }
}
