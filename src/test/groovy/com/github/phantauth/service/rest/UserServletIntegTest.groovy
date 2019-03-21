package com.github.phantauth.service.rest

import com.github.phantauth.resource.Endpoint
import com.github.phantauth.test.ServiceLocator
import groovyx.net.http.RESTClient
import org.junit.ClassRule
import spock.lang.Shared
import spock.lang.Specification

class UserServletIntegTest extends Specification {

    @Shared @ClassRule ServiceLocator locator = new ServiceLocator()

    @Shared
    RESTClient rest = RESTClient.newInstance(locator[Endpoint.USER])

    def "Generate random user"() {

        def user

        when:
        user = rest.get([:]).data
        then:
        user != null
    }

    def "user profile"() {

    }

}
