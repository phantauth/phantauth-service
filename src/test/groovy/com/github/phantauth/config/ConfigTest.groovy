package com.github.phantauth.config

import spock.lang.Specification

class ConfigTest extends Specification {

    def "test defaults"() {
        String domain = "localtest.me"

        when:
        Config config =  new Config.Builder().setDomain(domain).setStandalone(false).build()

        then:
        config.getPort() != 0
        domain == config.domain
        "https://" + domain == config.serviceURI
        "https://default." + domain == config.defaultTenantURI
        "https://www." + domain == config.developerPortalURI
    }

    def "test lite"() {
        when:
        Config config = new Config.Builder().setStandalone(true).build()

        then:

        "http://127.0.0.1:${config.port}" == config.serviceURI
    }
}
