package com.github.phantauth.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@Value.Immutable
@JsonDeserialize(builder = Config.Builder.class)
public abstract class Config {
    private static final String LOCAL_HOST = "local";
    private static final String LOCAL_IP = "127";

    private static final String ENV_SERVICE_URI = "PHANTAUTH_SERVICE_URI";
    private static final String ENV_DEFAULT_TENANT_URI = "PHANTAUTH_DEFAULT_TENANT_URI";
    private static final String ENV_DEVELOPER_PORTAL_URI = "PHANTAUTH_DEVELOPER_PORTAL_URI";
    private static final String ENV_PORT = "PHANTAUTH_SERVICE_PORT";
    private static final String ENV_PORT_HEROKU = "PORT";
    private static final String ENV_SERVICE_KEYS = "PHANTAUTH_SERVICE_KEYS";
    private static final String ENV_TTL = "PHANTAUTH_TTL";
    private static final String ENV_TENANT_DOMAIN = "PHANTAUTH_TENANT_DOMAIN";

    private static final String DEFAULT_SERVICE_URI = "https://phantauth.me";
    private static final String DEFAULT_DEFAULT_TENANT_URI = "https://default.phantauth.me";
    private static final String DEFAULT_DEVELOPER_PORTAL_URI = "https://www.phantauth.me";
    private static final String DEFAULT_PORT = "8080";
    private static final String DEFAULT_SERVICE_KEYS = "eyJhbGciOiJub25lIn0K.eyJrZXlzIjpbeyJrdHkiOiJvY3QiLCJ1c2UiOiJzaWciLCJraWQiOiJodHRwczovL2V4YW1wbGUuY29tL2ZIMXBvcTdVRWVpUDJIZDA0aG9IN0EiLCJrIjoibmZMbXVtd25nRUtYajNlNk1fV1lBUmFkcGJkS0VJMGdVRGFfMUN4Mzl6WSIsImFsZyI6IkhTMjU2In0seyJrdHkiOiJvY3QiLCJ1c2UiOiJlbmMiLCJraWQiOiJodHRwczovL2V4YW1wbGUuY29tL2tBZXBWcTdVRWVpVzQ2Y2tVTkxDU3ciLCJrIjoiZVhiN3ltVHBvSTJzUUxVS1NKQUZJeVFYSU5HWThPaTFZX2drZk8tQ1NJYyIsImFsZyI6IkhTMjU2In0seyJrdHkiOiJSU0EiLCJkIjoiRm5TQ1ZBWUNtN0dqU2xKSGM1VHlYRVZRN3g4S2U3SjZFNU5qdFVYMUZPQS1MNWhzX0NFMTFrVmNJLVlTZndDY2U5MVVlSjZLTHpCUDd3SkVwM2VodjN6TThLTjFpQmNrM21oclBuajZBeUtzblV2VW85aVNUbjVwYWJYZ3V5d0RlYm5xZlYtaTA1RGdDLVU2WWdLRDNFRVBsckFzU2Q5bGY4Mmh2SFM0SUUxQ3JfVktwNFVVdkM0Q292R1E4S1dJZmgxUnlOZUEtNWxFZ3h1R1QteDhuRFZINmUtclctakd1X2tiaU5EVEM5T2lTVTl3SUV4enNjdWhwLVlHd0tYand5V3MtMmJ4TEkxLUJCanhvNG13MWpaZ05FU3VhY2lXaENSQUJ4TGNzTHhteVAweUFDaUEtWHVvSU9NcmZqM0F6d2dvUXBSVlJQczlFZXNFdHJKaXNRIiwiZSI6IkFRQUIiLCJ1c2UiOiJzaWciLCJraWQiOiJodHRwczovL2V4YW1wbGUuY29tL21jc2FhSzdVRWVpRE5SdjlQY1h6dlEiLCJhbGciOiJSUzI1NiIsIm4iOiJ0TXFQYlZSZUpxQzlFdXRNaTBXQWVvU0E5UTREbThUTl95cGZILUVncThHelFnQWs0RjRvdmc1cXQzdmNic3pvM2plZER0QW9yX0hpQ2VHc0JXc1h2NHVtV2RVcnc0TWN1eHBZQml2TEN0Y2JqSWZzWFlHajR0WmJsYzNCNDNlWGhDSFl1b0FSdG1ZQ3FuOUJBNHowQXF1MXhWN1I1WEpVdzFaNDFRM2JMRlROZHNvelJSbmY1aEhWcFZEQ3BZcDBWRTFJSTlXZExZZ0pXQS13M2tkbjlrbHNpcUpRWHEwRkl0YzNqaTU0eVNuanI0RFdwQVl4VkpUSFdrTWVIa2ZDQW5ES2hNQmhsRUlYUHlDcEUyb01UZ3lOQ2ExY2c3OGFpSUhOUXNYcFVrNzROYWhBdkVWcVJMc2F1SnFXdlBTQ3I3TGQwVnh4T2xYU3pxM1BmMTFVMlEifV19Cg.";
    private static final String DEFAULT_TTL = String.valueOf(TimeUnit.HOURS.toSeconds(1));
    private static final String DEFAULT_TENANT_DOMAIN = "phantauth.cf";

    public static class Builder extends ConfigValue.Builder {
    }

    @Value.Default
    public String getDefaultTenantURI() {
        return getenv(ENV_DEFAULT_TENANT_URI, DEFAULT_DEFAULT_TENANT_URI);
    }

    @Value.Default
    public String getTenantDomain() {
        return getenv(ENV_TENANT_DOMAIN, DEFAULT_TENANT_DOMAIN);
    }

    @Value.Default
    public String getDeveloperPortalURI() {
        return getenv(ENV_DEVELOPER_PORTAL_URI, DEFAULT_DEVELOPER_PORTAL_URI);
    }

    @Value.Default
    public String getServiceURI() {
        return getenv(ENV_SERVICE_URI, DEFAULT_SERVICE_URI);
    }

    @Value.Default
    public int getPort() {
        return Integer.parseInt(getenv(ENV_PORT, getenv(ENV_PORT_HEROKU, DEFAULT_PORT)));
    }

    @Value.Default
    public String getServiceKeys() {
        return getenv(ENV_SERVICE_KEYS, DEFAULT_SERVICE_KEYS);
    }

    @Value.Default
    public long getTTL() {
        return TimeUnit.SECONDS.toMillis(Integer.parseInt(getenv(ENV_TTL, isLocal() ? "0" : DEFAULT_TTL)));
    }

    @Value.Derived
    public boolean isLocal() {
        final String host = URI.create(getServiceURI()).getHost();
        return host.startsWith(LOCAL_HOST) || host.startsWith(LOCAL_IP);
    }

    private String getenv(final String name, final String defaultValue) {
        return System.getenv(name) == null ? defaultValue : System.getenv(name);
    }
}
