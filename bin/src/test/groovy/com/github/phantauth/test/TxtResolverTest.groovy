package com.github.phantauth.test

import com.github.phantauth.core.Tenant
import org.junit.ClassRule
import spock.lang.Shared
import spock.lang.Specification

class TxtResolverTest extends Specification {

    @Shared @ClassRule ServiceLocator locator

    def "test resolver"() {
        when:
        Tenant tenant = locator.tenantRepository.get("csv")

        then:
        tenant.name == "CSV Test"
    }
}
