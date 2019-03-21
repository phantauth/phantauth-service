package com.github.phantauth.resource.producer;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Ints;
import org.hashids.Hashids;

public class Hashes {
    private static final int HASHIDS_MIN_LENGTH = 8;

    @SuppressWarnings("UnstableApiUsage")
    public static int hashSeed(final String value) {
        Preconditions.checkNotNull(value);

        return Hashing.farmHashFingerprint64().hashString(value, Charsets.UTF_8).asInt();
    }

    @SuppressWarnings("UnstableApiUsage")
    public static String shortHash(final String value) {
        return BaseEncoding.base32().omitPadding().encode(Ints.toByteArray(Hashing.farmHashFingerprint64().hashString(value, Charsets.UTF_8).asInt()));
    }

    @SuppressWarnings({"deprecation", "UnstableApiUsage"})
    public static String md5(final String value) {
        return Hashing.md5().hashString(value, Charsets.UTF_8).toString();
    }

    @SuppressWarnings({"deprecation", "UnstableApiUsage"})
    public static String md5Base64Url(final String value) {
        return BaseEncoding.base64Url().omitPadding().encode(Hashing.md5().hashString(value, Charsets.UTF_8).asBytes());
    }

    @SuppressWarnings("UnstableApiUsage")
    public static String sha256(final String value) {
        return Hashing.sha256().hashString(value, Charsets.UTF_8).toString();
    }

    public static String hashids(final int value) {
        return new Hashids(null, HASHIDS_MIN_LENGTH).encode(value);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static String farmhash(final String value) {
        return Hashing.farmHashFingerprint64().hashString(value, Charsets.UTF_8).toString();
    }

    @SuppressWarnings("UnstableApiUsage")
    public static String farmhashBase64Url(final String value) {
        return BaseEncoding.base64Url().omitPadding().encode(Hashing.farmHashFingerprint64().hashString(value, Charsets.UTF_8).asBytes());
    }
}
