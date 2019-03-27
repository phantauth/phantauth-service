package com.github.phantauth.config

import spock.lang.Specification

class ConfigTest extends Specification {

    def "test defaults"() {
        String domain = System.getenv("PHANTAUTH_DOMAIN")
        if ( domain == null ) {
            domain = Config.DEFAULT_DOMAIN
        }

        when:
        Config config = new Config() {}

        then:
        config.getPort() != 0
        domain == config.domain
        "https://" + domain == config.serviceURI
        "https://default." + domain == config.defaultTenantURI
        "https://www." + domain == config.developerPortalURI
    }
}
