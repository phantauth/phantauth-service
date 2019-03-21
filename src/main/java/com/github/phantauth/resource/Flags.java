package com.github.phantauth.resource;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.immutables.value.Value;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Value.Immutable
@Value.Modifiable
@SuppressWarnings({"UnusedReturnValue", "SpellCheckingInspection"})
public abstract class Flags {

    public static final char DELIMITER = ';';
    static final String EMPTY_STRING = "";

    public static final Flags EMPTY = Flags.Builder.parse(EMPTY_STRING);

    @SuppressWarnings("unused")
    public interface Flag {
        default String format() {
            return format(true);
        }

        boolean isDefault();

        String name();

        default StringBuilder append(final StringBuilder builder) {
            if ( ! isDefault() ) {
                if (builder.length() > 0) {
                    builder.append(DELIMITER);
                }
                builder.append(name().toLowerCase());

            }
            return builder;
        }

        default String format(final boolean includeDefault) {
            return includeDefault || ! isDefault() ? name().toLowerCase() : EMPTY_STRING;
        }
    }

    public enum Mail implements Flag {
        NOEMAIL, DETECT, PLUS, DOT;

        public static final Mail DEFAULT = Mail.DETECT;

        @Override public boolean isDefault() {
            return this == DEFAULT;
        }

        static boolean parse(final String value, final Consumer<Mail> consumer) {
            final Mail parsed = REVERSE.get(value);
            if (parsed != null) {
                consumer.accept(parsed);
                return true;
            }
            return false;
        }

        private static final Map<String, Mail> REVERSE = Arrays.stream(Mail.values()).collect(Collectors.toMap(Mail::format, value -> value));
    }

    public enum Avatar implements Flag {
        NOAVATAR, AI, SKETCH, PHOTO, DICE, KITTEN, ADORABLE, NOTFOUND, MP, IDENTICON, MONSTERID, WAVATAR, RETRO, ROBOHASH, BLANK;

        public static final Avatar DEFAULT = Avatar.AI;

        @Override public boolean isDefault() {
            return this == DEFAULT;
        }

        static boolean parse(final String value, final Consumer<Avatar> consumer) {
            final Avatar parsed = REVERSE.get(value);
            if (parsed != null) {
                consumer.accept(parsed);
                return true;
            }
            return false;
        }

        private static final Map<String, Avatar> REVERSE = Arrays.stream(Avatar.values()).collect(Collectors.toMap(Avatar::format, value -> value));
    }

    public enum Logo implements Flag {
        NOLOGO, ICON, FRACTAL;

        static final Logo DEFAULT = Logo.ICON;

        @Override public boolean isDefault() {
            return this == DEFAULT;
        }

        static boolean parse(final String value, final Consumer<Logo> consumer) {
            final Logo parsed = REVERSE.get(value);
            if (parsed != null) {
                consumer.accept(parsed);
                return true;
            }
            return false;
        }

        private static final Map<String, Logo> REVERSE = Arrays.stream(Logo.values()).collect(Collectors.toMap(Logo::format, value -> value));
    }

    public enum Gender implements Flag {
        NOGENDER, GUESS, MALE, FEMALE;

        public static final Gender DEFAULT = Gender.GUESS;

        @Override public boolean isDefault() {
            return this == DEFAULT;
        }

        static boolean parse(final String value, final Consumer<Gender> consumer) {
            final Gender parsed = REVERSE.get(value);
            if (parsed != null) {
                consumer.accept(parsed);
                return true;
            }
            return false;
        }

        private static final Map<String, Gender> REVERSE = Arrays.stream(Gender.values()).collect(Collectors.toMap(Gender::format, value -> value));
    }

    public enum Size implements Flag {
        TINY(5), SMALL(10), MEDIUM(25), LARGE(50), HUGE(100);

        @Getter
        private final int limit;

        public static final Size DEFAULT = Size.TINY;

        Size(final int limit) {
            this.limit = limit;
        }

        @Override public boolean isDefault() {
            return this == DEFAULT;
        }

        static boolean parse(final String value, final Consumer<Size> consumer) {
            final Size parsed = REVERSE.get(value);
            if (parsed != null) {
                consumer.accept(parsed);
                return true;
            }
            return false;
        }

        private static final Map<String, Size> REVERSE = Arrays.stream(Size.values()).collect(Collectors.toMap(Size::format, value -> value));
    }

    @Value.Default
    public Mail getMail() {
        return Mail.DEFAULT;
    }

    @Value.Default
    public Avatar getAvatar() {
        return Avatar.DEFAULT;
    }

    @Value.Default
    public Logo getLogo() {
        return Logo.DEFAULT;
    }

    @Value.Default
    public Gender getGender() {
        return Gender.DEFAULT;
    }

    @Value.Default
    public Locale getLocale() {
        return Locale.US;
    }

    @Value.Default
    public Size getSize() {
        return Size.DEFAULT;
    }

    public String format() {
        return format(true);
    }

    public String format(final boolean includeDefault) {
        if ( isDefault() && ! includeDefault ) {
            return EMPTY_STRING;
        }

        final StringBuilder buff = new StringBuilder();

        getLogo().append(buff);
        getAvatar().append(buff);
        getGender().append(buff);

        if (getLocale() != Locale.US) {
            if (buff.length() > 0) {
                buff.append(DELIMITER);
            }
            buff.append(getLocale());
        }

        getMail().append(buff);

        getSize().append(buff);

        return buff.toString();
    }

    @Value.Derived
    public boolean isDefault() {
        return getMail().isDefault() && getAvatar().isDefault() && getGender().isDefault() && getLocale() == Locale.US && getLogo().isDefault() && getSize().isDefault();
    }

    public static Flags parse(final String value) {
        return Builder.parse(value);
    }

    public static class Builder extends FlagsValue.Builder {
        private static final Map<String, Locale> LOCALES;

        static {
            final ImmutableMap.Builder<String, Locale> builder = ImmutableMap.builder();
            for (Locale locale : Locale.getAvailableLocales()) {
                builder.put(locale.toString(), locale);
            }
            LOCALES = builder.build();
        }

        static Flags parse(final String value) {
            final Builder builder = new Builder();
            if (Strings.isNullOrEmpty(value)) {
                return Flags.EMPTY == null ? builder.build() : Flags.EMPTY;
            }

            final StringTokenizer tokenizer = new StringTokenizer(value, String.valueOf(DELIMITER));
            while (tokenizer.hasMoreTokens()) {
                final String token = tokenizer.nextToken();

                if (LOCALES.containsKey(token)) {
                    builder.setLocale(LOCALES.get(token));
                    continue;
                }

                if (Mail.parse(token, builder::setMail)) {
                    continue;
                }

                if (Avatar.parse(token, builder::setAvatar)) {
                    continue;
                }

                if (Gender.parse(token, builder::setGender)) {
                    continue;
                }

                if ( Logo.parse(token, builder::setLogo) ) {
                    continue;
                }

                if ( Size.parse(token, builder::setSize) ) {
                    continue;
                }
            }
            return builder.build();
        }
    }
}
