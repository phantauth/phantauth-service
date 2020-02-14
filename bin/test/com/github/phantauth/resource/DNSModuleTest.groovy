package com.github.phantauth.resource

import com.github.phantauth.config.Config
import spock.lang.Specification

class DNSModuleTest  extends Specification {

    def "test lite resolver"() {

        setup:

        Config config = new Config.Builder().setStandalone(true).build()
        DNSModule.StandaloneTxtResolver resolver = new DNSModule.StandaloneTxtResolver(config)

        when:

        Set result = resolver.apply("foo")

        then:

        ! result.empty
        result.contains("name=PhantAuth Lite")
    }
}
