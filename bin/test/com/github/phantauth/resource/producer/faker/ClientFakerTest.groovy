package com.github.phantauth.resource.producer.faker

import com.github.phantauth.core.Client
import com.github.phantauth.core.Tenant
import com.github.phantauth.core.TenantBean
import spock.lang.*

class ClientFakerTest extends Specification {
    ClientFaker faker
    Tenant tenant;

    void setup() {
        tenant = new TenantBean(sub: "tenant", issuer: "http://localhost")

        faker = new ClientFaker()
    }

    def "test newClient: properties"() {
        Client client
        when:
        client = faker.get(tenant, "dummy_client_id")

        then:
        client != null
        client.clientId == "dummy_client_id"
        client.clientName.length() > 1
        client.clientSecret.length() == 8
        client.clientUri.startsWith(tenant.issuer as String)
        client.clientUri.indexOf(client.clientId) > 0
        client.softwareId.length() == 22 // base64url of md5
        client.softwareVersion.length() > 1
        client.logoUri.startsWith("https://www.gravatar.com/avatar")
        client.policyUri == "${tenant.issuer}/client/dummy_client_id/policy"
        client.tosUri == "${tenant.issuer}/client/dummy_client_id/tos"
    }

    def "test newClient: deterministic"() {
        Client client
        when:
        client = faker.get(tenant,"dummy_client_id")

        then:
        faker.get(tenant,"dummy_client_id") == client
        where:
        i << (1..10)
    }
}
