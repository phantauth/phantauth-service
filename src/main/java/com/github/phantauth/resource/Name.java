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

    public static class Builder extends NameValue.Builder {

        Builder() {
        }

        Builder(@Nullable final String input) {
            int idx;

            idx = input == null ? -1 : input.indexOf(Flags.DELIMITER);

            final String flagsPart = idx < 0 ? null : idx + 1 > input.length() ? null : input.substring(idx + 1);
            final Flags flags = Flags.Builder.parse(flagsPart);
            setFlags(flags);

            final String authorityPart = idx < 0 ? input : idx == 0 ? null : input.substring(0, idx);

            final String authority = StringUtils.stripAccents((authorityPart == null ? EMPTY_STRING : authorityPart).toLowerCase()).replace(' ', '.');
            setAuthority(authority);
            setAuthorityEmpty(Strings.isNullOrEmpty(authority));

            setSubject(authority + (flags.isDefault() ? EMPTY_STRING : (Flags.DELIMITER + flags.format())));

            idx = authority.indexOf(INSTANCE_SEPARATOR);

            String userpart = idx < 0 ? authority : idx == 0 ? EMPTY_STRING : authority.substring(0, idx);
            final String instance = idx < 0 || idx + 0 > authority.length() ? EMPTY_STRING : authority.substring(idx + 1);
            setInstance(instance);
            setInstanceEmpty(Strings.isNullOrEmpty(instance));

            idx = userpart.indexOf(AUTHORITY_SEPARATOR);
            final String userinfo = idx < 0 ? userpart : idx == 0 ? EMPTY_STRING : userpart.substring(0, idx);
            setUserInfo(userinfo);
            setUserInfoEmpty(Strings.isNullOrEmpty(userinfo));
            final String host = idx < 0 ? EMPTY_STRING : idx + 1 > userpart.length() ? EMPTY_STRING : userpart.substring(idx + 1);
            setHost(host);
            setHostEmpty(Strings.isNullOrEmpty(host));
            setRaw(input == null ? EMPTY_STRING : input);
        }
    }
}
