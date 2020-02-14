package com.github.phantauth.service.rest

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.phantauth.core.Client
import com.github.phantauth.core.TokenKind
import com.github.phantauth.resource.Endpoint
import com.github.phantauth.test.ServiceLocator
import com.nimbusds.oauth2.sdk.http.HTTPRequest
import com.nimbusds.oauth2.sdk.http.HTTPResponse
import org.junit.ClassRule
import spock.lang.Shared
import spock.lang.Specification

class ClientServletTest extends Specification {

    @Shared @ClassRule ServiceLocator locator

    @Shared ObjectMapper mapper

    @Shared
    URL endpoint

    def setupSpec() {
        endpoint = Endpoint.CLIENT.toURI(locator.config.serviceURI).toURL()
        mapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

    def "test get"() {
        HTTPRequest request = new HTTPRequest(HTTPRequest.Method.GET, endpoint)

        when:
        HTTPResponse response = request.send()

        then:
        response.statusCode == 200

        when:
        Client client = mapper.readValue(response.content, Client.class)

        then:
        client.clientId != null
    }

    def "test get profile"() {
        HTTPRequest request = new HTTPRequest(HTTPRequest.Method.GET, new URL(endpoint.toString() + "/dummy/profile"))

        when:
        HTTPResponse response = request.send()

        then:
        response.statusCode == 200
        response.contentType.baseType == "text/html"
    }

    def "test get legal"() {
        HTTPRequest request
        HTTPResponse response

        when:
        request = new HTTPRequest(HTTPRequest.Method.GET, new URL(endpoint.toString() + "/dummy/tos"))
        response = request.send()

        then:
        response.statusCode == 200
        response.contentType.baseType == "text/html"

        when:
        request = new HTTPRequest(HTTPRequest.Method.GET, new URL(endpoint.toString() + "/dummy/policy"))
        response = request.send()

        then:
        response.statusCode == 200
        response.contentType.baseType == "text/html"
    }

    def "test get token"(TokenKind kind) {
        HTTPRequest request = new HTTPRequest(HTTPRequest.Method.GET, new URL(endpoint.toString() + '/dummy/token/' + kind.name() ))

        when:
        HTTPResponse response = request.send()

        then:
        response.statusCode == 200
        response.content.length() > 10

        where:
        kind     | _
        TokenKind.REGISTRATION | _
        TokenKind.PLAIN | _
        TokenKind.SELFIE | _
    }
}
