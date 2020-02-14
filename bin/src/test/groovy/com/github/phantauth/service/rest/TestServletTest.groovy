package com.github.phantauth.service.rest

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.phantauth.core.TokenKind
import com.github.phantauth.core.User
import com.github.phantauth.exception.RequestMethodException
import com.github.phantauth.resource.Endpoint
import com.github.phantauth.test.ServiceLocator
import com.nimbusds.oauth2.sdk.http.HTTPRequest
import com.nimbusds.oauth2.sdk.http.HTTPResponse
import org.junit.ClassRule
import spock.lang.Shared
import spock.lang.Specification

class TestServletTest extends Specification {

    @Shared @ClassRule ServiceLocator locator

    @Shared ObjectMapper mapper

    @Shared
    URL endpoint

    def setupSpec() {
        endpoint = Endpoint.TEST.toURI(locator.config.serviceURI).toURL()
    }

    def "test get"() {
        HTTPRequest request
        HTTPResponse response

        when:
        request = new HTTPRequest(HTTPRequest.Method.GET, endpoint)
        response = request.send()

        then:
        response.statusCode == 200
        response.contentType.baseType == "text/html"

        when:
        request = new HTTPRequest(HTTPRequest.Method.GET, new URL(endpoint.toString() + "/user"))
        response = request.send()

        then:
        response.statusCode == 200
        response.contentType.baseType == "text/html"

        when:
        request = new HTTPRequest(HTTPRequest.Method.GET, new URL(endpoint.toString() + "/oidc"))
        response = request.send()

        then:
        response.statusCode == 200
        response.contentType.baseType == "text/html"
    }

    def "test post"() {
        when:
        HTTPResponse response = new HTTPRequest(HTTPRequest.Method.POST, endpoint).send()

        then:
        response.statusCode == 500
    }
}
