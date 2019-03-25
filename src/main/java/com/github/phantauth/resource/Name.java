package com.github.phantauth.resource;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.function.Supplier;

@Value.Immutable
@Value.Modifiable
public abstract class Name {
    private static final String EMPTY_STRING = "";

    public static final Name EMPTY = Name.parse(EMPTY_STRING);

    public static final char INSTANCE_SEPARATOR = '~';
    public static final char AUTHORITY_SEPARATOR = '@';

    public abstract String getSubject();
    public abstract String getAuthority();
    public abstract String getUserInfo();
    public abstract String getInstance();
    public abstract String getHost();
    public abstract Flags getFlags();
    public abstract boolean isHostEmpty();
    public abstract boolean isUserInfoEmpty();
    public abstract boolean isAuthorityEmpty();
    public abstract boolean isInstanceEmpty();
    public abstract String getRaw();

    public static Name parse(final String input) {
        return new Builder(input).build();
    }

    public Name ensureAuthority(final Supplier<String> newAuthority) {
        return isAuthorityEmpty() ? new Builder(newAuthority.get() + Flags.DELIMITER + getFlags().format()).build() : this;
    }

    public Name ensureHost(final Supplier<String> newHost) {
        return isHostEmpty() ? new Builder( getAuthority() + AUTHORITY_SEPARATOR + newHost.get() + Flags.DELIMITER + getFlags().format()).build() : this;
    }

    public Name ensureUserInfo(final Supplier<String> newUserInfo) {
        return isUserInfoEmpty() ? new Builder( newUserInfo.get() + getAuthority() + Flags.DELIMITER + getFlags().format()).build() : this;
    }

    public static class Builder extends NameValue.BuilderBase {

        Builder() {
        }

        Builder(@Nullable final String input) {
            int idx;

            idx = input == null ? -1 : input.indexOf(Flags.DELIMITER);

            final String flagsPart = after(input, idx);
            final Flags flags = Flags.Builder.parse(flagsPart);
            setFlags(flags);

            final String authorityPart = before(input, idx);

            final String authority = StringUtils.stripAccents((Strings.isNullOrEmpty(authorityPart) ? EMPTY_STRING : authorityPart).toLowerCase()).replace(' ', '.');
            setAuthority(authority);
            setAuthorityEmpty(Strings.isNullOrEmpty(authority));

            setSubject(authority + (flags.isDefault() ? EMPTY_STRING : (Flags.DELIMITER + flags.format())));

            idx = authority.indexOf(INSTANCE_SEPARATOR);

            final String userpart = before(authority, idx);
            final String instance = after(authority, idx);
            setInstance(instance);
            setInstanceEmpty(Strings.isNullOrEmpty(instance));

            idx = userpart.indexOf(AUTHORITY_SEPARATOR);
            final String userinfo = before(userpart, idx);
            setUserInfo(userinfo);
            setUserInfoEmpty(Strings.isNullOrEmpty(userinfo));
            final String host = after(userpart, idx);
            setHost(host);
            setHostEmpty(Strings.isNullOrEmpty(host));
            setRaw(input == null ? EMPTY_STRING : input);
        }

        static String before(final String input, final int separatorIndex) {
            if ( separatorIndex < 0 ) {
                return input;
            }
            return separatorIndex == 0 ? EMPTY_STRING : input.substring(0, separatorIndex);
        }

        static String after(final String input, final int separatorIndex) {
            if ( separatorIndex < 0 || separatorIndex + 1 > input.length() ) {
                return EMPTY_STRING;
            }
            return input.substring(separatorIndex + 1);
        }
    }
}
