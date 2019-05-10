package com.github.phantauth.resource.producer

import com.github.phantauth.core.Tenant
import com.github.phantauth.resource.Name
import com.github.phantauth.resource.ResourceModule
import com.github.phantauth.test.ServiceLocator
import com.github.phantauth.test.TestModule
import org.junit.ClassRule
import org.minidns.hla.ResolverApi
import spock.lang.Shared
import spock.lang.Specification

class DNSTenantProducerTest extends Specification {

    @Shared @ClassRule ServiceLocator locator

    def "test default tenant"() {
        URI serviceURI = new URI("http://example.com")
        URI tenantURI = new URI("http://default.example.com")
        URI portalURI = new URI("http://www.example.com")

        when:
        DNSTenantProducer producer = new DNSTenantProducer(serviceURI, tenantURI, portalURI, new ResourceModule.TxtMapper(new TestModule.TestTxtResolver(locator.config)))

        then:
        producer.getIssuer(Name.EMPTY) == serviceURI as String
        producer.getIssuer(Name.parse("default")) == serviceURI as String

        when:
        Tenant tenant = producer.get(null, Name.EMPTY)

        then:
        tenant != null
        tenant.issuer == serviceURI as String
        tenant.website == serviceURI as String
        tenant.template == (tenantURI as String) + '{/resource}'
        tenant.name == "Default"
        tenant.flags == null
        tenant.userinfo == null

        when:
        tenant = producer.get(null, Name.parse(";photo;large"))

        then:
        tenant.sub == "default;photo;large"
        tenant.name == "Default"
        tenant.flags == "photo;large"

        when:
        tenant = producer.get(null, Name.parse("joe@dummy"))

        then:
        tenant.sub == "joe@dummy"
        tenant.userinfo == "joe"
        tenant.name == "Joe"
        tenant.flags == null

        when:
        tenant = producer.get(null, Name.parse("joe@dummy;dice;small"))

        then:
        tenant.sub == "joe@dummy;dice;small"
        tenant.userinfo == "joe"
        tenant.name == "Joe"
        tenant.flags == "dice;small"


        when:
        tenant = producer.get(null, Name.parse("csv"))

        then:
        tenant.sub == "csv"
        tenant.userinfo == null
        tenant.name == "CSV Test"
        tenant.flags == null
        tenant.issuer == "https://example.com"

        /*
        when:
        tenant = producer.get(null, Name.parse("joe@fkrtzZ"))

        then:
        tenant.sub == "joe@fkrtzZ"
        tenant.userinfo == null
        tenant.template.toString().startsWith("https://bitbucket.org")
        tenant.name == null
        tenant.flags == null
*/

    }
}
