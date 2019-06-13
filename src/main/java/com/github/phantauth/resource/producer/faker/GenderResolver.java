package com.github.phantauth.resource.producer.faker;

import com.github.phantauth.exception.ConfigurationException;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

// quick & dirty

public class GenderResolver {
    public enum Gender {
        MALE, FEMALE, UNKNOWN;
        public String toProperty() {
            return name().toLowerCase();
        }
    }

    private static final List<Set<String>> femaleNames;
    private static final List<Set<String>> maleNames;
    private static final String FILENAME_FORMAT = "%s_%s.txt";

    static {
        femaleNames = ImmutableList.of(load("female", "en"), load("female", "hu"));
        maleNames = ImmutableList.of(load("male", "en"), load("male", "hu"));
    }

    private static Set<String> load(final String basename, final String locale) {
        try {
            return Resources.readLines(Resources.getResource(GenderResolver.class, String.format(FILENAME_FORMAT, basename, locale)), StandardCharsets.UTF_8).stream().map(GenderResolver::unaccent).collect(Collectors.toSet());
        } catch (IOException e) {
            throw new ConfigurationException("missing gender resolver resource(s)");
        }
    }

    static Gender getGender(final String givenName, final String familyName) {
        final Gender genderGiven = getGender(givenName);
        return genderGiven == Gender.UNKNOWN ? getGender(familyName) : genderGiven;
    }

    private static String unaccent(final String src) {
        return Normalizer.normalize(src, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    private static Gender getGender(final String givenName) {
        String key = unaccent(givenName.toLowerCase());
        for(Set<String> names : maleNames) {
            if (names.contains(key)) {
                return Gender.MALE;
            }
        }
        for(Set<String> names : femaleNames) {
            if (names.contains(key)) {
                return Gender.FEMALE;
            }
        }

        return Gender.UNKNOWN;
    }
}
