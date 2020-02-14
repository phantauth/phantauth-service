package com.github.phantauth.resource.producer

import com.github.phantauth.core.Domain
import com.github.phantauth.resource.Name
import com.github.phantauth.resource.ResourceModule
import com.github.phantauth.test.ServiceLocator
import com.github.phantauth.test.TestModule
import org.junit.ClassRule
import spock.lang.Shared
import spock.lang.Specification

class DNSDomainProducerTest extends Specification {

    @Shared @ClassRule ServiceLocator locator

    def "test default domain"() {
        URI serviceURI = new URI("http://example.com")
        URI portalURI = new URI("http://www.example.com")

        DNSDomainProducer producer = new DNSDomainProducer(serviceURI, portalURI, new ResourceModule.TxtMapper(new TestModule.TestTxtResolver(locator.config)), locator.tenantRepository)

        when:
        Domain domain = producer.get(locator.tenantRepository.defaultTenant, Name.EMPTY)

        then:
        domain != null

    }
}
